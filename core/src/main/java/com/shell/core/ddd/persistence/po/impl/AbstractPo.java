package com.shell.core.ddd.persistence.po.impl;

import com.baomidou.mybatisplus.annotation.*;
import com.shell.common.utils.JsonUtils;
import com.shell.core.ddd.persistence.po.IPo;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/14 14:56
 * @Description
 */
@Data
public abstract class AbstractPo implements IPo {

    private static final Long serialVersionUID = 1L;

    public static final String ID = "id";

    public static final String CREATE_USER_ID = "createUserId";

    public static final String CREATE_USER_NAME = "createUserName";

    public static final String CREATE_TIME = "createTime";

    public static final String MODIFY_USER_ID = "modifyUserId";

    public static final String MODIFY_USER_NAME = "modifyUserName";

    public static final String MODIFY_TIME = "modifyTime";

    public static final String REVISION = "revision";

    public static final String DELETED = "deleted";

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    /**
     * 创建人名称
     */
    @TableField(fill = FieldFill.INSERT)
    private String createUserName;

    /**
     * 创建时间
     */
    @TableField(insertStrategy = FieldStrategy.NOT_NULL)
    private OffsetDateTime createTime;

    /**
     * 修改人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long modifyUserId;

    /**
     * 修改人名称
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long modifyUserName;

    /**
     * 修改时间
     */
    @TableField(update = "now()", fill = FieldFill.UPDATE, insertStrategy = FieldStrategy.NOT_NULL)
    private OffsetDateTime modifyTime;

    /**
     * 版本号
     */
    @TableField(update = "%s+1", fill = FieldFill.UPDATE, insertStrategy = FieldStrategy.NEVER)
    private Long revision;

    /**
     * 删除标识
     */
    @TableLogic(value = "false", delval = "true")
    @TableField(whereStrategy = FieldStrategy.NEVER)
    private Boolean deleted;

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }

}
