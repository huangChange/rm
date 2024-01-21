package com.shell.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.shell.core.ddd.persistence.dao.AbstractMapper;
import com.shell.persistence.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/14 0:08
 * @Description
 */
@Mapper
public interface UserPoMapper extends AbstractMapper<UserPo> {
    IPage<Map<String, Object>> getUserLnk(@Param(Constants.WRAPPER) Wrapper<?> ew, IPage<Map<String, Object>> page);
}
