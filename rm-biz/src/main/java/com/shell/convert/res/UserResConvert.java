package com.shell.convert.res;

import com.shell.api.dto.res.UserResDto;
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
public interface UserResConvert extends IConvert<UserResDto, UserPo> {
}
