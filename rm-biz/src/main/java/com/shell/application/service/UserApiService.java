package com.shell.application.service;

import com.shell.api.dto.req.UserReqDto;
import com.shell.api.dto.res.UserResDto;
import com.shell.common.api.ApiPage;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:52
 * @Description
 */
public interface UserApiService {
    UserResDto save(UserReqDto reqDto);

    UserResDto getById(Long id);

    Boolean updateById(Long id, UserReqDto reqDto);

    Boolean deleteById(Long id);

    ApiPage<UserResDto> getPageable(MultiValueMap<String, String> query, Pageable pageable);
}
