package com.shell.core.exception.handler;

import com.google.common.collect.Lists;
import com.shell.common.result.ApiResult;
import com.shell.common.utils.ExceptionUtils;
import com.shell.core.exception.AppException;
import com.shell.core.exception.translator.ExceptionTranslator;
import com.shell.core.i18n.MessageFinder;
import feign.FeignException;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/15 21:13
 * @Description
 */
@Slf4j
public abstract class AbstractExceptionHandler {

    protected final String appId;

    protected final List<String> errorTypePriorities = initErrorTypePriorities();

    @Resource
    private ExceptionTranslator exceptionTranslator;

    @Resource
    private MessageFinder messageFinder;

    public AbstractExceptionHandler(String appId) {
        this.appId = appId;
    }

    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ResponseEntity<ApiResult<Object>> handleAppException(AppException e, Locale locale) {
        String message = messageFinder.lookupApiCode(appId, e.getApiStatus().getCode(), locale);
        String error = String.format("Application: %s(%s: %s) %s", e.getApiStatus().name(), e.getApiStatus().getCode(), message, e.getMessage());
        log.error(error, e);
        ApiResult<Object> result = new ApiResult<Object>(e.getApiStatus().getCode(), message, e.getData(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<ApiResult<String>> handleBindException(BindException e, Locale locale) {
        ApiResult<String> result = processBindingResult(e, locale, e.getBindingResult());
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ApiResult<String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Locale locale) {
        ApiResult<String> result = processBindingResult(e, locale, e.getBindingResult());
        return ResponseEntity.status(result.getCode()).body(result);
    }

    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ResponseEntity<ApiResult<String>> handleFeignException(FeignException e, Locale locale) {
        Throwable rootCause = ExceptionUtils.getRootCause(e);
        log.error(rootCause.getMessage(), rootCause.getCause());
        return handleThrowable(e, locale);
    }

    @ExceptionHandler(Throwable.class)
    private ResponseEntity<ApiResult<String>> handleThrowable(Throwable e, Locale locale) {
        ApiResult<String> result = handleResult(e, locale);
        log.error(e.getMessage(), e.getCause());
        return ResponseEntity.status(result.getCode()).body(result);
    }

    private ApiResult<String> handleResult(Throwable e, Locale locale) {
        HttpStatus status = exceptionTranslator.translate(e);
        String message = messageFinder.lookupSysException(e, locale);
        Throwable pretty = ExceptionUtils.getMessageCause(e);
        String details = StringUtils.firstNonEmpty(pretty.getMessage(), e.getClass().getSimpleName());
        return new ApiResult<>(status.value(), message, null, details);
    }

    private ApiResult<String> processBindingResult(BindException e, Locale locale, BindingResult r) {
        List<FieldError> fieldErrors = r.getFieldErrors();
        Optional<FieldError> fieldError = fieldErrors.stream().sorted(this::errTypeComparator).findFirst();
        String message = fieldError.map(fe -> fe.getField() + ":" + fe.getDefaultMessage()).orElse(r.getAllErrors().get(0).getDefaultMessage());
        return new ApiResult<>(exceptionTranslator.translate(e).value(), message, null, "<<ValidationError>>");
    }

    protected int errTypeComparator(FieldError lhs, FieldError rhs) {
        return Integer.compare(errorTypePriorities.indexOf(rhs.getCode()), errorTypePriorities.indexOf(lhs.getCode()));
    }

    protected List<String> initErrorTypePriorities() {
        List<String> priorities = Lists.newArrayList();
        priorities.add(Length.class.getSimpleName());
        priorities.add(Size.class.getSimpleName());
        priorities.add(NotEmpty.class.getSimpleName());
        priorities.add(NotNull.class.getSimpleName());
        priorities.add(TypeMismatchException.ERROR_CODE);
        return priorities;
    }

}
