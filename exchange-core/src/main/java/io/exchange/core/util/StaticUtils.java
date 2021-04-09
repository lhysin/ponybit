package io.exchange.core.util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.exchange.core.config.CoreConfig;
import io.exchange.domain.exception.BadRequestException;
import lombok.experimental.UtilityClass;

@SuppressWarnings("unused")
@UtilityClass
public class StaticUtils {

    /**
     * NOT UNIT TESTED Returns the URL (including query parameters) minus the
     * scheme, host, and context path. This method probably be moved to a more
     * general purpose class.
     */
    public static String getRelativeUrl() {

        HttpServletRequest request = StaticUtils.getCurrentRequest();

        String baseUrl = null;

        if ((request.getServerPort() == 80) || (request.getServerPort() == 443)) {
            baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
        } else {
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath();
        }

        StringBuffer buf = request.getRequestURL();

        if (request.getQueryString() != null) {
            buf.append("?");
            buf.append(request.getQueryString());
        }

        return buf.substring(baseUrl.length());
    }

    /**
     * NOT UNIT TESTED Returns the base url (e.g, <tt>http://myhost:8080/myapp</tt>)
     * suitable for using in a base tag or building reliable urls.
     */
    public static String getBaseUrl() {

        HttpServletRequest request = StaticUtils.getCurrentRequest();

        if ((request.getServerPort() == 80) || (request.getServerPort() == 443)) {
            return request.getScheme() + "://" + request.getServerName() + request.getContextPath();
        }  else {
            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
            + request.getContextPath();
        }
    }

    /**
     * Returns the file specified by <tt>path</tt> as returned by
     * <tt>ServletContext.getRealPath()</tt>.
     */
    public static File getRealFile(HttpServletRequest request, String path) {
        return new File(request.getSession().getServletContext().getRealPath(path));
    }

    public static HttpServletRequest getCurrentRequest() {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        if(request == null) {
            throw new BadRequestException("current request is null");
        }

        return request;
    }

    public static String getCurrentIp() {

        try {

            HttpServletRequest request = StaticUtils.getCurrentRequest();

            String ip = request.getHeader("X-Forwarded-For");

            if (StringUtils.isEmpty(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            return StaticUtils.getHostAddress();
        }
    }

    public static String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            return CoreConfig.SERVER_DEFAULT_IP;
        }
    }

    private static HttpHeaders getHeadersInfo() {

        HttpServletRequest request = StaticUtils.getCurrentRequest();
        HttpHeaders httpHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            httpHeaders.add(key, value);
        }
        return httpHeaders;
    }

    // javascript from encodeURIComponent(btoa('encode'))
    public static String decodeBase64(String encode) {
        String returnStr = encode;
        if(StringUtils.isEmpty(encode)) {
            return returnStr;
        }
        try {
            return new String(Base64.getDecoder().decode(returnStr));
        }catch (Exception e) {
            return returnStr;
        }
    }

    public static HttpHeaders getHeadersInfo(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            httpHeaders.add(key, value);
        }
        return httpHeaders;
    }

    public static String yyyyUnderMMUnderddFormatter(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
