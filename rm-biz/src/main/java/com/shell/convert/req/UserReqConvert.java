package com.shell.convert.req;

import com.shell.api.dto.req.UserReqDto;
import com.shell.core.convert.IConvert;
import com.shell.persistence.po.UserPo;
import org.mapstruct.Mapper;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/16 22:59
 * @Description
 */
@Mapper(componentModel = "spring")
public interface UserReqConvert extends IConvert<UserReqDto, UserPo> {
}
