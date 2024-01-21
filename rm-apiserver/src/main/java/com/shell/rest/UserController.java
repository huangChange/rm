package com.shell.rest;

import com.shell.api.UserApi;
import com.shell.api.dto.req.UserReqDto;
import com.shell.api.dto.res.UserResDto;
import com.shell.application.service.UserApiService;
import com.shell.common.api.ApiPage;
import com.shell.common.result.ApiResult;
import com.shell.core.context.Whoami;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:48
 * @Description
 */
@RestController
@RequestMapping("/users")
public class UserController implements UserApi {

    @Resource
    private UserApiService userApiService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<UserResDto> save(@RequestBody UserReqDto reqDto) {
        Whoami.set(new Whoami());
        UserResDto result = userApiService.save(reqDto);
        return ApiResult.ok(result);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<UserResDto> getById(@PathVariable Long id) {
        UserResDto result = userApiService.getById(id);
        return ApiResult.ok(result);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<UserResDto> updateById(@PathVariable Long id, @RequestBody UserReqDto reqDto) {
        Whoami.set(new Whoami());
        Boolean result = userApiService.updateById(id, reqDto);
        return result ? getById(id) : ApiResult.fail(500);
    }

    @DeleteMapping(value = "/{id}")
    public ApiResult<Boolean> deleteById(@PathVariable Long id) {
        Boolean result = userApiService.deleteById(id);
        return ApiResult.ok(result);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<ApiPage<UserResDto>> getPageableUser(@RequestParam MultiValueMap<String, String> query, Pageable pageable) {
        ApiPage<UserResDto> result = userApiService.getPageable(query, pageable);
        return ApiResult.ok(result);
    }

}
