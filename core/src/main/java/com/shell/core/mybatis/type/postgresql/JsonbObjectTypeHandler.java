package com.shell.core.mybatis.type.postgresql;

import com.shell.common.json.JsonbObject;
import com.shell.common.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class JsonbObjectTypeHandler extends BaseTypeHandler<JsonbObject> {

    protected String toPGobjectType() {
        return "jsonb";
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonbObject parameter, JdbcType jdbcType) throws SQLException {

    }

    @Override
    public JsonbObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toJsonbObject(rs.getString(columnName));
    }

    @Override
    public JsonbObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toJsonbObject(rs.getString(columnIndex));
    }

    @Override
    public JsonbObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toJsonbObject(cs.getString(columnIndex));
    }

    private JsonbObject toJsonbObject(String jsonString) {
        if (jsonString == null) {
            return null;
        }
        return new JsonbObject(JsonUtils.fromJsonString(jsonString, LinkedHashMap.class));
    }
}
