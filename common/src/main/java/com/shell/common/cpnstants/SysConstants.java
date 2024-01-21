package com.shell.common.cpnstants;

import jakarta.annotation.Nullable;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 13:22
 * @Description
 */
public abstract class SysConstants {

    @Nullable
    public static final String SWAGGER_TOKEN_URL = System.getProperty("SWAGGER_TOKEN_URL");

    @Nullable
    public static final boolean SKIP_API_DATA = "true".equalsIgnoreCase(System.getProperty("SKIP_API_DATA"));

    @Nullable
    public static final String SQL_GUARD_STRATEGY = System.getProperty("SQL_GUARD_STRATEGY");

    public SysConstants() {}

}
