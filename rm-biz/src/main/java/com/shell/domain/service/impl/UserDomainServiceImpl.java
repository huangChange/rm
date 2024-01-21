package com.shell.domain.service.impl;

import com.shell.api.dto.req.UserReqDto;
import com.shell.domain.service.UserDomainService;
import com.shell.persistence.service.UserPoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:53
 * @Description
 */
@Service
public class UserDomainServiceImpl implements UserDomainService {

    @Resource
    private UserPoService userPoService;

    @Override
    public void init(UserReqDto reqDto) {

    }
}
