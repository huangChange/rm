package com.shell.common;

import com.shell.core.ddd.api.IApiStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/15 21:16
 * @Description
 */
@Getter
@AllArgsConstructor
public enum RmStatusEnum implements IApiStatus {

    SUCCESS(200), // 成功
    FAIL(500), // 失败
    ;

    private int code;

}
