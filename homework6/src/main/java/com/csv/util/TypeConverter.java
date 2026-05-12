package com.csv.util;

import com.csv.annotation.CsvCollection;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TypeConverter {

    public static Object convert(String value, Field field) {
        Class<?> type = field.getType();

        if (List.class.isAssignableFrom(type)) {
            String delimiter = getDelimiter(field);
            Class<?> elementType = getListElementType(field);
            if (value == null || value.isEmpty()) return new ArrayList<>();
            return Arrays.stream(value.split(Pattern.quote(delimiter)))
                .map(s -> convertPrimitive(s.trim(), elementType))
                .collect(Collectors.toCollection(ArrayList::new));
        }

        return convertPrimitive(value, type);
    }

    public static String toString(Object value, Field field) {
        if (value instanceof List<?> list) {
            String delimiter = getDelimiter(field);
            return list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
        }
        return value == null ? "" : value.toString();
    }

    private static Object convertPrimitive(String value, Class<?> type) {
        if (value == null || value.isEmpty()) return null;
        if (type == String.class) return value;
        if (type == int.class || type == Integer.class) return Integer.parseInt(value);
        if (type == long.class || type == Long.class) return Long.parseLong(value);
        if (type == double.class || type == Double.class) return Double.parseDouble(value);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(value);
        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    private static String getDelimiter(Field field) {
        CsvCollection annotation = field.getAnnotation(CsvCollection.class);
        return annotation != null ? annotation.delimiter() : "|";
    }

    private static Class<?> getListElementType(Field field) {
        ParameterizedType paramType = (ParameterizedType) field.getGenericType();
        return (Class<?>) paramType.getActualTypeArguments()[0];
    }
}
