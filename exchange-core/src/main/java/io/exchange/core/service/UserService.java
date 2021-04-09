package io.exchange.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.TransactionDto;
import io.exchange.core.dto.UserDto.ReqChangePassword;
import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.dto.UserDto.UserWithWallets;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.dto.WalletDto.ResWallet;
import io.exchange.core.hibernate.repository.ManualTransactionRepository;
import io.exchange.core.hibernate.repository.UserRepository;
import io.exchange.core.hibernate.repository.UserTokenRepository;
import io.exchange.core.hibernate.repository.WalletRepository;
import io.exchange.core.util.CustomPageable;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Role;
import io.exchange.domain.enums.Status;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.enums.UserTokenType;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.hibernate.user.QUser;
import io.exchange.domain.hibernate.user.User;
import io.exchange.domain.hibernate.user.UserToken;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@SuppressWarnings("unused")
@RequiredArgsConstructor
public class UserService {

    private final OtpService otpService;
    private final EmailService emailService;
    private final TransactionService transactionService;
    private final WalletService walletService;
    private final AdminWalletService adminWalletService;

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final ManualTransactionRepository manualTransactionRepository;
    private final UserTokenRepository userTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Integer EMAIL_SEND_RESET_SEC = 60;

    public Optional<User> get(Long id) {
        return userRepository.findById(id);
    }
    public Optional<User> get(String email){
        return userRepository.findByEmail(email);
    }

    public ResUser getUser(Long id){
        User user = this.get(id).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS, "user_id:" + id));
        return ModelUtils.map(user, ResUser.class);
    }
    public ResUser getUser(String email){
        User user = this.get(email).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS, "email:" + email));
        return ModelUtils.map(user, ResUser.class);
    }
 
    public CommonDto.ResPage<UserWithWallets> getAllWithWalletsTest() {

        CommonDto.ResPage<UserWithWallets> res = CommonDto.ResPage.<UserWithWallets>builder()
                .list(this.userRepository.getAllGroupByWallets())
                .build();

        return res;
    }

    private Page<ResUser> getAll(Integer page, Integer size){
        QBean<ResUser> userBean = Projections.bean(UserWithWallets.class, QUser.user.id, QUser.user.email, QUser.user.name);
        Page<ResUser> result = userRepository.findAll(userBean, null, CustomPageable.of(page, size));
        return result;
    }

    public CommonDto.ResPage<ResUser> getAllUser(Integer page, Integer size) {

        Page<ResUser> result = this.getAll(page, size);

        CommonDto.ResPage<ResUser> res = CommonDto.ResPage.<ResUser>builder()
                .page(page)
                .size(size)
                .list(result.getContent())
                .totalPages(result.getTotalPages())
                .build();

        return res;
    }

    public List<User> findByOtherRefCdIsMyRefCd(String myRefCd){
        return this.userRepository.findByOtherRefCd(myRefCd);
    }

    public CommonDto.ResPage<UserWithWallets> getAllUserWithWallets(CommonDto.ReqPage req) {

        Integer page = req.getPage();
        Integer size = req.getSize();

        Page<ResUser> result = this.getAll(page, size);

        List<UserWithWallets> userWithWallets = Lists.newArrayList();

        if(!result.getContent().isEmpty()) {

            // getUserIds
            Set<Long> userIds = result.getContent()
                    .stream()
                    .map(resUser -> resUser.getId())
                    .collect(Collectors.toSet());

            // getResWallets
            List<ResWallet> resWallets = walletRepository.getAllWithCoinByUserIds(userIds);

            // user + wallet
            for(ResUser resUser : result.getContent()) {
                UserWithWallets userWithWallet = ModelUtils.map(resUser, UserWithWallets.class);
                userWithWallet.setWallets(Lists.newArrayList());
                for(WalletDto.ResWallet resWallet : resWallets) {
                    if(NumberCompareUtils.isEqual(userWithWallet.getId(), resWallet.getUserId())) {
                        userWithWallet.getWallets().add(ModelUtils.map(resWallet, WalletDto.ResWallet.class));
                    }
                }
                userWithWallets.add(userWithWallet);
            }
        }

        CommonDto.ResPage<UserWithWallets> res = CommonDto.ResPage.<UserWithWallets>builder()
                .page(page)
                .size(size)
                .list(userWithWallets)
                .totalPages(result.getTotalPages())
                .build();

        return res;
    }

    @Transactional
    public User changeUserPassword(ReqChangePassword req) {

        if (!req.getNewPwd().equals(req.getNewConfirmPwd())) {
            throw new BusinessException(Code.INVALID_PASSWORD);
        }

        User existsUser = this.get(req.getUserId()).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS, "user_id:" + req.getUserId()));

        if (existsUser.getDelDtm() != null) {
            throw new BusinessException(Code.USER_NOT_EXISTS);
        }

        if (!bCryptPasswordEncoder.matches(StringUtils.trim(req.getPwd()), StringUtils.trim(existsUser.getPwd()))) {
            throw new BusinessException(Code.INVALID_PASSWORD);
        }

        // change pwd
        existsUser.changePwd(bCryptPasswordEncoder.encode(StringUtils.trim(req.getNewPwd())));

        return existsUser;
    }

    @Transactional
    public User createUserAndToken(String email, String pwd, String refCd) {

        email = StringUtils.trim(email);
        pwd = StringUtils.trim(pwd);
        refCd = StringUtils.trim(refCd);

        User user = this.get(email).orElse(null);
        if(user != null) {
            throw new BusinessException(Code.USER_ALREADY_EXISTS);
        }

        User referralUser = null;
        if(StringUtils.isNotEmpty(refCd)) {
            referralUser = userRepository.findByMyRefCd(refCd).orElse(null);
            if(referralUser == null) {
                refCd = null;
            }
        }

        LocalDateTime now = LocalDateTime.now();
 
        user = User.builder()
            .email(email)
            .pwd(bCryptPasswordEncoder.encode(pwd))
            .userLevel(UserLevel.LEVEL0)
            .role(Role.ROLE_USER)
            .myRefCd(this.getRefCodeByEmail(email))
            .regIp(StaticUtils.getCurrentIp())
            .regDtm(now)
            .delDtm(null)
            .otpHash(otpService.genSecretKey(email + "_" + now))
            .otherRefCd(refCd)
            .build();
        userRepository.save(user);

        // email confirm regist
        String token = KeyGenUtils.generateEmailConfirmNumericKey();
        UserToken userToken = UserToken.builder()
                .userId(user.getId())
                .type(UserTokenType.USER_ACTIVE)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(now)
                .sendDtm(now)
                .token(token)
                .build();

        // email send
        String subject = "Ponybit.io의 회원가입을 축하드립니다.";
        String targetUrl = StaticUtils.getBaseUrl() + "/active#" + userToken.getToken();
        emailService.sendEmailByAdmin(user.getEmail()
                , targetUrl
                , subject
                , now
                , "Ponybit.io에 로그인하기 위해서 계정활성화가 필요합니다."
                , "아래 링크를 클릭하여 계정 활성화를 진행해 주세요.");

        userTokenRepository.save(userToken);

        // precreate ETHEREUM wallet
        walletService.precreateWallet(user.getId(), CoinName.ETHEREUM);
        // precreate PONYCOIN wallet
        Wallet ponyWallet = this.walletService.precreateWallet(user.getId(), CoinName.PONYCOIN);

        this.addPromotionPony(user, CoreConfig.AUTH_PROMOTION_PONY_BALANCE, null, "회원가입");

        if(referralUser != null) {
            this.addPromotionPony(user, CoreConfig.AUTH_PROMOTION_PONY_BALANCE, null, "추천인 입력");
            this.addPromotionPony(referralUser, CoreConfig.REF_PROMOTION_PONY_BALANCE, user.getId(), "추천받음");
        }

        return user;
    }

    @Transactional
    public User userEmailVerificationByToken(String token) {

        token = StringUtils.trim(token);

        UserToken userToken = Optional.ofNullable(userTokenRepository.findOneByToken(token))
                .orElseThrow(() -> new BusinessException(Code.TOKEN_INVALID));
        User existsUser = Optional.ofNullable(userToken.getUser())
                .orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));

        if(existsUser.getUserLevel().getLevel() > UserLevel.LEVEL0.getLevel()){
            throw new BusinessException(Code.USER_ALREADY_EMAIL_VERIFICATION);
        }

        LocalDateTime now = LocalDateTime.now();

        // token expiry check. max 24 hour
        long hours = userToken.getSendDtm().until(now, ChronoUnit.HOURS);
        if(hours > 24) {
            throw new BusinessException(Code.TOKEN_EXPIRED);
        }

        existsUser.changeUserLevel(UserLevel.LEVEL1);
//        this.addPromotionPony(existsUser, CoreConfig.AUTH_PROMOTION_PONY_BALANCE, null, "이메일 인증");

        return existsUser;
    }

    @Transactional
    public User resendTokenByEmail(String email) {

        email = StringUtils.trim(email);

        UserToken existsUserToken = userTokenRepository.findByEmailAndType(email, UserTokenType.USER_ACTIVE)
                .orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));
        User existsUser = Optional.ofNullable(existsUserToken.getUser())
                 .orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));

        if(existsUser.getUserLevel().getLevel() > UserLevel.LEVEL0.getLevel()){
            throw new BusinessException(Code.USER_ALREADY_EMAIL_VERIFICATION);
        }

        LocalDateTime now = LocalDateTime.now();

        // email send is possible for 60 second
        LocalDateTime sendDtm = Optional.ofNullable(existsUserToken.getSendDtm())
                .orElse(LocalDateTime.now());
        long sec = sendDtm.until(now, ChronoUnit.SECONDS);
        if(sec < EMAIL_SEND_RESET_SEC) {
            throw new BusinessException(Code.TOO_MANY_REQUEST, ImmutableMap.of("leftTimeSec", (EMAIL_SEND_RESET_SEC - sec)));
        }

        String newToken = KeyGenUtils.generateEmailConfirmNumericKey();

        // email send
        String subject = "Ponybit.io 계정 활성화 메일입니다.";
        String targetUrl = StaticUtils.getBaseUrl() + "/active#" + existsUserToken.getToken();
        emailService.sendEmailByAdmin(existsUser.getEmail()
                , targetUrl
                , subject
                , now
                , "Ponybit.io에 로그인하기 위해서 계정활성화가 필요합니다."
                , "아래 링크를 클릭하여 계정 활성화를 진행해 주세요.");

        existsUserToken.changeToken(newToken);
        existsUserToken.setSendDtm(now);

        return existsUser;
    }

    @Transactional
    public void userOtherRefCd(Long userId, String otherRefCd) {

        otherRefCd = StringUtils.trim(otherRefCd);

        User user = this.get(userId).orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));

        if(StringUtils.isNotEmpty(user.getOtherRefCd())) {
            throw new BusinessException(Code.REF_ALREADY_EXISTS);
        }
        if(StringUtils.equals(user.getMyRefCd(), otherRefCd)) {
            throw new BusinessException(Code.REF_CD_EQUALS);
        }

        User referralUser = this.userRepository.findByMyRefCd(otherRefCd).orElseThrow(() -> new BusinessException(Code.REF_NOT_EXISTS));

        user.setOtherRefCd(otherRefCd);

        this.addPromotionPony(user, CoreConfig.AUTH_PROMOTION_PONY_BALANCE, null, "추천인 입력");
        this.addPromotionPony(referralUser, CoreConfig.REF_PROMOTION_PONY_BALANCE, user.getId(), "추천받음");
    }

    @Transactional
    public User userResetPwdByEmail(String email) {

        email = StringUtils.trim(email);

        UserToken existsUserToken = userTokenRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));

        LocalDateTime now = LocalDateTime.now();

        // email send is possible for 60 second
        LocalDateTime sendDtm = Optional.ofNullable(existsUserToken.getSendDtm())
                .orElse(LocalDateTime.now());
        long sec = sendDtm.until(now, ChronoUnit.SECONDS);
        if(sec < EMAIL_SEND_RESET_SEC) {
            throw new BusinessException(Code.TOO_MANY_REQUEST, ImmutableMap.of("leftTimeSec", (EMAIL_SEND_RESET_SEC - sec)));
        }

        User existsUser = Optional.ofNullable(existsUserToken.getUser())
                .orElseThrow(() -> new BusinessException(Code.USER_NOT_EXISTS));

        // generate new password.
        String newPwd = KeyGenUtils.generateKey(12);

        // email send
        String subject = "Ponybit.io 비밀번호 초기화 메일입니다.";
        String targetUrl = StaticUtils.getBaseUrl() + "/login";
        emailService.sendEmailByAdmin(
                  existsUser.getEmail()
                , targetUrl
                , subject
                , now
                , "Ponybit.io의 로그인 비밀번호가 초기화 되었습니다."
                , "아래 비밀번호를 이용하여 로그인 시도를 진행해 주세요."
                , "새로운 비밀번호 : " + newPwd);

        existsUser.changePwd(bCryptPasswordEncoder.encode(newPwd));
        existsUserToken.setSendDtm(now);

        existsUser.changeUserLevel(UserLevel.LEVEL1);

        return existsUser;
    }

    public String getRefCodeByEmail(String email) {
        email = StringUtils.trim(email);
        int hashcode = email.hashCode();
        String suffix = null;
        if(hashcode < 0) {
            suffix = String.valueOf("-".hashCode());
            hashcode = Math.abs(hashcode);
        } else {
            suffix = String.valueOf("+".hashCode());
        }
        return String.valueOf(hashcode) + suffix;
    }

    public Long getRefCnt(String myRefCd) {
        return userRepository.countByOtherRefCdAndUserLevel(myRefCd, UserLevel.LEVEL2);
    }

    public Optional<User> getUserByMyRefCd(String otherRefCd) {
        return userRepository.findByMyRefCd(otherRefCd);
    }

    public void createAdminIfNotFound(String email, String pwd) {

        User user = this.get(email).orElse(null);

        if(user == null) {

            LocalDateTime now = LocalDateTime.now();

            user = User.builder()
                    .email(email)
                    .pwd(bCryptPasswordEncoder.encode(pwd))
                    .userLevel(UserLevel.LEVEL1)
                    .role(Role.ROLE_ADMIN)
                    .myRefCd(this.getRefCodeByEmail(email))
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(now)
                    .name("admin")
                    .delDtm(null)
                    .otpHash(otpService.genSecretKey(email + "_" + now))
                    .build();
            userRepository.save(user);

            UserToken userToken = UserToken.builder()
                    .userId(user.getId())
                    .type(UserTokenType.USER_ACTIVE)
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(now)
                    .token(KeyGenUtils.generateEmailConfirmNumericKey())
                    .sendDtm(now.minus(100L, ChronoUnit.SECONDS))
                    .build();

            userTokenRepository.save(userToken);

            // precreate ETHEREUM wallet
            walletService.precreateWallet(user.getId(), CoinName.ETHEREUM);
            // precreate PONYCOIN wallet
            Wallet ponyWallet = this.walletService.precreateWallet(user.getId(), CoinName.PONYCOIN);
        }
    }

    public void addPromotionPony(User user, BigDecimal ponyAmount, Long fromRefUserId, String reason) {

        Wallet ponyWallet = walletService.get(user.getId(), CoinName.PONYCOIN).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        AdminWallet ponyAdminWallet = this.adminWalletService.get(CoinName.PONYCOIN, WalletType.HOT).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        Status status = Status.PENDING;
        if(user.getUserLevel().getLevel() >= UserLevel.LEVEL2.getLevel()){
            status = Status.APPROVAL;
        }

        ManualTransaction manualTransaction = ManualTransaction.builder()
                .userId(user.getId())
                .coinName(CoinName.PONYCOIN)
                .category(Category.PROMOTION)
                .status(status)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .txId(KeyGenUtils.generateTxIdByEthereum())
                .fromAddress(ponyAdminWallet.getAddress())
                .toAddress(ponyWallet.getAddress())
                .reqAmount(ponyAmount)
                .realAmount(ponyAmount)
                .fromRefUserId(fromRefUserId)
                .reason(reason)
                .build();

        this.manualTransactionRepository.save(manualTransaction);

        TransactionDto.ReqTransactionAdd transactionReq = ModelUtils.map(manualTransaction, TransactionDto.ReqTransactionAdd.class);
        this.transactionService.createConfirmTransaction(transactionReq);
    }

    public List<ResUser> getRefCntTop10Users(){
        return this.userRepository.getRefCntTop10Users();
    }
}
