package io.exchange.web.config.resolver;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import io.exchange.core.config.CoreConfig;
import io.exchange.domain.enums.Phase;
import io.exchange.domain.exception.BadParameterException;
import io.exchange.domain.exception.BadRequestException;
import io.exchange.domain.exception.BusinessException;
import io.exchange.web.util.ResBody;
import io.exchange.web.util.StaticWebUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomHandlerExceptionResolver implements HandlerExceptionResolver {

//    private ActionLogService actionLogService;

    // https://www.baeldung.com/exception-handling-for-rest-with-spring
    /*
     * 
     * DefaultHandlerExceptionResolver
     * ResponseStatusExceptionResolver
     * 
     @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        return loggingFilter;
    }
     **/

    /**
     * a common MVC exception and Custom Exception handling
     * 
     * Exception (global exception)
     * BusinessException (custom exception)
     * BadReqeustException (custom exception)
     * BadParameterException (custom exception)
     * 
     * (web MVC exception)
     * NoHandlerFoundException
     * MethodArgumentTypeMismatchException
     * ConstraintViolationException
     * MethodArgumentNotValidException
     * BindException
     * TypeMismatchException
     * MissingServletRequestPartException
     * MissingServletRequestParameterException
     * HttpRequestMethodNotSupportedException
     * HttpMediaTypeNotSupportedException
     * HttpMediaTypeNotAcceptableException
     * ServletRequestBindingException
     * ConversionNotSupportedException
     * AsyncRequestTimeoutException
     * 
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpReq, HttpServletResponse httpRes, Object handler,
            Exception ex) {

        if(CoreConfig.currentPhase.isEquals(Phase.LOCAL, Phase.DEV)) {
//            log.debug("CustomHandlerExceptionResolver : {}", ex);
        }

        try {
            if (ex instanceof BusinessException) {
                return this.handleBusiness(BusinessException.class.cast(ex));

            } else if (ex instanceof BadRequestException) {
                return this.handleBadReqeust(BadRequestException.class.cast(ex));

            } else if (ex instanceof BadParameterException) {
                return this.handleBadParameter(BadParameterException.class.cast(ex));

            } else if (ex instanceof NoHandlerFoundException) {
                return this.handleNoHandlerFound(NoHandlerFoundException.class.cast(ex));

            } else if (ex instanceof MethodArgumentTypeMismatchException) {
                return this.handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException.class.cast(ex));

            } else if (ex instanceof ConstraintViolationException) {
                return this.handleConstraintViolation(ConstraintViolationException.class.cast(ex));

            } else if (ex instanceof MethodArgumentNotValidException) {
                return this.handleMethodArgumentNotValid(MethodArgumentNotValidException.class.cast(ex));

            } else if (ex instanceof BindException) {
                return this.handleBind(BindException.class.cast(ex));

            } else if (ex instanceof TypeMismatchException) {
                return this.handleTypeMismatch(TypeMismatchException.class.cast(ex));

            } else if (ex instanceof MissingServletRequestPartException) {
                return this.handleMissingServletRequestPart(MissingServletRequestPartException.class.cast(ex));

            } else if (ex instanceof MissingServletRequestParameterException) {
                return this.handleMissingServletRequestParameter(MissingServletRequestParameterException.class.cast(ex));

            } else if (ex instanceof HttpRequestMethodNotSupportedException) {
                return this.handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException.class.cast(ex));

            } else if (ex instanceof HttpMediaTypeNotSupportedException) {
                return this.handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException.class.cast(ex));

            } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
                return this.handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException.class.cast(ex));

            } else if (ex instanceof ServletRequestBindingException) {
                return this.handleServletRequestBinding(ServletRequestBindingException.class.cast(ex));

            } else if (ex instanceof ConversionNotSupportedException) {
                return this.handleConversionNotSupported(ConversionNotSupportedException.class.cast(ex));

            } else if (ex instanceof AsyncRequestTimeoutException) {
                return this.handleAsyncRequestTimeout(AsyncRequestTimeoutException.class.cast(ex), httpRes);

            // @PreAuthorize("isAuthenticated()") handling
            } else if (ex instanceof AccessDeniedException) {
                return this.handleAccessDenied(AccessDeniedException.class.cast(ex), httpRes);

            } else {
                return this.handleAll(ex);
            }
        } catch (Exception e) {
            log.error("error:{}", e);
            return null;
        }
    }

    /**
     * Exception handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : Exception.class
     * @return ModelAndView
     */
    private ModelAndView handleAll(Exception ex) {
        log.error("error:{}", ex);

        if(ex != null) {
            String errorMsg = ex.getMessage();
            return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
        } else {
            return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * BusinessException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : BusinessException.class
     * @return ModelAndView
     */
    private ModelAndView handleBusiness(BusinessException ex) {
        log.info(ex.getClass().getName());

        ResBody<Object> resBody = ResBody.builder()
                .msg(ex.getMessage())
                .data(ex.data)
                .code(ex.getCodeEnum().getCode())
                .build();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, resBody);
    }

    /**
     * BadReqeustException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : BadReqeustException.class
     * @return ModelAndView
     */
    private ModelAndView handleBadReqeust(BadRequestException ex) {
        log.info(ex.getClass().getName());

        ResBody<Object> resBody = ResBody.builder()
                .msg(ex.getMessage())
                .build();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, resBody);
    }

    /**
     * BadParameterException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : BadParameterException.class
     * @return ModelAndView
     */
    private ModelAndView handleBadParameter(BadParameterException ex) {
        log.info(ex.getClass().getName());

        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if(error.isBindingFailure()) {
                if(StringUtils.isEmpty(error.getField())) {
                    errors.add( error.getRejectedValue() +" is invalid parameter");
                } else {
                    errors.add(error.getField() + " : " + error.getRejectedValue() +" is invalid parameter");
                }
            } else {
                if(StringUtils.isEmpty(error.getField())) {
                    errors.add(error.getDefaultMessage());
                } else {
                    errors.add(error.getField() + " : " + error.getDefaultMessage());
                }
            }
        }

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * NoHandlerFoundException handling
     * 
     * HttpSatus : 404
     * 
     * @param ex : NoHandlerFoundException.class
     * @return ModelAndView
     */
    private ModelAndView handleNoHandlerFound(NoHandlerFoundException ex) {

        String errorMsg = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.NOT_FOUND, errorMsg);
    }

    /**
     * MethodArgumentTypeMismatchException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : MethodArgumentTypeMismatchException.class
     * @return ModelAndView
     */
    private ModelAndView handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getValue() + " is invalid parameter";

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * ConstraintViolationException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : ConstraintViolationException.class
     * @return ModelAndView
     */
    private ModelAndView handleConstraintViolation(ConstraintViolationException ex) {
        log.info(ex.getClass().getName());

        List<String> errors = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * MethodArgumentNotValidException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : MethodArgumentNotValidException.class
     * @return ModelAndView
     */
    private ModelAndView handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.info(ex.getClass().getName());

        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * BindException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : BindException.class
     * @return ModelAndView
     */
    private ModelAndView handleBind(BindException ex) {
        log.info(ex.getClass().getName());

        List<String> errors = new ArrayList<String>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if(error.isBindingFailure()) {
                errors.add(error.getField() + " : '" + error.getRejectedValue() +"' is invalid parameter");
            } else {
                errors.add(error.getField() + " : " + error.getDefaultMessage());
            }
        }

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errors);
    }

    /**
     * TypeMismatchException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : TypeMismatchException.class
     * @return ModelAndView
     */
    private ModelAndView handleTypeMismatch(TypeMismatchException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * MissingServletRequestPartException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : MissingServletRequestPartException.class
     * @return ModelAndView
     */
    private ModelAndView handleMissingServletRequestPart(MissingServletRequestPartException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getRequestPartName() + " part is missing";

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * MissingServletRequestParameterException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : MissingServletRequestParameterException.class
     * @return ModelAndView
     */
    private ModelAndView handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getParameterName() + " parameter is missing";

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * HttpRequestMethodNotSupportedException handling
     * 
     * HttpSatus : 405
     * 
     * @param ex : HttpRequestMethodNotSupportedException.class
     * @return ModelAndView
     */
    private ModelAndView handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.info(ex.getClass().getName());

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
        String errorMsg = builder.toString();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.METHOD_NOT_ALLOWED, errorMsg);
    }

    /**
     * HttpMediaTypeNotSupportedException handling
     * 
     * HttpSatus : 415
     * 
     * @param ex : HttpMediaTypeNotSupportedException.class
     * @return ModelAndView
     */
    private ModelAndView handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.info(ex.getClass().getName());

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
        String errorMsg = builder.substring(0, builder.length() - 2);

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.UNSUPPORTED_MEDIA_TYPE, errorMsg);
    }

    /**
     * HttpMediaTypeNotAcceptableException handling
     * 
     * HttpSatus : 406
     * 
     * @param ex : HttpMediaTypeNotAcceptableException.class
     * @return ModelAndView
     */
    private ModelAndView handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex) {
        log.info(ex.getClass().getName());

        StringBuilder builder = new StringBuilder();
        builder.append("Media type is not acceptable. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
        String errorMsg = builder.substring(0, builder.length() - 2);

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.NOT_ACCEPTABLE, errorMsg);
    }

    /**
     * ServletRequestBindingException handling
     * 
     * HttpSatus : 400
     * 
     * @param ex : ServletRequestBindingException.class
     * @return ModelAndView
     */
    private ModelAndView handleServletRequestBinding(ServletRequestBindingException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getMessage();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.BAD_REQUEST, errorMsg);
    }

    /**
     * ConversionNotSupportedException handling
     * 
     * HttpSatus : 500
     * 
     * @param ex : ConversionNotSupportedException.class
     * @return ModelAndView
     */
    private ModelAndView handleConversionNotSupported(ConversionNotSupportedException ex) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getValue() + " value for " + ex.getPropertyName() + " should be of type " + ex.getRequiredType();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);
    }

    /**
     * AsyncRequestTimeoutException handling
     * 
     * HttpSatus : 503
     * 
     * @param ex : AsyncRequestTimeoutException.class
     * @return ModelAndView
     */
    private ModelAndView handleAsyncRequestTimeout(AsyncRequestTimeoutException ex, HttpServletResponse response) {
        log.info(ex.getClass().getName());

        if (response != null && response.isCommitted()) {
            log.warn("Async request timed out");
            return null;
        }

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * AccessDeniedException handling
     * 
     * HttpSatus : 403
     * 
     * @param ex : AccessDeniedException.class
     * @return ModelAndView
     */
    private ModelAndView handleAccessDenied(AccessDeniedException ex, HttpServletResponse response) {
        log.info(ex.getClass().getName());

        String errorMsg = ex.getMessage();

        return StaticWebUtils.defaultExceptionModelAndView(HttpStatus.FORBIDDEN, errorMsg);
    }
}
