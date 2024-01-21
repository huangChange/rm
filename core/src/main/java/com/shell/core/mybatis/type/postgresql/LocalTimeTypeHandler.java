package com.shell.core.mybatis.type.postgresql;

import com.shell.common.json.JsonbObject;
import com.shell.common.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.time.LocalTime;
import java.util.LinkedHashMap;

public class LocalTimeTypeHandler extends BaseTypeHandler<LocalTime> {

    protected String toPGobjectType() {
        return "jsonb";
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, LocalTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setTime(i, Time.valueOf(parameter));
    }

    @Override
    public LocalTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Time time = rs.getTime(columnName);
        return getLocalTime(time);
    }

    @Override
    public LocalTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Time time = rs.getTime(columnIndex);
        return getLocalTime(time);
    }

    @Override
    public LocalTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Time time = cs.getTime(columnIndex);
        return getLocalTime(time);
    }

    private LocalTime getLocalTime(Time time) {
        if (time != null) {
            return time.toLocalTime();
        }
        return null;
    }
}
