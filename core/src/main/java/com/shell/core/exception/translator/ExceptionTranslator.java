package com.shell.core.exception.translator;

import com.shell.common.exception.ApiInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 22:28
 * @Description
 */
@Slf4j
@Component
public class ExceptionTranslator {

    private final Map<Class<?>, HttpStatus> errorStatus = new HashMap<>();

    private void init() {
        // errorStatus.put(AuthenticationException.class, HttpStatus.UNAUTHORIZED);
        // errorStatus.put(AccessDeniedException.class, HttpStatus.FORBIDDEN);
        errorStatus.put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
        errorStatus.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        errorStatus.put(BindException.class, HttpStatus.BAD_REQUEST);
        errorStatus.put(DuplicateKeyException.class, HttpStatus.BAD_REQUEST);
        errorStatus.put(ApiInputException.class, HttpStatus.BAD_REQUEST);
    }

    public ExceptionTranslator(ObjectProvider<ExceptionTranslatorCustomizer> exceptionTranslatorCustomizer) {
        init();
        Optional.ofNullable(exceptionTranslatorCustomizer.getIfAvailable()).ifPresent(et -> {
            et.customize(this);
        });
    }

    public HttpStatus translate(Throwable e) {
        if (e != null) {
            for (Class<?> t = e.getClass(); t != null; t = t.getSuperclass()) {
                HttpStatus status = errorStatus.get(t);
                if (status != null) {
                    return status;
                }
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public Map<Class<?>, HttpStatus> errorStatus() {
        return errorStatus;
    }

}
