package com.shell.core.ddd.api;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/16 21:42
 * @Description
 */
public interface IApiStatus {

    int getCode();

    String name();

    default boolean isOk() {
        int code = getCode();
        return code >= 200 && code <= 208;
    }

}
