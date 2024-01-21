package com.shell.exception;

import com.shell.common.Constants;
import com.shell.core.exception.handler.AbstractExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 23:53
 * @Description
 */
@RestControllerAdvice
public class RmExceptionHandler extends AbstractExceptionHandler {

    public RmExceptionHandler() {
        super(Constants.APP_ID);
    }

}
