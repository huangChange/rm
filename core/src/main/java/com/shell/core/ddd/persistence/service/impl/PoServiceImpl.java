package com.shell.core.ddd.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shell.core.ddd.persistence.dao.AbstractMapper;
import com.shell.core.ddd.persistence.service.IPoService;
import com.shell.core.ddd.persistence.service.PoServiceContainer;
import com.shell.core.mybatis.util.WrapperUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:27
 * @Description
 */
public class PoServiceImpl<M extends AbstractMapper<T>, T> extends ServiceImpl<M, T> implements IPoService<T>, InitializingBean {

    @Resource
    private PoServiceContainer poServiceContainer;

    @Override
    public void afterPropertiesSet() throws Exception {
        poServiceContainer.put(this.entityClass, this);
    }

    @Override
    public OffsetDateTime now() {
        return getBaseMapper().now();
    }

    @Override
    public boolean exists(Wrapper<T> queryWrapper) {
        return getBaseMapper().exists(queryWrapper);
    }

    @Override
    public boolean updateByIdWithAllColumns(T entity) {
        UpdateWrapper<T> updateWrapper = WrapperUtils.buildUpdateByIdWithAllColumns(entity, getEntityClass());
        return update(null, updateWrapper);
    }

    @Override
    public List<T> getList(MultiValueMap<String, String> query) {
        Wrapper<T> queryWrapper = WrapperUtils.toQueryWrapper(query, getEntityClass());
        return list(null, queryWrapper);
    }

    @Override
    public IPage<T> getPageable(MultiValueMap<String, String> query, Pageable pageable) {
        IPage<T> page = WrapperUtils.toPage(pageable);
        Wrapper<T> queryWrapper = WrapperUtils.toQueryWrapper(query, getEntityClass());
        return page(page, queryWrapper);
    }

}
