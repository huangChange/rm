package com.shell.application.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shell.api.dto.req.UserReqDto;
import com.shell.api.dto.res.UserResDto;
import com.shell.application.service.UserApiService;
import com.shell.common.api.ApiPage;
import com.shell.convert.req.UserReqConvert;
import com.shell.convert.res.UserResConvert;
import com.shell.core.mybatis.util.WrapperUtils;
import com.shell.domain.service.UserDomainService;
import com.shell.persistence.po.UserPo;
import com.shell.persistence.service.UserPoService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:52
 * @Description
 */
@Service
public class UserApiServiceImpl implements UserApiService {

    @Resource
    private UserPoService userPoService;

    @Resource
    private UserDomainService userDomainService;

    @Resource
    private UserReqConvert userReqConvert;

    @Resource
    private UserResConvert userResConvert;

    @Override
    public UserResDto save(UserReqDto reqDto) {
        userDomainService.init(reqDto);
        UserPo userPo = userReqConvert.dto2Po(reqDto);
        userPoService.save(userPo);
        return userResConvert.po2Dto(userPoService.getById(userPo.getId()));
    }

    @Override
    public UserResDto getById(Long id) {
        return userResConvert.po2Dto(userPoService.getById(id));
    }

    @Override
    public Boolean updateById(Long id, UserReqDto reqDto) {
        UserPo userPo = userReqConvert.dto2Po(reqDto);
        userPo.setId(id);
        return userPoService.updateById(userPo);
    }

    @Override
    public Boolean deleteById(Long id) {
        return userPoService.removeById(id);
    }

    @Override
    public ApiPage<UserResDto> getPageable(MultiValueMap<String, String> query, Pageable pageable) {
        IPage<UserPo> page = userPoService.getPageable(query, pageable);
        return ApiPage.to(page, userResConvert::po2DtoList);
    }

}
