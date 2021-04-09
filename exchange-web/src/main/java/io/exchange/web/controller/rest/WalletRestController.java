package io.exchange.web.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.exchange.core.annotation.PathObject;
import io.exchange.core.annotation.RequestQuery;
import io.exchange.core.dto.CommonDto;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.service.WalletService;
import io.exchange.core.util.ModelUtils;
import io.exchange.domain.hibernate.coin.Wallet;
import io.exchange.web.service.CustomValidationService;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/wallets")
@Validated
@RequiredArgsConstructor
public class WalletRestController {

    private final WalletService walletService;
    private final CustomValidationService customValidationService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    @ResponseBody
    public ResponseEntity<Object> getWalletsByPaging(@RequestQuery WalletDto.ReqWallets req) {

        customValidationService.validationObject(req);

        CommonDto.ResPage<WalletDto.ResWallet> wallets = walletService.getAllDynamicSearch(req);

        return StaticWebUtils.defaultSuccessResponseEntity(wallets);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}/users/{coinName}/coins")
    @ResponseBody
    public ResponseEntity<Object> getWallets(@PathObject WalletDto.ReqWalletByAdmin req){

        customValidationService.validationObject(req);
 
        WalletDto.ResWallet wallet = walletService.getWallet(req.getUserId(), req.getCoinName());

        return StaticWebUtils.defaultSuccessResponseEntity(wallet);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{coinName}/coins")
    @ResponseBody
    public ResponseEntity<Object> getWalletsByUserId(@PathObject WalletDto.ReqWalletByUser req){

        customValidationService.validationObject(req);

        WalletDto.ResWallet wallet = walletService.getWallet(req.getUserId(), req.getCoinName());

        return StaticWebUtils.defaultSuccessResponseEntity(wallet);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    @ResponseBody
    public ResponseEntity<Object> precreateWallet(@RequestBody WalletDto.ReqAdd req) {

        customValidationService.validationObject(req);

        Wallet wallet = walletService.precreateWallet(req.getUserId(), req.getCoinName());
        log.trace("success put user's wallet={}", wallet);

        WalletDto.ResWallet res = ModelUtils.map(wallet,  WalletDto.ResWallet.class);
        return StaticWebUtils.defaultSuccessResponseEntity(res);
    }
}
