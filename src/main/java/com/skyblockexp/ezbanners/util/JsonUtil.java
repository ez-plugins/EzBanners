package com.skyblockexp.ezbanners.util;

import java.util.Collection;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return '"' + escape((String) value) + '"';
        }
        if (value instanceof Number || value instanceof Boolean) {
            return String.valueOf(value);
        }
        if (value instanceof Map) {
            return mapToJson((Map<?, ?>) value);
        }
        if (value instanceof Collection) {
            return collectionToJson((Collection<?>) value);
        }
        return '"' + escape(String.valueOf(value)) + '"';
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append('"').append(escape(String.valueOf(entry.getKey()))).append('"').append(':');
            builder.append(toJson(entry.getValue()));
        }
        builder.append('}');
        return builder.toString();
    }

    private static String collectionToJson(Collection<?> collection) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        boolean first = true;
        for (Object value : collection) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append(toJson(value));
        }
        builder.append(']');
        return builder.toString();
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\':
                    builder.append("\\\\");
                    break;
                case '"':
                    builder.append("\\\"");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                default:
                    if (c < 32) {
                        builder.append(String.format("\\u%04x", (int) c));
                    } else {
                        builder.append(c);
                    }
                    break;
            }
        }
        return builder.toString();
    }
}
