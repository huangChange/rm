package com.shell.core.i18n;

import com.shell.common.lazy.LazyObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 22:34
 * @Description
 */
@Slf4j
@Component
public class MessageFinder {

    private static final int MAX_EXCEPTION_DEPTH = 16;

    @Resource
    private MessageSource messageSource;

    private final Object[] args = null;

    public String lookupSysException(final  Throwable e, Locale locale) {
        Throwable prev = null;
        int idx = 0;
        for (Throwable cur = e; cur != null && cur != prev && (idx++ < MAX_EXCEPTION_DEPTH); cur = cur.getCause()) {
            String messageKey = MessageTypeEnum.SYS_EXCEPTION.toKey(e.getClass().getName());
            String message = messageSource.getMessage(messageKey, args, "", locale);
            if (StringUtils.isNotEmpty(message)) {
                String detail = extractErrorDetail(cur);
                if (StringUtils.isNotEmpty(detail)) {
                    return message + ": " + detail;
                }
                return message;
            } else {
                log.error("No message defined for {}", locale, messageKey);
            }
            prev = cur;
        }
        return e.getMessage();
    }

    public String lookupHttpCode(HttpStatus httpStatus, Locale locale) {
        String messageKey = MessageTypeEnum.API_CODE.toKey("common" + "." + httpStatus.value());
        return messageSource.getMessage(messageKey, args, httpStatus.getReasonPhrase(), locale);
    }

    public String lookupApiCode(String appId, int code, Locale locale) {
        String biz = code < 100000 ? "common" : appId;
        String messageKey = MessageTypeEnum.API_CODE.toKey(biz + "." + code);
        return messageSource.getMessage(messageKey, args, "", locale);
    }

    private String extractErrorDetail(Throwable cur) {
        if (cur instanceof DuplicateKeyException) {
            return LazyObject.from(cur)
                    .apply(DuplicateKeyException.class, e -> e.getCause())
                    .to(String.class);
        }
        return "";
    }

}
