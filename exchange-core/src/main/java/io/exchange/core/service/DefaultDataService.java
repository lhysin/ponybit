package io.exchange.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.querydsl.core.BooleanBuilder;

import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactionAdd;
import io.exchange.core.dto.PostDto;
import io.exchange.core.dto.UserDto.ResUser;
import io.exchange.core.hibernate.repository.AdminWalletRepository;
import io.exchange.core.hibernate.repository.CoinRepository;
import io.exchange.core.hibernate.repository.ManualTransactionRepository;
import io.exchange.core.hibernate.repository.PostRepository;
import io.exchange.core.hibernate.repository.UserRepository;
import io.exchange.core.hibernate.repository.UserTokenRepository;
import io.exchange.core.hibernate.repository.WalletRepository;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Active;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.PostType;
import io.exchange.domain.enums.Role;
import io.exchange.domain.enums.Status;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.enums.UserTokenType;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.coin.Coin;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.domain.hibernate.coin.QManualTransaction;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.hibernate.user.User;
import io.exchange.domain.hibernate.user.UserToken;
import io.exchange.domain.util.EnumUtils;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultDataService {

    public final UserRepository userRepository;
    public final UserTokenRepository userTokenRepository;
    public final CoinRepository coinRepository;
    public final WalletRepository walletRepository;
    public final ManualTransactionRepository manualTransactionRepository;
    public final PostRepository postRepository;
    public final AdminWalletRepository adminWalletRepository;

    public final UserService userService;
    public final CoinService coinService;
    public final ManualTransactionService manualTransactionService;
    public final PostService postService;
    public final WalletService walletService;
    public final AdminWalletService adminWalletService;
    public final OtpService otpService;
    public final EmailService emailService;

    public final BCryptPasswordEncoder bCryptPasswordEncoder;

    private LocalDateTime now;

    public void createTestData(LocalDateTime now) {
        this.now = now;

        ResUser adminUser = userService.getUser("test@admin.com");
        ResUser admin2User = userService.getUser("admin@ponybit.io");
        
        ReqManualTransactionAdd req = new ReqManualTransactionAdd();
//        req.setTxId("0xf53f72f97371cc840c86b7330a8ac927e17c56e2fd655ffcca4ee9785ffb7cac");
//        req.setUserId(adminUser.getId());
//        req.setCoinName(CoinName.ETHEREUM);
//        req.setAmount(new BigDecimal("0.305"));
//        req.setAddress("보내는 주소 테스트");
//        this.createManualTransactionIfNotFound(req, Category.DEPOSIT);

        this.createUserIfNotFound("test1@user.com", "user", "name is testUser1", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test2@user.com", "user", "name is testUssr2", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test3@user.com", "user", "name is testUser3", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test4@user.com", "user", "name is testUser4", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test5@user.com", "user", "name is testUser5", Active.Y, Role.ROLE_USER, admin2User.getMyRefCd());
        this.createUserIfNotFound("test6@user.com", "user", "name is testUser6", Active.Y, Role.ROLE_USER, admin2User.getMyRefCd());
        this.createUserIfNotFound("test7@user.com", "user", "name is testUser7", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test8@user.com", "user", "name is testUser8", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("test9@user.com", "user", "name is testUser9", Active.Y, Role.ROLE_USER, adminUser.getMyRefCd());
        this.createUserIfNotFound("user@user.com", "user", "name is user", Active.N, Role.ROLE_USER, null);

        List<User> users = this.userRepository.findAll();
        List<Coin> coins = this.coinRepository.findAll();

        User firstUser = null;
        for(User user : users) {
            if(firstUser == null) {
                firstUser = user;
            }
            Long userId = user.getId();
            if(StringUtils.contains(user.getEmail(), "test") &&
                    StringUtils.contains(user.getEmail(), "@user.com")) {
                continue;
            }
            for (Coin coin : coins) {
                CoinName coinName = coin.getName();
                this.createWalletIfNotFound(userId, coinName);
                if(this.walletService.get(userId, coinName).orElse(null) == null) {
                    continue;
                }

                BooleanBuilder builder = new BooleanBuilder();
                builder.and(QManualTransaction.manualTransaction.coinName.eq(coinName));
                builder.and(QManualTransaction.manualTransaction.userId.eq(userId));
                if(this.manualTransactionRepository.count(builder.getValue()) > 0) {
                    continue;
                };
                for(int i = 0; i < 15; i++) {
                    req = new ReqManualTransactionAdd();
                    req.setTxId(KeyGenUtils.generateTxIdByEthereum());
                    req.setUserId(userId);
                    req.setCoinName(coinName);
                    if(coinName.equals(CoinName.BITCOIN)) {
                        req.setAddress(KeyGenUtils.generateAddressIdByEthereum());
                        req.setAmount(new BigDecimal(KeyGenUtils.generateNumericKey(3, 8)));
                    } else if (coinName.equals(CoinName.ETHEREUM)){
                        req.setAddress(KeyGenUtils.generateAddressIdByEthereum());
                        req.setAmount(new BigDecimal(KeyGenUtils.generateNumericKey(19, 8)));
                    } else if (coinName.equals(CoinName.KRW)){
                        req.setAddress(KeyGenUtils.generateNumericKey(15));
                        req.setAmount(new BigDecimal(KeyGenUtils.generateNumericKey(10)));
                    } else if (coinName.equals(CoinName.PONYCOIN)){
                        req.setAddress(KeyGenUtils.generateAddressIdByEthereum());
                        req.setAmount(new BigDecimal(KeyGenUtils.generateNumericKey(19, 8)));
                    }

                    this.createManualTransactionIfNotFound(req, Category.DEPOSIT);
                }
            }
        }
        for(int i = 0; i < 15; i++) {
            PostDto.ReqAdd postAdd = new PostDto.ReqAdd();
            postAdd.setUserId(firstUser.getId());
            postAdd.setTitle("공지사항 제목" + (i+1));
            postAdd.setContent("<h1><font color=\"red\">공지사항 내용입니다."  + (i+1) + "</font></h1>");
            postAdd.setType(PostType.NOTICE);
            this.postService.add(postAdd);
        }
    }

    public User createUserIfNotFound(String email, String pwd, String name, Active active, Role role, String otherRefCd) {

        User user = userService.get(email).orElse(null);
        if(user == null) {

            user = User.builder()
                    .email(email)
                    .pwd(bCryptPasswordEncoder.encode(pwd))
                    .userLevel(UserLevel.LEVEL1)
                    .role(role)
                    .myRefCd(userService.getRefCodeByEmail(email))
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(now)
                    .name(name)
                    .delDtm(null)
                    .otpHash(otpService.genSecretKey(email + "_" + now))
                    .otherRefCd(otherRefCd)
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
        }

        if(walletService.get(user.getId(), CoinName.PONYCOIN) == null) {
            Wallet ponyWallet = walletService.precreateWallet(user.getId(), CoinName.PONYCOIN);
        }

        return user;
    }

    public void createWalletIfNotFound(Long userId, CoinName coinName) {

        Wallet wallet = this.walletService.get(userId, coinName).orElse(null);

        if (wallet == null) {

            String address = null;
            BigDecimal availableBalance = BigDecimal.ZERO;
            if(CoinName.BITCOIN.equals(coinName)) {
                address = KeyGenUtils.generateAddressIdByEthereum();
                if(NumberCompareUtils.isEqual(userId, 1L)) {
                    availableBalance = new BigDecimal("30.12345678");
                }
            }
            if(CoinName.ETHEREUM.equals(coinName)) {
                if(NumberCompareUtils.isEqual(userId, 1L)) {
                    return;
                }
                address = KeyGenUtils.generateAddressIdByEthereum();
            }
            String depositDvcd = null;
            if (CoinName.KRW.equals(coinName)) {
                depositDvcd = KeyGenUtils.generateNumericKey(8);
                address = KeyGenUtils.generateNumericKey(15);
            }
            if(CoinName.PONYCOIN.equals(coinName)) {
                address = KeyGenUtils.generateAddressIdByEthereum();
            }

            wallet = Wallet.builder()
                    .userId(userId)
                    .coinName(coinName)
                    .regIp(StaticUtils.getCurrentIp())
                    .regDtm(now)
                    .availableBalance(availableBalance)
                    .usingBalance(BigDecimal.ZERO)
                    .todayWithdrawalTotalBalance(BigDecimal.ZERO)
                    .depositDvcd(depositDvcd)
                    .address(address)
                    .build();

            walletRepository.save(wallet);
        }
    }

    private void createManualTransactionIfNotFound(ReqManualTransactionAdd req, Category category) {

        AdminWallet adminWallet = this.adminWalletService.get(req.getCoinName(), WalletType.HOT).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        String fromAddress = req.getAddress();
        String toAddress = adminWallet.getAddress();

        if(EnumUtils.isEqual(Category.WITHDRAWAL, category)) {
            fromAddress = adminWallet.getAddress();
            toAddress = req.getAddress();
        }

        ManualTransaction manualTransation = ManualTransaction.builder()
                .userId(req.getUserId())
                .coinName(req.getCoinName())
                .category(category)
                .status(Status.PENDING)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(now)
                .txId(req.getTxId())
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .reqAmount(req.getAmount())
                .realAmount(req.getAmount())
                .build();

        this.manualTransactionRepository.save(manualTransation);
    }

}
