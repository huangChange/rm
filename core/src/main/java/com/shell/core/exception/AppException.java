package com.shell.core.exception;

import com.shell.core.ddd.api.IApiStatus;
import lombok.Getter;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/15 21:08
 * @Description
 */
@Getter
public class AppException extends RuntimeException {

    private static final Long serialVersionUID = 1L;

    private final IApiStatus apiStatus;

    private Object data;

    public AppException(IApiStatus apiStatus) {
        this.apiStatus = apiStatus;
    }

    public AppException(IApiStatus apiStatus, String message) {
        super(message);
        this.apiStatus = apiStatus;
    }

    public AppException(IApiStatus apiStatus, String message, Object data) {
        super(message);
        this.apiStatus = apiStatus;
        this.data = data;
    }

    public AppException(IApiStatus apiStatus, String message, Throwable cause) {
        super(message, cause);
        this.apiStatus = apiStatus;
    }
}
