package com.shell.api.dto.res;

import com.shell.core.ddd.api.dto.impl.AbstractResDto;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/13 23:50
 * @Description
 */
@Data
public class UserResDto extends AbstractResDto {

    private static final Long serialVersionUID = 1L;

    private Long id;

    private OffsetDateTime createTime;

    private Long createUserId;

    private String createUserName;

    private OffsetDateTime modifyTime;

    private Long modifyUserId;

    private String modifyUserName;

    private Long revision;

    private String username;

    private String pwd;

    private Integer age;

    private Integer sex;

}
