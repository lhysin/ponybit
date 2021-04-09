package io.exchange.core.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;

import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactionAdd;
import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactionEdit;
import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactions;
import io.exchange.core.dto.ManualTransactionDto.ResManualTransaction;
import io.exchange.core.dto.TransactionDto;
import io.exchange.core.hibernate.repository.ManualTransactionRepository;
import io.exchange.core.hibernate.repository.TransactionRepository;
import io.exchange.core.util.CustomPageable;
import io.exchange.core.util.KeyGenUtils;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Category;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Status;
import io.exchange.domain.enums.UserLevel;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.domain.hibernate.coin.QManualTransaction;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.domain.hibernate.user.User;
import io.exchange.domain.util.EnumUtils;
import io.exchange.domain.util.NumberCompareUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@SuppressWarnings("unused")
@Slf4j
@Service
@RequiredArgsConstructor
public class ManualTransactionService {

    private final WalletService walletService;
    private final UserService userService;
    private final AdminWalletService adminWalletService;
    private final TransactionService transactionService;

    private final ManualTransactionRepository manualTransactionRepository;
    private final TransactionRepository transactionRepository;

    public Optional<ManualTransaction> get(Long id, Long userId, CoinName coinName) {
        return this.manualTransactionRepository.findByIdAndUserIdAndAndCoinName(id, userId, coinName);
    }
    public Optional<ManualTransaction> getByTxId(String txId) {
        return  this.manualTransactionRepository.findByTxId(txId);
    }

    private Page<ResManualTransaction> getPagingByReq(Integer page, Integer size, ReqManualTransactions req) {

        QManualTransaction qmt = QManualTransaction.manualTransaction;
        QBean<ResManualTransaction> select = Projections.bean(ResManualTransaction.class, 
                qmt.txId,
                qmt.category,
                qmt.status,
                qmt.coinName,
                qmt.reqAmount,
                qmt.regDtm,
                qmt.completeDtm,
                qmt.reason);

        BooleanBuilder where = new BooleanBuilder();
        where.and(qmt.userId.eq(req.getUserId()).and(qmt.category.ne(Category.PROMOTION)));
        where.or(qmt.userId.eq(req.getUserId()).and(qmt.status.ne(Status.PENDING).and(qmt.category.eq(Category.PROMOTION))));

        if(req.getCategory() != null) {
            where.and(qmt.category.eq(req.getCategory()));
        }

        Page<ResManualTransaction> result = this.manualTransactionRepository.findAll(select, where, CustomPageable.of(page, size), qmt.regDtm.desc());
        return result;
    }

    public ResPage<ResManualTransaction> getAllResMnualTransactionByReq(ReqManualTransactions req) {
        Page<ResManualTransaction> result = this.getPagingByReq(req.getPage(), req.getSize(), req);
        CommonDto.ResPage<ResManualTransaction> res = CommonDto.ResPage.<ResManualTransaction>builder()
                .page(req.getPage())
                .size(req.getSize())
                .list(result.getContent())
                .totalPages(result.getTotalPages())
                .build();
        return res;
    }

    @Transactional
    public void requiredDeposit(ReqManualTransactionAdd req) {

        AdminWallet adminWallet = this.adminWalletService.get(req.getCoinName(), WalletType.HOT).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        ManualTransaction existsManualTransaction = manualTransactionRepository.findByTxIdAndStatusNot(req.getTxId(), Status.CANCEL).orElse(null);
        if(existsManualTransaction != null) {
            throw new BusinessException(Code.TRANSACTION_ALREADY_EXISTS);
        }

        String fromAddress = StringUtils.trim(req.getAddress());
        String toAddress = StringUtils.trim(adminWallet.getAddress());
        String txId = StringUtils.trim(req.getTxId());
        if(StringUtils.isEmpty(txId)) {
            txId = KeyGenUtils.generateTxIdByEthereum();
        }

        ManualTransaction manualTransaction = ManualTransaction.builder()
                .userId(req.getUserId())
                .coinName(req.getCoinName())
                .category(Category.DEPOSIT)
                .status(Status.PENDING)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .txId(txId)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .reqAmount(req.getAmount())
                .build();

        this.manualTransactionRepository.save(manualTransaction);
    }

    @Transactional
    public void requiredWithdrawal(ReqManualTransactionAdd req) {

        ManualTransaction existsManualTransaction = manualTransactionRepository.findByTxIdAndStatusNot(req.getTxId(), Status.CANCEL).orElse(null);
        if(existsManualTransaction != null) {
            throw new BusinessException(Code.TRANSACTION_ALREADY_EXISTS);
        }

        if (NumberCompareUtils.isNotPositiveOrZero(req.getAmount())) {
            throw new BusinessException(Code.AMOUNT_IS_UNDER_ZERO);
        }

        Wallet wallet = this.walletService.get(req.getUserId(), req.getCoinName()).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        if(NumberCompareUtils.isGT(req.getAmount(), wallet.getAvailableBalance())) {
            throw new BusinessException(Code.AVAILABLE_BALANCE_NOT_ENOUGH);
        }

        AdminWallet adminWallet = this.adminWalletService.get(req.getCoinName(), WalletType.HOT).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));
        if(NumberCompareUtils.isLT(adminWallet.getAvailableBalance(), req.getAmount())) {
            throw new BusinessException(Code.NOT_ENOUGH_ADMIN_WALLET);
        }

        wallet.availableBalanceSubtract(req.getAmount());

        String fromAddress = StringUtils.trim(adminWallet.getAddress());
        String toAddress = StringUtils.trim(req.getAddress());
        String txId = StringUtils.trim(req.getTxId());
        if(StringUtils.isEmpty(txId)) {
            txId = KeyGenUtils.generateTxIdByEthereum();
        }

        ManualTransaction manualTransaction = ManualTransaction.builder()
                .userId(req.getUserId())
                .coinName(req.getCoinName())
                .category(Category.WITHDRAWAL)
                .status(Status.PENDING)
                .regIp(StaticUtils.getCurrentIp())
                .regDtm(LocalDateTime.now())
                .txId(txId)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .reqAmount(req.getAmount())
                .build();

        this.manualTransactionRepository.save(manualTransaction);
    }

    @Transactional
    private void editManualTransaction(ReqManualTransactionEdit req, Status status) {

        ManualTransaction manualTransaction =  this.get(req.getId(), req.getUserId(), req.getCoinName())
                .orElseThrow(() -> new BusinessException(Code.TRANSACTION_NOT_EXISTS));

        if(EnumUtils.isNotEqual(manualTransaction.getStatus(), Status.PENDING)) {
            throw new BusinessException(Code.TRANSACTION_IS_INVALID);
        }

        Wallet wallet = this.walletService.get(req.getUserId(), req.getCoinName()).orElse(null);
        if(wallet == null) {
            walletService.precreateWallet(manualTransaction.getUserId(), manualTransaction.getCoinName());
        }

        if(EnumUtils.isEqual(status, Status.APPROVAL)) {

            // TODO RPC Server required.
            String txId = StringUtils.trim(req.getRealTxId());

            if(EnumUtils.isEqual(req.getCategory(), Category.DEPOSIT)) {
                wallet.availableBalanceAdd(manualTransaction.getRealAmount());

                // TODO RPC Server validation.
                if(!StringUtils.equals(txId, manualTransaction.getTxId())) {
                    throw new BusinessException(Code.TRANSACTION_IS_INVALID);
                }

            } else if(EnumUtils.isEqual(req.getCategory(), Category.WITHDRAWAL)) {
                if(!StringUtils.isEmpty(txId)) {
                    throw new BusinessException(Code.TRANSACTION_IS_INVALID);
                } else {
                    manualTransaction.injectTxId(txId);
                }
            }

            TransactionDto.ReqTransactionAdd transationReq = ModelUtils.map(manualTransaction, TransactionDto.ReqTransactionAdd.class);
            transactionService.createConfirmTransaction(transationReq);

        } else if(EnumUtils.isEqual(status, Status.CANCEL)) {
            if(EnumUtils.isEqual(req.getCategory(), Category.DEPOSIT)) {
                
            } else if(EnumUtils.isEqual(req.getCategory(), Category.WITHDRAWAL)) {
                wallet.availableBalanceAdd(manualTransaction.getRealAmount());
            }
        }

        manualTransaction.changeStatus(status);

    }

    public void approvalMunualTransaction(ReqManualTransactionEdit req) {
        this.editManualTransaction(req, Status.APPROVAL);
    }

    public void cancelMunualTransaction(ReqManualTransactionEdit req) {
        this.editManualTransaction(req, Status.CANCEL);
    }

    public List<ManualTransaction> getByCoinNameAndTxidsAndStatusIsPending(CoinName coinName, List<String> txIds){
        return this.manualTransactionRepository.findByCoinNameAndTxIdInAndStatus(coinName, txIds, Status.PENDING);
    }

    @Transactional
    public void changePonyPromotionStatusIsApproval(User user) {

        Wallet ponyWallet = this.walletService.get(user.getId(), CoinName.PONYCOIN).orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

        this.manualTransactionRepository.changePonyPromotionStatusIsApproval(user);
        BigDecimal approvalPonyPromotionBalance = this.manualTransactionRepository.getAllPonyPromotionBalanceByApproval(user);
        ponyWallet.availableBalanceAdd(approvalPonyPromotionBalance);

        User referralUser = userService.getUserByMyRefCd(user.getOtherRefCd()).orElse(null);
        if(referralUser != null) {

            if(referralUser.getUserLevel().getLevel() < UserLevel.LEVEL2.getLevel()) {
                return;
            }

            Wallet referralUserPonyWallet = this.walletService.get(referralUser.getId(), CoinName.PONYCOIN)
                    .orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

            ManualTransaction refMamunalTransciotn = this.manualTransactionRepository.findByUserIdAndCoinNameAndFromRefUserId(referralUser.getId(), CoinName.PONYCOIN, user.getId())
                    .orElseThrow(() -> new BusinessException(Code.MANUAL_TRANSACTION_NOT_EXISTS));

            refMamunalTransciotn.changeStatus(Status.APPROVAL);
            referralUserPonyWallet.availableBalanceAdd(refMamunalTransciotn.getRealAmount());
        }
    }
}
