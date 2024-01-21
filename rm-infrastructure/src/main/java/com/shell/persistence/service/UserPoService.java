package com.shell.persistence.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shell.core.ddd.persistence.service.IPoService;
import com.shell.persistence.po.UserPo;

import java.util.Map;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/14 0:07
 * @Description
 */
public interface UserPoService extends IPoService<UserPo> {
    IPage<Map<String, Object>> getLnk(Wrapper<?> ew, IPage<Map<String, Object>> page);
}
