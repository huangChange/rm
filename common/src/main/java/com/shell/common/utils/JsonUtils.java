package com.shell.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 21:20
 * @Description
 */
@Component
public class JsonUtils implements ApplicationContextAware {

    private static volatile ObjectMapper objectMapper = createDefaultObjectMapper();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }

    /**
     *
     * @param javaObject the java object to be converted
     * @return
     */
    public static String toJsonString(@Nonnull Object javaObject) {
        return toJsonString(javaObject, false);
    }

    /**
     *
     * @param javaObject
     * @param forcePretty
     * @return
     */
    public static String toJsonString(@Nonnull Object javaObject, boolean forcePretty) {
        try {
            if (forcePretty) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(javaObject);
            } else {
                return objectMapper.writeValueAsString(javaObject);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param jsonString
     * @param valueType
     * @return
     * @param <T>
     */
    public static <T> T fromJsonString(@Nonnull String jsonString, @Nonnull Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param jsonString
     * @param valueTypeRef
     * @return
     * @param <T>
     */
    public static <T> T fromJsonString(@Nonnull String jsonString, @Nonnull TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(jsonString, valueTypeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param fromValue
     * @param toValueType
     * @return
     * @param <T>
     */
    public static <T> T convertValue(@Nonnull Object fromValue, @Nonnull Class<T> toValueType) {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    private static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_FLOAT_AS_INT, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDefaultSetterInfo(JsonSetter.Value.construct(Nulls.SKIP, Nulls.DEFAULT));
        return objectMapper;
    }

}
