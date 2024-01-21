package com.shell.core.ddd.persistence.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:27
 * @Description
 */
@Component
public class PoServiceContainer {

    /**
     * po类和poService接口的映射
     */
    private ConcurrentHashMap<Class<?>, IPoService<?>> poServiceMap = new ConcurrentHashMap<>();

    public <T> void put(Class<? extends T> t, IPoService<? extends T> poService) {
        poServiceMap.put(t, poService);
    }

    /**
     *
     * @param t - po类
     * @param <T> - poService接口
     * @return
     */
    public <T> IPoService<T> get(Class<? extends T> t) {
        return (IPoService<T>) poServiceMap.get(t);
    }

}
