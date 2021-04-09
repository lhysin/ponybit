package io.exchange.web.controller.view;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.exchange.core.service.PostService;
import io.exchange.web.util.LoginUser;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(method = RequestMethod.GET)
@Slf4j
@RequiredArgsConstructor
public class DashBoardController {
    
    private final PostService postService;

//    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
//    public String dashboard(LoginUser loginUser) {
//        if(loginUser == null) {
//            return "user/login";
//        } else {
//            return "dashboard/dashboard";
//        }
//    }
//
//    @RequestMapping(value = "/transaction", method = RequestMethod.GET)
//    public String transaction(LoginUser loginUser) {
//        if(loginUser == null) {
//            return "user/login";
//        } else {
//            return "dashboard/transaction";
//        }
//    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET)
    public String notice(LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            boolean isAdmin = loginUser.getAuthorities().stream().anyMatch(auth -> StringUtils.equals(auth.getAuthority(), "ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        return "dashboard/notice";
    }

    @RequestMapping(value = "/notice/{postId}", method = RequestMethod.GET)
    public String noticeDetail(@PathVariable("postId") Long postId, LoginUser loginUser, Model model) {
        if(loginUser != null) {
            model.addAttribute("isLogin", true);
            boolean isAdmin = loginUser.getAuthorities().stream().anyMatch(auth -> StringUtils.equals(auth.getAuthority(), "ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        }
        model.addAttribute("postId", postId);
        model.addAttribute("resPost", this.postService.get(postId));
        return "dashboard/noticeDetail";
    }
}
