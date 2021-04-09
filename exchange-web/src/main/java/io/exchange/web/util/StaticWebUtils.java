package io.exchange.web.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.google.gson.Gson;

import io.exchange.core.config.CoreConfig;
import io.exchange.core.util.ModelUtils;
import io.exchange.core.util.StaticUtils;
import io.exchange.domain.enums.Phase;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unused")
@Component
@Slf4j
public class StaticWebUtils {

    public static void defaultHttpServletResponse(HttpServletResponse response, HttpStatus httpStatus) throws IOException {
        ResBody<Object> resBody = ResBody.builder()
                .msg(httpStatus.getReasonPhrase())
                .status(httpStatus.value())
                .build();
        StaticWebUtils.defaultHttpServletResponse(response, httpStatus, resBody);
    }

    public static void defaultHttpServletResponse(HttpServletResponse response, HttpStatus httpStatus, Object body) throws IOException {
        if(!response.isCommitted()) {
            response.setStatus(httpStatus.value());
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.write(ModelUtils.toJsonString(body));
            printWriter.flush();
            printWriter.close();
        }
    }

    public static void defaultExceptionHttpServletResponse(HttpServletResponse response, HttpStatus httpStatus, Object cause) throws IOException {
        if(!response.isCommitted()) {
            response.setStatus(httpStatus.value());
            response.setContentType("application/json");

            ResBody<Object> resBody = ResBody.builder()
                    .msg(httpStatus.getReasonPhrase())
                    .status(httpStatus.value())
                    .cause(cause)
                    .build();

            PrintWriter printWriter = response.getWriter();
            printWriter.write(ModelUtils.toJsonString(resBody));
            printWriter.flush();
            printWriter.close();
        }
    }

    public static ResponseEntity<Object> defaultSuccessResponseEntity(){
        return StaticWebUtils.defaultSuccessResponseEntity(null);
    }

    public static ResponseEntity<Object> defaultSuccessResponseEntity(Object data){

        ResBody<Object> resBody = ResBody.builder()
                .msg(HttpStatus.OK.getReasonPhrase())
                .status(HttpStatus.OK.value())
                .data(data)
                .build();
        return ResponseEntity.ok(resBody);
    }

    public static ResponseEntity<Object> defaultExceptionResponseEntity(HttpStatus httpStatus){
        return StaticWebUtils.defaultExceptionResponseEntity(httpStatus, null);
    }

    public static ResponseEntity<Object> defaultExceptionResponseEntity(HttpStatus httpStatus, Object cause){

        ResBody<Object> resBody = ResBody.builder()
                .msg(httpStatus.getReasonPhrase())
                .status(httpStatus.value())
                .cause(cause)
                .build();

        return ResponseEntity.status(httpStatus).body(resBody);
    }

    public static ModelAndView defaultExceptionModelAndView(HttpStatus httpStatus){
        return StaticWebUtils.defaultExceptionModelAndView(httpStatus, null);
    }

    public static ModelAndView defaultExceptionModelAndView(HttpStatus httpStatus, Object cause){

        /**
         * INTERNAL_SERVER_ERROR case,
         * cause show only local and dev profiles.
         */
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(httpStatus) && cause != null) {
            if(!CoreConfig.currentPhase.isEqual(Phase.LOCAL) &&
                    !CoreConfig.currentPhase.isEqual(Phase.DEV)) {
                cause = null;
            }
        }

        ResBody<Object> resBody = ResBody.builder()
                .msg(httpStatus.getReasonPhrase())
                .status(httpStatus.value())
                .cause(cause)
                .build();

        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setExtractValueFromSingleKeyModel(true);
        view.setPrettyPrint(true);

        ModelAndView mav = new ModelAndView();
        mav.setView(view);
        mav.setStatus(httpStatus);
        mav.addObject(resBody);

        return mav;
    }

    public static LoginUser getCurrentLoginUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LoginUser loginUser = null;
        if (authentication != null) {
            Object obj = authentication.getPrincipal();
            if (obj instanceof LoginUser) {
                loginUser = LoginUser.class.cast(obj);
            }
        }

        return loginUser;
    }

    public static Long getCurrentLoginUserId() {

        Long userId = null;

        LoginUser loginUser = StaticWebUtils.getCurrentLoginUser();
        if(loginUser != null) {
            userId = loginUser.getId();
        }

        return userId;
    }

    public static boolean isAjax(HttpServletRequest request) {
        boolean isAjax = false;
        String requestedWithHeader = request.getHeader("X-Requested-With");
        if(StringUtils.equals(requestedWithHeader, "XMLHttpRequest") ||
                StringUtils.contains(request.getRequestURI(), "api")) {
            isAjax = true;
        }
        return isAjax;
    }

    public static void clearCurrentSessionByRequest() {

        HttpServletRequest req = null;

        try {
            req = StaticUtils.getCurrentRequest();
        } catch (Exception e) {
            log.debug("StaticWebUtils.currentSessionClear Exception: {}", e);
        }

        if(req != null) {

            // if not exists session, non create
            HttpSession session = req.getSession(false);

            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();
        }
    }

    private static SessionRegistry sessionRegistry;

    @SuppressWarnings("static-access")
    @Autowired(required = true)
    public void setModelMapper(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public static LoginUser findLoginUserByUserId(Long userId) {
        return sessionRegistry.getAllPrincipals()
                .stream()
                .filter(principal -> principal instanceof LoginUser)
                .map(LoginUser.class::cast)
                .filter(loginUser -> loginUser.getId() == userId)
                .findFirst()
                .orElse(null);
    }
}
