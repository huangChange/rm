package com.shell.common.utils;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 23:35
 * @Description
 */
public abstract class ExceptionUtils {

    public static final int MAX_CAUSE_DEPTH = 64;

    public static Throwable getMessageCause(@Nonnull Throwable t) {
        int depth = 0;
        for (Throwable child = t; child != null && depth < MAX_CAUSE_DEPTH; ++depth, child = child.getCause()) {
            if (StringUtils.isNotEmpty(child.getMessage())) {
                return child;
            }
        }
        return t;
    }

    public static Throwable getRootCause(@Nonnull Throwable t) {
        int depth = 0;
        Throwable root;
        do {
            root = t;
            t = t.getCause();
        } while (t != null && root != t && depth < MAX_CAUSE_DEPTH);
        return root;
    }

    public static String toJobError(Throwable e) {
        int maxRows = 5;
        int maxLength = 4096;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
        try (PrintWriter pw = new PrintWriter(bos)) {
            e.printStackTrace(pw);
        }
        String EOL = System.lineSeparator();
        String[] errStack = bos.toString().split("[\\r\\n]+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i< maxRows; ++i) {
            if (i > 0) {
                sb.append(EOL);
            }
            sb.append(errStack[i]);
        }
        String jobError = sb.toString();
        if (jobError.length() > maxLength) {
            jobError = jobError.substring(0, maxLength - 4) + " ...";
        }
        return jobError;
    }

}
