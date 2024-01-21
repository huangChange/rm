package com.shell.core.i18n;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 22:29
 * @Description
 */
public enum MessageTypeEnum implements IMessageType {

    SYS_EXCEPTION("SYS.EXCEPTION"),
    API_CODE("API.CODE"),
    ;

    private final String prefix;

    MessageTypeEnum(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String prefix() {
        return prefix;
    }

    @Override
    public String toKey(String remaining) {
        return prefix() + "." + remaining;
    }
}

interface IMessageType {

    /**
     *
     * @return prefix as part of the code
     */
    String prefix();

    /**
     *
     * @param remaining
     * @return
     */
    String toKey(String remaining);

}