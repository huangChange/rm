package com.shell.core.ddd.persistence.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
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
public interface IPoService<T> extends IService<T> {

    /**
     * databse now
     */
    OffsetDateTime now();

    /**
     * 是否存在记录
     * @param queryWrapper
     * @return
     */
    boolean exists(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据id修改所有字段，包括 null 值字段（乐观锁字段部委null时，条件策略生效；否则直接更新，忽略乐观锁条件。）
     * @param entity
     * @return
     */
    boolean updateByIdWithAllColumns(@Param(Constants.ENTITY) T entity);

    /**
     *
     * @param query 单表查询条件
     * @return
     */
    List<T> getList(MultiValueMap<String, String> query);

    /**
     *
     * @param query 支持单表查询
     * @param pageable 分页参数（Spring controller 自动注入）
     * @return 分页对象
     */
    IPage<T> getPageable(MultiValueMap<String, String> query, Pageable pageable);

}
