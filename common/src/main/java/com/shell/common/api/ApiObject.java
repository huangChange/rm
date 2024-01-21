package com.shell.common.api;

import com.shell.common.utils.JsonUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/19 19:54
 * @Description
 */
@Component
public class ApiObject implements Serializable {

    private static final Long serialVersionUID = 1L;

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }

}
