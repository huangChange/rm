package com.shell.persistence.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shell.core.ddd.persistence.po.impl.AbstractPo;
import lombok.Data;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/14 0:06
 * @Description
 */
@Data
@TableName(value = "rm_user", resultMap = "UserResultMap")
public class UserPo extends AbstractPo {

    private static final Long serialVersionUID = 1L;

    private String username;

    private String pwd;

    private Integer age;

    private Integer sex;

}
