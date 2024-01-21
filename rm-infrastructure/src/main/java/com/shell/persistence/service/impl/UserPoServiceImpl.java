package com.shell.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shell.core.ddd.persistence.service.impl.PoServiceImpl;
import com.shell.persistence.po.UserPo;
import com.shell.persistence.repository.UserPoMapper;
import com.shell.persistence.service.UserPoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/14 0:07
 * @Description
 */
@Service
public class UserPoServiceImpl extends PoServiceImpl<UserPoMapper, UserPo> implements UserPoService {

    @Resource
    private UserPoMapper userPoMapper;

    @Override
    public IPage<Map<String, Object>> getLnk(Wrapper<?> ew, IPage<Map<String, Object>> page) {
        return userPoMapper.getUserLnk(ew, page);
    }
}
