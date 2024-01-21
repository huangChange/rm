package com.shell.common.lazy;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/20 22:40
 * @Description
 */
public class LazyObject {

    private Object value;

    private LazyObject(Object value) {
        this.value = value;
    }

    public <F, T> LazyObject apply(Class<F> type, Function<F, T> func) {
        Object v = value;
        if (type.isInstance(v)) {
            value = func.apply(type.cast(v));
        } else {
            value = null;
        }
        return this;
    }

    public <T> void accept(Class<T> type, Consumer<T> consumer) {
        Object v = value;
        if (type.isInstance(v)) {
            consumer.accept(type.cast(v));
        }
    }

    public <T> T to(Class<T> type) {
        return to(type, null);
    }

    public <T> T to(Class<T> type, T defaultValue) {
        Object v = value;
        if (type.isInstance(v)) {
            return type.cast(v);
        }
        return defaultValue;
    }

    public static LazyObject from(Object object) {
        return new LazyObject(object);
    }

}
