package com.shell.core.mybatis.util.sql;

import com.shell.common.exception.ApiInputException;

/**
 * SQL 注入处理策略
 */
public interface SqlGuard {

    /**
     *
     * @param fieldOption
     * @return handled fieldOption
     * @throws ApiInputException
     */
    String handleFieldOption(String fieldOption) throws ApiInputException;

    /**
     *
     * @param jsonExpression
     * @return handled jsonExpression
     * @throws ApiInputException
     */
    String handleJsonExpression(String jsonExpression) throws ApiInputException;

    /**
     *
     * @param sortProperty
     * @return handled sortProperty
     * @throws ApiInputException
     */
    String handleSortProperty(String sortProperty) throws ApiInputException;

}
