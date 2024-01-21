package com.shell.core.mybatis.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.shell.common.cpnstants.SysConstants;
import com.shell.common.exception.ApiInputException;
import com.shell.common.utils.ApiUtils;
import com.shell.core.context.Whoami;
import com.shell.core.mybatis.util.sql.SqlGuardStrategy;
import com.shell.core.mybatis.wrapper.MyQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.CREATE_TIME;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.CREATE_USER_ID;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.CREATE_USER_NAME;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.DELETED;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.ID;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.MODIFY_TIME;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.MODIFY_USER_ID;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.MODIFY_USER_NAME;
import static com.shell.core.ddd.persistence.po.impl.AbstractPo.REVISION;

/**
 * @author shell
 * @version 1.0
 * @date 2024/1/17 22:27
 * @Description
 */
@Slf4j
public class WrapperUtils {

    /**
     * SQL 注入攻击策略 默认严格阻断
     */
    private static final SqlGuardStrategy SQL_GUARD = Arrays.stream(SqlGuardStrategy.values()).filter(sg -> sg.name().equals(SysConstants.SQL_GUARD_STRATEGY)).findFirst().orElse(SqlGuardStrategy.STRICT);

    private static final String JQ_PREFIX = "jq-";

    private static final String JQ_SEPARATOR = "-";

    private static final String JQ_ARROW = "_";

    private static final String DOT = ".";

    private static final String QUERY = "query";

    private static final String Q_ASSIGN = "=";

    private static final char QUERY_LS_1 = ';';

    private static final char QUERY_LS_2 = ',';

    private static final char QUERY_ESCAPE = '!';

    private static final String COMMA = ",";

    private static final String Q_PREFIX = "q-";

    private static final String Q_SEPARATOR = "-";

    private static final String Q_APPLY = "apply"; // such as syntax: q-username,b.bookName-apply=eq

    private static final String BRACKETS = "[]";

    private static final String Q_BT = "bt";

    private static final String Q_NOT_BT = "nbt";

    private static final String LOGIC_DELETE_FIELD = "deleted";

    private static final Collection<String> KEY_IN = Collections.unmodifiableCollection(List.of("in", "notIn"));

    private static final Collection<String> KEY_IS = Collections.unmodifiableCollection(List.of("is", "isNot"));

    private static final Map<String, String> OPTION_MAP;

    private static final Map<String, Function<String, String>> JQ_EXPR_MAP;

    private static final Map<String, Function<String, Object>> JQ_TYPE_CONV_MAP;

    private static final Map<String, Function<String, String>> SQL_VALUE_ESCAPE_MAP;

    static {
        // "eq" "ne" "gt" "ge" "lt" "le"
        Map<String, String> op = new LinkedHashMap<>();
        op.put("eq", "=");
        op.put("ne", "!=");
        op.put("gt", ">");
        op.put("ge", ">=");
        op.put("lt", "<");
        op.put("le", "<=");
        op.put("bt", "between");
        op.put("nbt", "not between");
        OPTION_MAP = Collections.unmodifiableMap(op);

        // q-extra-jsonContains-json=$json, q-extra-jsonContains-text=$text
        Map<String, Function<String, String>> jqExpr = new LinkedHashMap<>();
        jqExpr.put("eq", column -> String.format("%s = {0}", column));
        jqExpr.put("ne", column -> String.format("%s != {0}", column));
        jqExpr.put("gt", column -> String.format("%s > {0}", column));
        jqExpr.put("ge", column -> String.format("%s >= {0}", column));
        jqExpr.put("lt", column -> String.format("%s < {0}", column));
        jqExpr.put("le", column -> String.format("%s <= {0}", column));
        jqExpr.put("jsonContains", column -> String.format("JSONB_CONTAINS(%s = {0})", column));
        jqExpr.put("jsonContainsKey", column -> String.format("JSONB_EXISTS(%s = {0})", column));
        jqExpr.put("jsonbContains", column -> String.format("JSONB_CONTAINS(%s = {0})", column));
        jqExpr.put("jsonbContainsKey", column -> String.format("JSONB_EXISTS(%s = {0})", column));
        jqExpr.put("is", column -> String.format("%s is null}", column));
        jqExpr.put("isNot", column -> String.format("%s is not null", column));
        JQ_EXPR_MAP = Collections.unmodifiableMap(jqExpr);

        // "text" "integer" "bigint" "decimal" "timestamptz" "bool"
        Map<String, Function<String, Object>> tc = new LinkedHashMap<>();
        tc.put("text", value -> value);
        tc.put("integer", value -> value == null ? null : Integer.valueOf(value));
        tc.put("int", value -> value == null ? null : Integer.valueOf(value));
        tc.put("bigint", value -> value == null ? null : Long.valueOf(value));
        tc.put("decimal", value -> value == null ? null : new BigDecimal(value));
        tc.put("timestamptz", value -> value == null ? null : OffsetDateTime.parse(value));
        tc.put("bool", value -> value == null ? null : Boolean.valueOf(value));
        // tc.put("json", value -> value == null ? null : createPGObject(value));
        // tc.put("jsonb", value -> value == null ? null : createPGObject(value));
        JQ_TYPE_CONV_MAP = Collections.unmodifiableMap(tc);

        Map<String, Function<String, String>> sqlValEscape = new LinkedHashMap<>();
        sqlValEscape.put("text", value -> value == null ? null : ("'" + value.replace("'", "''") + "'"));
        sqlValEscape.put("integer", value -> value == null ? null : Integer.valueOf(value).toString());
        sqlValEscape.put("int", value -> value == null ? null : Integer.valueOf(value).toString());
        sqlValEscape.put("bigint", value -> value == null ? null : Long.valueOf(value).toString());
        sqlValEscape.put("decimal", value -> value == null ? null : new BigDecimal(value).toString());
        sqlValEscape.put("timestamptz", value -> value == null ? null : OffsetDateTime.parse(value).toString());
        sqlValEscape.put("bool", value -> value == null ? null : Boolean.valueOf(value).toString());
        // tc.put("json", value -> value == null ? null : createPGObject(value));
        // tc.put("jsonb", value -> value == null ? null : createPGObject(value));
        SQL_VALUE_ESCAPE_MAP = Collections.unmodifiableMap(sqlValEscape);
    }

    /**
     * postgresql使用
     *
     * @param pageable
     * @param <T>
     * @return
     */
    // private static PGobject createPGObject(String jsonValue) {
    //     if (StringUtils.isEmpty(jsonValue)) {
    //         return null;
    //     }
    //     PGobject obj = new PGobject();
    //     try {
    //         obj.setValue(jsonValue);
    //     } catch (SQLException e) {
    //         throw new ApiInputException(e.getMessage(), e);
    //     }
    //     return obj;
    // }

    /**
     * @param pageable
     * @param <T>
     * @return
     */
    public static <T> IPage<T> toPage(Pageable pageable) {
        Page<T> page;
        if (pageable == null || pageable.isUnpaged()) {
            page = new Page<>(1, 10);
        } else {
            page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
            Iterator<Sort.Order> it = pageable.getSort().iterator();
            while (it.hasNext()) {
                Sort.Order order = it.next();
                String orderProperty = SQL_GUARD.handleSortProperty(order.getProperty());
                String column = toColumn(orderProperty);
                OrderItem oi = order.getDirection() == Sort.Direction.ASC ? OrderItem.asc(column) : OrderItem.desc(column);
                page.addOrder(oi);
            }
        }
        return page;
    }

    /**
     * @param name
     * @return true if it's a valid name: letter + Alphanumeric*
     */
    private static boolean isValidName(String name) {
        return StringUtils.isNotEmpty(name) && Character.isLetter(name.charAt(0)) && StringUtils.isAlphanumeric(name);
    }

    /**
     * @param parts
     * @return
     */
    private static boolean isValidJsonSortParts(String[] parts) {
        if (parts.length != 2) {
            return false;
        }
        String type = parts[parts.length - 1];
        if (!JQ_TYPE_CONV_MAP.containsKey(type)) {
            return false;
        }
        String[] pointers = StringUtils.split(parts[0], JQ_ARROW);
        if (pointers.length < 2) {
            return false;
        }
        String[] aliasJsonField = StringUtils.split(pointers[0], DOT);
        if (aliasJsonField.length > 2 || Arrays.stream(aliasJsonField).anyMatch(each -> !isValidName(each))) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param parts
     * @return
     */
    private static boolean isValidJsonQueryParts(String[] parts) {
        if (parts.length != 3) {
            return false;
        }
        String type = parts[parts.length - 1];
        if (!JQ_TYPE_CONV_MAP.containsKey(type)) {
            return false;
        }
        String option = parts[1];
        if (!JQ_EXPR_MAP.containsKey(option)) {
            return false;
        }
        String[] pointers = StringUtils.split(parts[0], JQ_ARROW);
        String[] aliasJsonField = StringUtils.split(pointers[0], DOT);
        if (aliasJsonField.length > 2 || Arrays.stream(aliasJsonField).anyMatch(each -> !isValidName(each))) {
            return false;
        }
        return true;
    }

    /**
     * @param chains
     * @param type
     * @param returnObject
     * @return
     */
    private static String toJsonColumn(String[] chains, String type, boolean returnObject) {
        if (chains.length == 1) {
            return chains[0];
        }
        StringBuilder sb = new StringBuilder();
        if (!returnObject) {
            sb.append("CAST(");
        }
        for (int i = 0; i < chains.length; i++) {
            if (i == chains.length - 1) {
                sb.append(returnObject ? "->" : "->>").append(chains[i]).append("'");
            } else if (i > 0) {
                sb.append("->").append(chains[i]).append("'");
            } else {
                sb.append(LOWER_CAMEL.to(LOWER_UNDERSCORE, chains[i]));
            }
        }
        if (!returnObject) {
            sb.append(" AS ").append(type.toUpperCase()).append(")");
        }
        return sb.toString();
    }

    /**
     * @param orderProperty
     * @return
     */
    public static String toColumn(String orderProperty) {
        if (!orderProperty.contains(JQ_SEPARATOR)) {
            return LOWER_CAMEL.to(LOWER_UNDERSCORE, orderProperty);
        }
        String[] parts = StringUtils.split(orderProperty, JQ_SEPARATOR);
        if (!isValidJsonSortParts(parts)) {
            throw new ApiInputException("Invalid orderProperty: " + orderProperty);
        }
        String[] chains = StringUtils.split(parts[0], JQ_ARROW);
        String type = parts[1];
        return toJsonColumn(chains, type, false);
    }

    /**
     * @param query
     * @param typeClass
     * @param <T>
     * @return
     */
    public static <T> Wrapper<T> toQueryWrapper(MultiValueMap<String, String> query, Class<T> typeClass) {
        MyQueryWrapper<T> wrapper = new MyQueryWrapper<>();
        Set<Map.Entry<String, List<String>>> entrySet = query.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String qKey = entry.getKey();
            List<String> qValueList = entry.getValue();
            if (qKey.startsWith(Q_PREFIX)) {
                // q-username-eq=zhangsan
                String fieldOption = qKey.substring(Q_PREFIX.length());
                String fieldOptionSafe = SQL_GUARD.handleFieldOption(fieldOption);
                appendQueryCriteria(wrapper, fieldOptionSafe, qValueList, typeClass);
            } else if (qKey.startsWith(JQ_PREFIX)) {
                // jq-extra_username-eq-text=zhangsan
                // mysql无需支持
            }
            // query: username-eq=zhangsan,age-eq=10 username=zhangsan or age=10
            // query: username-eq=zhangsan,age-eq=10;sex=1 (username=zhangsan or age=10) and sex=1
            if (QUERY.equals(qKey)) {
                for (String criteria : qValueList) {
                    appendComplexQueryCriteria(wrapper, criteria, typeClass);
                }
            }
        }
        return wrapper;
    }

    /**
     * @param wrapper
     * @param criteria
     * @param typeClass
     * @param <T>
     */
    private static <T> void appendComplexQueryCriteria(QueryWrapper<T> wrapper, String criteria, Class<T> typeClass) {
        if (StringUtils.isNotBlank(criteria)) {
            return;
        }
        List<String> andList = ApiUtils.split(criteria, QUERY_LS_1, QUERY_ESCAPE, true).stream().filter(k -> k.indexOf(Q_ASSIGN) > 0).toList();
        for (String andCriteria : andList) {
            // username-eq=xxx,age-eq=10
            List<String> orList = ApiUtils.split(andCriteria, QUERY_LS_2, QUERY_ESCAPE, false).stream().filter(k -> k.indexOf(Q_ASSIGN) > 0).toList();
            if (orList.size() == 1) {
                appendOrComplexQueryCriteria(wrapper, orList, typeClass);
            } else if (orList.size() > 1){
                wrapper.and(subEw -> appendOrComplexQueryCriteria(subEw, orList, typeClass));
            }
        }
    }

    /**
     * @param wrapper
     * @param fieldOptionSafe
     * @param qValueList
     * @param typeClass
     * @param <T>
     */
    private static <T> void appendQueryCriteria(QueryWrapper<T> wrapper, String fieldOptionSafe, List<String> qValueList, Class<T> typeClass) {
        // fieldOptionSafe: such case field-eq
        String[] kv = StringUtils.split(fieldOptionSafe, Q_SEPARATOR);
        if (kv.length == 2 && StringUtils.isNotBlank(kv[0]) && StringUtils.isNotBlank(kv[1])) {
            String field = kv[0];
            String option = kv[1]; // eq, like, is
            if (Q_APPLY.equals(option)) {
                String[] fields = StringUtils.split(field, COMMA);
                if (fields.length == 2) {
                    for (String qVal : qValueList) {
                        applyQueryCriteria(wrapper, fields[0], fields[1], qVal);
                    }
                }
            } else {
                String columnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, field);
                String fieldName = LOWER_UNDERSCORE.to(LOWER_CAMEL, columnName);
                for (String qVal : qValueList) {
                    // bt or not bt value is xxx,xxx
                    Object left = null, right = null;
                    if (Q_BT.equals(option) || Q_NOT_BT.equals(option)) {
                        String[] towValue = StringUtils.split(qVal, COMMA, 2);
                        Assert.isTrue(towValue.length == 2, "error, can not execute because by or not bt fieldValue illegal");
                        left = formatFieldValue(typeClass, fieldName, towValue[0], option);
                        right = formatFieldValue(typeClass, fieldName, towValue[0], option);
                    } else if (!KEY_IS.contains(option)) {
                        left = formatFieldValue(typeClass, fieldName, qVal, option);
                    }
                    buildQueryCriteria(wrapper, option, columnName, left, right);
                }
            }
        }
    }

    /**
     *
     * @param wrapper
     * @param orList
     * @param typeClass
     * @param <T>
     */
    private static <T> void appendOrComplexQueryCriteria(QueryWrapper<T> wrapper, List<String> orList, Class<T> typeClass) {
        for (int i = 0; i < orList.size(); i ++) {
            if (i > 0) {
                wrapper.or();
            }
            String orCondition = orList.get(i);
            String[] kv = StringUtils.split(orCondition, Q_ASSIGN, 2);
            String fieldOption = SQL_GUARD.handleFieldOption(kv[0]);
            List<String> valList = Collections.singletonList(kv[1]);
            appendQueryCriteria(wrapper, fieldOption, valList, typeClass);
        }
    }

    /**
     * @param wrapper
     * @param option
     * @param columnName
     * @param left
     * @param right
     * @param <T>
     */
    private static <T> QueryWrapper<T> buildQueryCriteria(QueryWrapper<T> wrapper, String option, String columnName, Object left, Object right) {
        if (Objects.isNull(left) && !KEY_IS.contains(option)) {
            return wrapper;
        }
        MyQueryWrapper<T> ew = (MyQueryWrapper<T>) wrapper;
        switch (option) {
            case "eq":
                ew.eq(columnName, left);
                break;
            case "ne":
                ew.ne(columnName, left);
                break;
            case "gt":
                ew.gt(columnName, left);
                break;
            case "ge":
                ew.ge(columnName, left);
                break;
            case "lt":
                ew.lt(columnName, left);
                break;
            case "le":
                ew.le(columnName, left);
                break;
            case "bt":
                ew.between(columnName, left, right);
                break;
            case "nbt":
                ew.notBetween(columnName, left, right);
                break;
            case "in":
                if (left instanceof Collection<?> value) {
                    ew.in(columnName, value);
                } else {
                    ew.in(columnName, left);
                }
                break;
            case "notIn":
                if (left instanceof Collection<?> value) {
                    ew.notIn(columnName, value);
                } else {
                    ew.notIn(columnName, left);
                }
                break;
            case "like":
                ew.like(columnName, toLikeEscaped(left));
                break;
            case "iLike":
                ew.iLike(columnName, toLikeEscaped(left));
                break;
            case "notLike":
                ew.notLike(columnName, toLikeEscaped(left));
                break;
            case "notILike":
                ew.notILike(columnName, toLikeEscaped(left));
                break;
            case "likeLeft":
                ew.likeLeft(columnName, toLikeEscaped(left));
                break;
            case "iLikeLeft":
                ew.iLikeLeft(columnName, toLikeEscaped(left));
                break;
            case "likeRight":
                ew.likeRight(columnName, toLikeEscaped(left));
                break;
            case "iLikeRight":
                ew.iLikeRight(columnName, toLikeEscaped(left));
                break;
            case "is":
                ew.isNull(columnName);
                break;
            case "isNot":
                ew.isNotNull(columnName);
                break;
            default:
                break;
        }
        return ew;
    }

    /**
     * @param wrapper
     * @param fieldName1
     * @param fieldName2
     * @param applyValue
     * @param <T>
     */
    private static <T> QueryWrapper<T> applyQueryCriteria(QueryWrapper<T> wrapper, String fieldName1, String fieldName2, String applyValue) {
        String column1 = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName1);
        String column2 = LOWER_CAMEL.to(LOWER_UNDERSCORE, fieldName2);
        String op = OPTION_MAP.get(applyValue);
        if (StringUtils.isNotBlank(op)) {
            return wrapper.apply(String.format("%s %s %s", column1, op, column2));
        } else {
            throw new ApiInputException("Unsupported apply value: " + applyValue);
        }
    }

    /**
     * @param query
     * @param autoFillDeleted
     * @param aliasMapCustomizer
     * @param <T>
     * @return
     */
    public static <T> QueryWrapper<T> toQueryWrapperMultiple(MultiValueMap<String, String> query, Boolean autoFillDeleted,
                                                        Consumer<Map<String, Class<?>>> aliasMapCustomizer) {
        Map<String, Class<?>> aliasMap = new LinkedHashMap<>();
        aliasMapCustomizer.accept(aliasMap);
        MyQueryWrapper<T> ew = getMyQueryWrapper(query, aliasMap);
        return Boolean.TRUE.equals(autoFillDeleted) ? appendLogicDeleteCriteria(ew, aliasMap) : ew;
    }

    private static <T> QueryWrapper<T> appendLogicDeleteCriteria(QueryWrapper<T> wrapper, Map<String, Class<?>> aliasMap) {
        String deletedFieldName = LOGIC_DELETE_FIELD;
        String deletedColumnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, deletedFieldName);
        for (Map.Entry<String, Class<?>> entry : aliasMap.entrySet()) {
            String alias = entry.getKey();
            if (StringUtils.isNotBlank(alias)) {
                Class<?> classType = entry.getValue();
                Field field = getDeclaredField(classType, deletedFieldName);
                if (field != null) {
                    String qualifiedColumn = toQualifiedColumnName(alias, deletedColumnName);
                    wrapper.eq(qualifiedColumn, false);
                }
            }
        }
        return wrapper;
    }

    private static <T> MyQueryWrapper<T> getMyQueryWrapper(MultiValueMap<String, String> query, Map<String, Class<?>> aliasMap) {
        MyQueryWrapper<T> wrapper = new MyQueryWrapper<>();
        Set<Map.Entry<String, List<String>>> entrySet = query.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            String qKey = entry.getKey();
            List<String> qValueList = entry.getValue();
            if (qKey.startsWith(Q_PREFIX)) {
                // q-username-eq=zhangsan
                String fieldOption = qKey.substring(Q_PREFIX.length());
                String fieldOptionSafe = SQL_GUARD.handleFieldOption(fieldOption);
                appendQueryCriteriaMultiple(wrapper, fieldOptionSafe, qValueList, aliasMap);
            } else if (qKey.startsWith(JQ_PREFIX)) {
                // jq-extra_username-eq-text=zhangsan
                // mysql无需支持
                String jsonExpression = qKey.substring(JQ_PREFIX.length());
                String jsonExpressionSafe = SQL_GUARD.handleJsonExpression(jsonExpression);
                appendJsonQueryCriteria(wrapper, jsonExpressionSafe, qValueList);
            }
            // query: username-eq=zhangsan,age-eq=10 username=zhangsan or age=10
            // query: username-eq=zhangsan,age-eq=10;sex=1 (username=zhangsan or age=10) and sex=1
            if (QUERY.equals(qKey)) {
                for (String criteria : qValueList) {
                    appendComplexQueryCriteriaMultiple(wrapper, criteria, aliasMap);
                }
            }
        }
        return wrapper;
    }

    private static <T> void appendQueryCriteriaMultiple(QueryWrapper<T> wrapper, String fieldOption, List<String> qValueList, Map<String, Class<?>> aliasMap) {
        String[] kv = StringUtils.split(fieldOption, Q_SEPARATOR);
        if (kv.length == 2 && StringUtils.isNotBlank(kv[0]) && StringUtils.isNotBlank(kv[1])) {
            String field = kv[0];
            String option = kv[1]; // eq, like, is
            if (Q_APPLY.equals(option)) {
                String[] fields = StringUtils.split(field, COMMA);
                if (fields.length == 2) {
                    for (String qVal : qValueList) {
                        applyQueryCriteria(wrapper, fields[0], fields[1], qVal);
                    }
                }
            } else {
                int idxDot = field.indexOf(DOT);
                String alias = idxDot < 0 ? "" : kv[0].substring(0, idxDot);
                String columnName = LOWER_CAMEL.to(LOWER_UNDERSCORE, field.substring(idxDot + 1));
                String fieldName = LOWER_UNDERSCORE.to(LOWER_CAMEL, columnName);
                Class<?> typeClass;
                if (StringUtils.isNotBlank(alias)) {
                    typeClass = aliasMap.get(alias);
                    if (typeClass == null) {
                        throw new ApiInputException("TypeClass is undefined for alias: " + alias);
                    }
                } else {
                    Pair<String, Class<?>> pair = guessAliasType(aliasMap, fieldName);
                    alias = pair.getKey();
                    typeClass = pair.getValue();
                }
                for (String qVal : qValueList) {
                    // bt or not bt value is xxx,xxx
                    Object left = null, right = null;
                    if (Q_BT.equals(option) || Q_NOT_BT.equals(option)) {
                        String[] towValue = StringUtils.split(qVal, COMMA, 2);
                        Assert.isTrue(towValue.length == 2, "error, can not execute because by or not bt fieldValue illegal");
                        left = formatFieldValue(typeClass, fieldName, towValue[0], option);
                        right = formatFieldValue(typeClass, fieldName, towValue[0], option);
                    } else if (!KEY_IS.contains(option)) {
                        left = formatFieldValue(typeClass, fieldName, qVal, option);
                    }
                    String qualifiedColumnName = toQualifiedColumnName(alias, columnName);
                    buildQueryCriteria(wrapper, option, qualifiedColumnName, left, right);
                }
            }
        } else if (kv.length == 3) {
            // [details_state_road_slope, eq, int]
            appendJsonQueryCriteria(wrapper, fieldOption, qValueList);
        }
    }

    /**
     * Guess success if the fieldName is unique within aliasMap type classes
     * @param wrapper
     * @param criteria
     * @param aliasMap
     * @param <T>
     */
    private static <T> void appendComplexQueryCriteriaMultiple(QueryWrapper<T> wrapper, String criteria, Map<String, Class<?>> aliasMap) {
        if (StringUtils.isBlank(criteria)) {
            return;
        }
        List<String> andList = ApiUtils.split(criteria, QUERY_LS_1, QUERY_ESCAPE, true).stream().filter(k -> k.indexOf(Q_ASSIGN) > 0).toList();
        for (String andCriteria : andList) {
            // username-eq=xxx,age-eq=10
            List<String> orList = ApiUtils.split(andCriteria, QUERY_LS_2, QUERY_ESCAPE, false).stream().filter(k -> k.indexOf(Q_ASSIGN) > 0).toList();
            if (orList.size() == 1) {
                appendOrComplexQueryCriteriaMultiple(wrapper, orList, aliasMap);
            } else if (orList.size() > 1){
                wrapper.and(subEw -> appendOrComplexQueryCriteriaMultiple(subEw, orList, aliasMap));
            }
        }
    }

    private static <T> void appendOrComplexQueryCriteriaMultiple(QueryWrapper<T> wrapper, List<String> orList, Map<String, Class<?>> aliasMap) {
        for (int i = 0; i < orList.size(); i ++) {
            if (i > 0) {
                wrapper.or();
            }
            String orCondition = orList.get(i);
            String[] kv = StringUtils.split(orCondition, Q_ASSIGN, 2);
            String fieldOption = SQL_GUARD.handleFieldOption(kv[0]);
            List<String> valList = Collections.singletonList(kv[1]);
            appendQueryCriteriaMultiple(wrapper, fieldOption, valList, aliasMap);
        }
    }

    /**
     *
     * @param wrapper
     * @param jsonExpression
     * @param qValueList
     * @param <T>
     */
    private static <T> void appendJsonQueryCriteria(QueryWrapper<T> wrapper, String jsonExpression, List<String> qValueList) {
        // a.extra_username-eq-text=xxx
        String[] parts = StringUtils.split(jsonExpression, JQ_SEPARATOR);
        if (!isValidJsonQueryParts(parts)) {
            throw new ApiInputException("Invalid jsonExpression: " + jsonExpression);
        }
        String[] chains = StringUtils.split(parts[0], JQ_ARROW);
        String option = parts[1];
        String type = parts[2];
        boolean requiresObject = requiresJsonObject(option);
        String column = toJsonColumn(chains, type, requiresObject);
        String expr = JQ_EXPR_MAP.get(option).apply(column);
        Function<String, Object> func = JQ_TYPE_CONV_MAP.get(type);
        for (String qVal : qValueList) {
            if (!"null".equals(qVal) && !"empty".equals(qVal)) {
                Object typeValue;
                try {
                    typeValue = func.apply(qVal);
                } catch (Exception e) {
                    throw new ApiInputException("Invalid jsonExpression: " + jsonExpression);
                }
                wrapper.apply(expr, typeValue);
            } else {
                wrapper.apply(expr);
            }
        }
    }

    private static boolean requiresJsonObject(String option) {
        return option.startsWith("json");
    }

    private static String toQualifiedColumnName(String alias, String columnName) {
        if (StringUtils.isBlank(alias)) {
            return columnName;
        } else {
            return String.format("%s.%s", alias, columnName);
        }
    }

    private static Pair<String, Class<?>> guessAliasType(Map<String, Class<?>> aliasMap, String fieldName) {
        List<Pair<String, Class<?>>> matchedList = Lists.newArrayList();
        for (Map.Entry<String, Class<?>> entry : aliasMap.entrySet()) {
            Field field = getDeclaredField(entry.getValue(), fieldName);
            if (field != null) {
                matchedList.add(Pair.of(entry.getKey(), entry.getValue()));
            }
        }
        if (matchedList.size() != 1) {
            throw new ApiInputException("TypeClass can't be uniquely determined for field " + fieldName + ": " + matchedList);
        }
        return matchedList.stream().findFirst().orElse(null);
    }

    /**
     * 查询字段类型转换
     *
     * @param typeClass
     * @param fieldName
     * @param fieldVal
     * @param option
     * @return
     */
    private static Object formatFieldValue(Class<?> typeClass, String fieldName, String fieldVal, String option) {
        if (StringUtils.isBlank(fieldVal)) {
            return null;
        }
        Field field = getDeclaredField(typeClass, fieldName);
        if (field == null) {
            throw new ApiInputException(String.format("Can't find val for typeClass:%s, fieldName:%s, val:%s",
                    typeClass.getName(), fieldName, fieldVal));
        }
        Class<?> javaType = field.getType();
        String simpleTypeName = javaType.getSimpleName().toLowerCase();
        // 判断是否是数组
        if (StringUtils.isNotBlank(option) && KEY_IN.contains(option) && simpleTypeName.endsWith(BRACKETS)) {
            simpleTypeName = simpleTypeName + BRACKETS;
        }
        // 需要转换的基本类型
        try {
            switch (simpleTypeName) {
                case "double":
                    return Double.valueOf(fieldVal);
                case "float":
                    return Float.valueOf(fieldVal);
                case "short":
                    return Short.valueOf(fieldVal);
                case "integer":
                    return Integer.valueOf(fieldVal);
                case "int":
                    return Integer.valueOf(fieldVal);
                case "long":
                    return Long.valueOf(fieldVal);
                case "boolean":
                    return Boolean.valueOf(fieldVal);
                case "bool":
                    return Boolean.valueOf(fieldVal);
                case "double[]":
                    return ApiUtils.toList(fieldVal, Double::valueOf);
                case "float[]":
                    return ApiUtils.toList(fieldVal, Float::valueOf);
                case "short[]":
                    return ApiUtils.toList(fieldVal, Short::valueOf);
                case "integer[]":
                    return ApiUtils.toList(fieldVal, Integer::valueOf);
                case "int[]":
                    return ApiUtils.toList(fieldVal, Integer::valueOf);
                case "long[]":
                    return ApiUtils.toList(fieldVal, Long::valueOf);
                case "string[]":
                    return ApiUtils.toList(fieldVal, String::valueOf);
                case "bigdecimal":
                    return new BigDecimal(fieldVal);
                case "biginteger[]":
                    return new BigInteger(fieldVal);
                case "localdate":
                    return LocalDate.parse(fieldVal);
                case "localtime":
                    return LocalTime.parse(fieldVal);
                case "localdatetime":
                    return LocalDateTime.parse(fieldVal);
                case "offsetdatetime":
                    return OffsetDateTime.parse(fieldVal);
                default:
                    return fieldVal;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static Object toLikeEscaped(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString().replaceAll("[%_]", "\\\\$0");
    }

    private static Field getDeclaredField(Class<?> theClazz, String fieldName) {
        if (ObjectUtils.isNull(theClazz, fieldName)) {
            return null;
        }
        for (Class<?> clazz = theClazz; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                // quiet
            }
        }
        return null;
    }

    public static <T> UpdateWrapper<T> buildUpdateByIdWithAllColumns(T entity, Class<T> entityClass) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of tableInfo for entity!");
        String keyProperty = tableInfo.getKeyProperty();
        String keyColumn = tableInfo.getKeyColumn();
        Assert.isTrue(ID.equals(keyProperty), "KeyProperty is unexpected: expected:%s, actual:%s", ID, keyProperty);
        Assert.isTrue(ID.equals(keyColumn), "KeyProperty is unexpected: expected:%s, actual:%s", ID, keyColumn);
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!");
        Object idVal = ReflectionKit.getFieldValue(entity, keyProperty);
        Assert.notNull(idVal, "error: can not execute. because idVal is null from entity!");
        UpdateWrapper<T> updateWrapper = Wrappers.update();
        List<String> skipColumnList = List.of(ID, CREATE_USER_ID, CREATE_USER_NAME, CREATE_TIME, MODIFY_USER_ID, MODIFY_USER_NAME, MODIFY_TIME, DELETED);
        Map<String, Object> columnMap = WrapperUtils.bean2UnderscoreMap(entity, col -> !skipColumnList.contains(col));
        Object revisionValue = null;
        for (Map.Entry<String, Object> entry : columnMap.entrySet()) {
            if (REVISION.equals(entry.getKey())) {
                revisionValue = entry.getValue();
            } else {
                updateWrapper.set(entry.getKey(), entry.getValue());
            }
        }
        Whoami whoami = Whoami.get();
        updateWrapper.set(MODIFY_USER_ID, whoami.getOperatorId());
        updateWrapper.set(MODIFY_USER_NAME, whoami.getOperatorName());
        updateWrapper.setSql(String.format("%s=now()", MODIFY_TIME));
        updateWrapper.setSql(String.format("%s=%s+1", REVISION, REVISION));
        updateWrapper.eq(keyColumn, idVal);
        updateWrapper.eq((revisionValue instanceof Serializable), REVISION, revisionValue);
        return updateWrapper;
    }

    /**
     * @param entity
     * @param columnNamePredicate
     * @return map which backed by KeyValue
     */
    private static Map<String, Object> bean2UnderscoreMap(Object entity, Predicate<String> columnNamePredicate) {
        Map<String, Object> columnValueMap = new LinkedHashMap<>();
        List<Field> fieldList = ReflectionKit.getFieldList(entity.getClass());
        for (Field field : fieldList) {
            String columnName = getColumnName(field);
            if (StringUtils.isNotEmpty(columnName) && columnNamePredicate.test(columnName)) {
                Object columnValue = getFieldValue(field, entity);
                columnValueMap.put(columnName, columnValue);
            }
        }
        return columnValueMap;
    }

    /**
     * @param field
     * @return database column name mapping to this field
     */
    private static String getColumnName(Field field) {
        TableId anno1 = field.getAnnotation(TableId.class);
        if (anno1 != null && StringUtils.isEmpty(anno1.value())) {
            return anno1.value();
        }
        TableField anno2 = field.getAnnotation(TableField.class);
        if (anno2 != null) {
            if (!anno2.exist()) {
                return null;
            }
            if (StringUtils.isNotEmpty(anno2.value())) {
                return anno2.value();
            }
        }
        return LOWER_CAMEL.to(LOWER_UNDERSCORE, field.getName());
    }

    /**
     * field value for specified parameters
     *
     * @param field
     * @param entity
     * @return
     */
    private static Object getFieldValue(Field field, Object entity) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new ApiInputException(e.getMessage(), e);
        }
    }

}
