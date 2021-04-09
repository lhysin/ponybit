package io.exchange.web.controller.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.annotation.RequestQuery;
import io.exchange.core.dto.CommonDto.ResPage;
import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactionAdd;
import io.exchange.core.dto.ManualTransactionDto.ReqManualTransactions;
import io.exchange.core.dto.ManualTransactionDto.ResManualTransaction;
import io.exchange.core.service.ManualTransactionService;
import io.exchange.core.util.ModelUtils;
import io.exchange.domain.hibernate.coin.ManualTransaction;
import io.exchange.web.service.CustomValidationService;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final CustomValidationService customValidationService;
    private final ManualTransactionService manualTransactionService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/manuals")
    @ResponseBody
    public ResponseEntity<Object> getAllResMnualTransactionByReq(@RequestQuery ReqManualTransactions req) {

        customValidationService.validationObject(req);

        ResPage<ResManualTransaction> resManualTransactions
            = this.manualTransactionService.getAllResMnualTransactionByReq(req);

        return StaticWebUtils.defaultSuccessResponseEntity(resManualTransactions);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/manual/deposit")
    @ResponseStatus(HttpStatus.OK)
    public void depositManualTransaction(@RequestBody ReqManualTransactionAdd req) {

        customValidationService.validationObject(req);

        this.manualTransactionService.requiredDeposit(req);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/manual/withdrawl")
    @ResponseStatus(HttpStatus.OK)
    public void withdrawlManualTransaction(@RequestBody ReqManualTransactionAdd req) {

        customValidationService.validationObject(req);

        this.manualTransactionService.requiredWithdrawal(req);
    }
}
