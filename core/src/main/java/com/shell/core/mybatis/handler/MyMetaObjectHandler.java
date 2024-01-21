package com.shell.core.mybatis.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.shell.core.context.Whoami;
import org.apache.ibatis.reflection.MetaObject;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:34
 * @Description
 */
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Whoami whoami = Whoami.get();
        OffsetDateTime now = OffsetDateTime.now();
        Object id = getFieldValByName("id", metaObject);
        if (Objects.isNull(id)) {
            setFieldValByName("id", IdWorker.getId(new Object()), metaObject);
        }
        Object createTime = getFieldValByName("createTime", metaObject);
        if (Objects.isNull(createTime)) {
            setFieldValByName("createTime", now, metaObject);
        }
        Object modifyTime = getFieldValByName("modifyTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("modifyTime", now, metaObject);
        }
        Object createUserId = getFieldValByName("createUserId", metaObject);
        if (Objects.isNull(createUserId)) {
            setFieldValByName("createUserId", whoami.getOperatorId(), metaObject);
        }
        Object createUserName = getFieldValByName("createUserName", metaObject);
        if (Objects.isNull(createUserName)) {
            setFieldValByName("createUserName", whoami.getOperatorName(), metaObject);
        }
        Object modifyUserId = getFieldValByName("modifyUserId", metaObject);
        if (Objects.isNull(modifyUserId)) {
            setFieldValByName("modifyUserId", whoami.getOperatorId(), metaObject);
        }
        Object modifyUserName = getFieldValByName("modifyUserName", metaObject);
        if (Objects.isNull(modifyUserName)) {
            setFieldValByName("modifyUserName", whoami.getOperatorName(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Whoami whoami = Whoami.get();
        Object modifyTime = getFieldValByName("modifyTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("modifyTime", OffsetDateTime.now(), metaObject);
        }
        Object modifyUserId = getFieldValByName("modifyUserId", metaObject);
        if (Objects.isNull(modifyUserId)) {
            setFieldValByName("modifyUserId", whoami.getOperatorId(), metaObject);
        }
        Object modifyUserName = getFieldValByName("modifyUserName", metaObject);
        if (Objects.isNull(modifyUserName)) {
            setFieldValByName("modifyUserName", whoami.getOperatorName(), metaObject);
        }
    }

}
