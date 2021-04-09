package io.exchange.web.controller.view;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.dto.CoinMarketCapDto.ResCoinMarketCap;
import io.exchange.core.dto.WalletDto;
import io.exchange.core.service.AdminWalletService;
import io.exchange.core.service.CoinMarketCapService;
import io.exchange.core.service.UserService;
import io.exchange.core.service.WalletService;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Code;
import io.exchange.domain.enums.CoinName;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.enums.WalletType;
import io.exchange.domain.exception.BusinessException;
import io.exchange.domain.hibernate.coin.AdminWallet;
import io.exchange.domain.hibernate.user.User;
import io.exchange.domain.util.NumberCompareUtils;
import io.exchange.web.util.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(method = RequestMethod.GET)
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final WalletService walletService;
    private final AdminWalletService adminWalletService;
    private final CoinMarketCapService coinMarketCapService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("refCnt", userService.getRefCnt(loginUser.getMyRefCd()));
        }

        BigDecimal allSoldPonyBalance = walletService.getAllSoldPonyBalance();
        BigDecimal appendixPonyBalance = new BigDecimal("327500000");
        if(NumberCompareUtils.isLT(allSoldPonyBalance, appendixPonyBalance)) {
            model.addAttribute("allSoldPonyBalance", allSoldPonyBalance.add(appendixPonyBalance));
        } else {
            model.addAttribute("allSoldPonyBalance", allSoldPonyBalance);
        }

        model.addAttribute("maxSalePonyBalance", CoreConfig.MAX_SALE_PONY_BALANCE);

        model.addAttribute("homeRefModalNotOpenKey", CoreConfig.HOME_REF_MODAL_NOT_OPEN_KEY);

        return "home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(LoginUser loginUser, Model model) {
        if(loginUser == null) {
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            return "user/login";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(LoginUser loginUser, Model model) {
        if(loginUser == null) {
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            return "user/signup";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public String emailVerify(LoginUser loginUser, Model model) {
        if(loginUser == null) {
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            return "user/active";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public String reset(LoginUser loginUser, Model model) {
        if(loginUser == null) {
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            return "user/reset";
        } else {
            return "redirect:/";
        }
    }

    @RequestMapping(value = "/chgpwd", method = RequestMethod.GET)
    public String chgpwd(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("refCnt", userService.getRefCnt(loginUser.getMyRefCd()));
        } else {
            return "redirect:/login";
        }
        return "user/chgpwd";
    }

    @RequestMapping(value = "/mypage", method = RequestMethod.GET)
    public String mypage(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("refCnt", userService.getRefCnt(loginUser.getMyRefCd()));
        } else {
            return "redirect:/login";
        }
        return "user/mypage";
    }

    @RequestMapping(value = "/refpage", method = RequestMethod.GET)
    public String refpage(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            User user = userService.get(loginUser.getId())
                    .orElseThrow(() -> new BusinessException(Code.BAD_REQUEST));
            String otherRefCd = user.getOtherRefCd();
            model.addAttribute("loginUser", loginUser);
            model.addAttribute("otherRefCd", otherRefCd);
            model.addAttribute("refCnt", userService.getRefCnt(loginUser.getMyRefCd()));
        } else {
            return "redirect:/login";
        }
        return "user/refpage";
    }

    @RequestMapping(value = "/presale", method = RequestMethod.GET)
    public String presale(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            model.addAttribute("baseUrl", StaticUtils.getBaseUrl());
            model.addAttribute("loginUser", loginUser);

            AdminWallet adminWallet = this.adminWalletService.get(CoinName.ETHEREUM, WalletType.HOT)
                    .orElseThrow(() -> new BusinessException(Code.WALLET_NOT_EXISTS));

            WalletDto.ResWallet ponyCoinWallet = this.walletService.getWallet(loginUser.getId(), CoinName.PONYCOIN);
            model.addAttribute("ponyCoinWallet", ponyCoinWallet);
            model.addAttribute("adminWallet", adminWallet);

            ResCoinMarketCap etherMarketCap = this.coinMarketCapService.getCoinMarketCapEtherByRegDtmEqualMidnight();
            model.addAttribute("etherMarketCap", etherMarketCap);
            model.addAttribute("today", LocalDate.now());

            if(CoreConfig.currentPhase.isEqual(Phase.PRD)) {
                model.addAttribute("etherSacnUrl", "https://etherscan.io/tx/");
            } else {
                model.addAttribute("etherSacnUrl", "https://ropsten.etherscan.io/tx/");
            }

            BigDecimal allSoldPonyBalance = walletService.getAllSoldPonyBalance();
            BigDecimal appendixPonyBalance = new BigDecimal("327500000");
            BigDecimal saleAvailPonyBalance = null;
            if(NumberCompareUtils.isLT(allSoldPonyBalance, appendixPonyBalance)) {
                saleAvailPonyBalance = CoreConfig.MAX_SALE_PONY_BALANCE.subtract(allSoldPonyBalance.add(appendixPonyBalance));
            } else {
                saleAvailPonyBalance = CoreConfig.MAX_SALE_PONY_BALANCE.subtract(allSoldPonyBalance);
            }
            if(!NumberCompareUtils.isPositive(saleAvailPonyBalance)) {
                saleAvailPonyBalance = BigDecimal.ZERO;
            }
            model.addAttribute("saleAvailPonyBalance", saleAvailPonyBalance);
            model.addAttribute("ponyKrwPirce", CoreConfig.PONY_KRW_PRICE);

        } else {
            return "redirect:/login";
        }

        return "user/presale";
    }

    @RequestMapping(value = "/continue", method = RequestMethod.GET)
    public String continu(HttpServletRequest request, Model model) {
//        String referer = request.getHeader("referer");
//        if(!StringUtils.contains(referer, "kauth.kakao.com/oauth/authorize")) {
//            throw new AccessDeniedException("Access Denied");
//        }
        return "user/continue";
    }

}
