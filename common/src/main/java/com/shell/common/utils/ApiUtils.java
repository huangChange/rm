package com.shell.common.utils;

import com.google.common.collect.Lists;
import com.shell.common.exception.ApiInputException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 19:52
 * @Description
 */
@UtilityClass
public class ApiUtils {

    /**
     *
     * @param resource
     * @return "forward:resource"
     */
    public static String forward(String resource) {
        return "forward:" + resource;
    }

    /**
     *
     * @param resource
     * @return "redirect:resource"
     */
    public static String redirect(String resource) {
        return "redirect:" + resource;
    }

    /**
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static Integer parseInt(String value, Integer defaultValue) {
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static Long parseLong(String value, Long defaultValue) {
        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static Double parseDouble(String value, Double defaultValue) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     *
     * @param csv
     * @return
     */
    public static List<Long> parseIds(String csv) {
        return toList(csv, Long::valueOf);
    }

    /**
     *
     * @param csv
     * @param func
     * @return
     * @param <T>
     */
    public static <T> List<T> toList(String csv, Function<String, T> func) {
        String[] itemIdList = StringUtils.split(csv, ",");
        List<T> valueList = Lists.newArrayListWithCapacity(itemIdList.length);
        for (String item : itemIdList) {
            try {
                T value = func.apply(item);
                valueList.add(value);
            } catch (Exception e) {
                throw new ApiInputException(e.getMessage(), e);
            }
        }
        return valueList;
    }

    /**
     *
     * @param text
     * @param separatorChar the separator char
     * @param escapeChar
     * @return splited list
     */
    public static List<String> split(String text, char separatorChar, char escapeChar) {
        return split(text, separatorChar, escapeChar, false);
    }

    /**
     *
     * @param text
     * @param separatorChar
     * @param escapeChar
     * @param reserveEscape
     * @return
     */
    public static List<String> split(String text, char separatorChar, char escapeChar, boolean reserveEscape) {
        if (separatorChar == escapeChar) {
            throw new ApiInputException("Separator char should diff from escape char: " + separatorChar + " == " + escapeChar);
        }
        List<String> items = Lists.newArrayList();
        StringBuilder sb = new StringBuilder();
        int idx = 0;
        int maxLen = text.length();
        while (idx < maxLen) {
            char curChar = text.charAt(idx);
            if (curChar == escapeChar) {
                if (idx + 1 >= maxLen) {
                    throw new ApiInputException("Terminated at escape: " + text);
                }
                char nextChar = text.charAt(idx + 1);
                if (reserveEscape) {
                    sb.append(curChar);
                }
                sb.append(nextChar);
                idx += 2;
            } else if (curChar == separatorChar) {
                items.add(sb.toString());
                sb.setLength(0);
                idx += 1;
            } else {
                sb.append(curChar);
                idx += 1;
                if (idx >= maxLen) {
                    items.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        return items;
    }

}
