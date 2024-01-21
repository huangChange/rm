package com.shell.core.ddd.persistence.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.OffsetDateTime;

public interface AbstractMapper<T> extends BaseMapper<T> {

    /**
     * database now
     */
    @Select("select NOW()")
    OffsetDateTime now();

    /**
     * 判断是否存在
     * @param queryWrapper
     * @return
     */
    boolean exists(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

}
