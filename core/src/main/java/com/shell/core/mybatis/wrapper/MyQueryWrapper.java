package com.shell.core.mybatis.wrapper;

import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:27
 * @Description
 */
public class MyQueryWrapper<T> extends QueryWrapper<T> {

    private static final Long serialVersionUID = 1L;

    private static final String ILIKE = "ILIKE";

    private static final String NOT_ILIKE = "NOT ILIKE";

    public MyQueryWrapper() {
        super();
    }

    public MyQueryWrapper(T entity, String ...columns) {
        super(entity, columns);
    }

    public MyQueryWrapper(T entity) {
        super(entity);
    }

    /**
     * ILIKE '%val%'
     * @param column
     * @param val
     * @return
     */
    public MyQueryWrapper<T> iLike(String column, Object val) {
        return (MyQueryWrapper<T>) maybeDo(true, () -> appendSqlSegments(() -> column, () -> ILIKE, () -> formatSqlMaybeWithParam("{0}", SqlUtils.concatLike(val, SqlLike.DEFAULT))));
    }

    /**
     * NOT ILIKE '%val%'
     * @param column
     * @param val
     * @return
     */
    public MyQueryWrapper<T> notILike(String column, Object val) {
        return (MyQueryWrapper<T>) maybeDo(true, () -> appendSqlSegments(() -> column, () -> NOT_ILIKE, () -> formatSqlMaybeWithParam("{0}", SqlUtils.concatLike(val, SqlLike.DEFAULT))));
    }

    /**
     * ILIKE '%val'
     * @param column
     * @param val
     * @return
     */
    public MyQueryWrapper<T> iLikeLeft(String column, Object val) {
        return (MyQueryWrapper<T>) maybeDo(true, () -> appendSqlSegments(() -> column, () -> NOT_ILIKE, () -> formatSqlMaybeWithParam("{0}", SqlUtils.concatLike(val, SqlLike.LEFT))));
    }

    /**
     * ILIKE 'val%'
     * @param column
     * @param val
     * @return
     */
    public MyQueryWrapper<T> iLikeRight(String column, Object val) {
        return (MyQueryWrapper<T>) maybeDo(true, () -> appendSqlSegments(() -> column, () -> NOT_ILIKE, () -> formatSqlMaybeWithParam("{0}", SqlUtils.concatLike(val, SqlLike.RIGHT))));
    }

    /**
     * 用于生成嵌套 sql
     * @return
     */
    @Override
    protected MyQueryWrapper<T> instance() {
        return new MyQueryWrapper<>(getEntity(), getEntityClass(), paramNameSeq, paramNameValuePairs,
        new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    private MyQueryWrapper(T entity, Class<T> entityClass, AtomicInteger paramNameSeq, Map<String, Object> paramNameValuePairs,
                           MergeSegments mergeSegments, SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        super.paramNameSeq = paramNameSeq;
        super.paramNameValuePairs = paramNameValuePairs;
        super.expression = mergeSegments;
        super.lastSql = lastSql;
        super.sqlComment = sqlComment;
        super.sqlFirst = sqlFirst;
    }

}
