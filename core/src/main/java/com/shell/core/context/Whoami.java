package com.shell.core.context;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.List;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:27
 * @Description
 */
public class Whoami {

    private Long appId;

    private Long operatorId;

    private String operatorName;

    private Boolean administor;

    private List<String> administors;

    private static final TransmittableThreadLocal<Whoami> TRANSMITTABLE_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(Whoami whoami) {
        TRANSMITTABLE_THREAD_LOCAL.set(whoami);
    }

    public static Whoami get() {
        return TRANSMITTABLE_THREAD_LOCAL.get();
    }

    public static void cleanUp() {
        TRANSMITTABLE_THREAD_LOCAL.remove();
    }

    public Whoami() {

    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Boolean getAdministor() {
        return administor;
    }

    public void setAdministor(Boolean administor) {
        this.administor = administor;
    }

    public List<String> getAdministors() {
        return administors;
    }

    public void setAdministors(List<String> administors) {
        this.administors = administors;
    }
}
