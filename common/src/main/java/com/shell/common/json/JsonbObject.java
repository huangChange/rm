package com.shell.common.json;

import com.shell.common.utils.JsonUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class JsonbObject extends LinkedHashMap<String, Object> {

    private static final Long serialVersionUID = 1L;

    public JsonbObject() {
        super();
    }

    public JsonbObject(JsonbObject jsonbObject) {
        super(jsonbObject);
    }

    public JsonbObject(int initialCapacity, float loadFactory, boolean accessOrder) {
        super(initialCapacity, loadFactory, accessOrder);
    }

    public JsonbObject(int initialCapacity, float loadFactory) {
        super(initialCapacity, loadFactory);
    }

    public JsonbObject(int initialCapacity) {
        super(initialCapacity);
    }

    public JsonbObject(Map<? extends String, Object> m) {
        super(m);
    }

    public JsonbObject(String src) {
        super(JsonUtils.fromJsonString(src, Map.class));
    }

}
