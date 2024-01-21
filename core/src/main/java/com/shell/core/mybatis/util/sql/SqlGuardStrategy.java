package com.shell.core.mybatis.util.sql;

import com.shell.common.exception.ApiInputException;

import java.util.regex.Pattern;

/**
 * @author shell
 * @version 1.0
 * @date ${DATE} ${TIME}
 * @Description 阻断SQL注入风险策略实现枚举类
 */
public enum SqlGuardStrategy implements SqlGuard {

    STRICT {
        @Override
        public String handleFieldOption(String fieldOption) throws ApiInputException {
            if (!STRICT_FIELDS_OPTION_VERIFIER.matcher(fieldOption).matches()) {
                throw new ApiInputException(String.format("Unsupported fieldOption: %s", fieldOption));
            }
            return fieldOption;
        }

        @Override
        public String handleJsonExpression(String jsonExpression) throws ApiInputException {
            if (!STRICT_JSON_EXPRESSION_VERIFIER.matcher(jsonExpression).matches()) {
                throw new ApiInputException(String.format("Unsupported jsonExpression: %s", jsonExpression));
            }
            return jsonExpression;
        }

        @Override
        public String handleSortProperty(String sortProperty) throws ApiInputException {
            if (!STRICT_SORT_PROPERTY_VERIFIER.matcher(sortProperty).matches()) {
                throw new ApiInputException(String.format("Unsupported sortProperty: %s", sortProperty));
            }
            return sortProperty;
        }
    },

    IGNORE {
        @Override
        public String handleFieldOption(String fieldOption) throws ApiInputException {
            return fieldOption;
        }

        @Override
        public String handleJsonExpression(String jsonExpression) throws ApiInputException {
            return jsonExpression;
        }

        @Override
        public String handleSortProperty(String sortProperty) throws ApiInputException {
            return sortProperty;
        }
    },
    ;

    /**
     * b.bookName-eq
     */
    private static final Pattern STRICT_FIELDS_OPTION_VERIFIER = Pattern.compile("^(?!.*--.*)[a-zA-Z0-9,._-]+$");

    private static final Pattern STRICT_JSON_EXPRESSION_VERIFIER = Pattern.compile("^(?!.*--.*)[a-zA-Z0-9,._-]+$");

    private static final Pattern STRICT_SORT_PROPERTY_VERIFIER = Pattern.compile("^(?!.*--.*)[a-zA-Z0-9,._-]+$");

}
