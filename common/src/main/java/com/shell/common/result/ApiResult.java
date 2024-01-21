package com.shell.common.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.shell.common.cpnstants.SysConstants;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/15 1:11
 * @Description
 */
@JsonPropertyOrder({"code", "msg", "data"})
public class ApiResult<T> {

    @JsonProperty(index = 10, value = "code")
    private int code;

    @JsonProperty(index = 20, value = "msg")
    private String msg;

    @JsonProperty(index = 30, value = "data")
    private T data;

    @JsonProperty(index = 40, value = "errorMsg")
    private String errorMsg;

    @JsonIgnore
    public boolean isOk() {
        return this.code >= 200 && this.code <= 208;
    }

    public static<T> ApiResult<T> ok(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.code = 200;
        result.data = data;
        result.msg = "";
        return result;
    }

    public static<T> ApiResult<T> fail(String msg) {
        ApiResult<T> result = new ApiResult<>();
        result.code = 500;
        result.data = null;
        result.msg = msg;
        return result;
    }

    public static<T> ApiResult<T> fail(final int code) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        return result;
    }

    public static<T> ApiResult<T> fail(int code, String msg) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.data = null;
        result.msg = msg;
        return result;
    }

    public static<T> ApiResult<T> fail(int code, String msg, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static<T> ApiResult<T> fail(int code, String msg, T data, String errorMsg) {
        ApiResult<T> result = new ApiResult<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        result.errorMsg = errorMsg;
        return result;
    }

    public ApiResult() {

    }

    public ApiResult(final int code, final T data) {
        this.code = code;
        this.data = data;
    }

    public ApiResult(final int code, final T data, final String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public ApiResult(final int code, final String msg, final T data, final String errorMsg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.errorMsg = errorMsg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return !this.isOk() && SysConstants.SKIP_API_DATA ? null : this.data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }
}
