package com.shell.api.dto.req;

import com.shell.core.ddd.api.dto.impl.AbstractReqDto;
import lombok.Data;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:50
 * @Description
 */
@Data
public class UserReqDto extends AbstractReqDto {

    private final Long serialVersionUID = 1L;

    private String username;

    private String pwd;

    private Integer age;

    private Integer sex;

}
