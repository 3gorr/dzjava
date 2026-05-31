package com.csv.util;

import com.csv.annotation.CsvName;
import com.csv.model.ResolvedField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldResolver {

    public static List<ResolvedField> resolve(Class<?> klass) {
        return resolve(klass, Collections.emptyList(), "");
    }

    private static List<ResolvedField> resolve(Class<?> klass, List<Field> parentPath, String prefix) {
        List<ResolvedField> result = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = getFieldName(field);
            String header = prefix.isEmpty() ? fieldName : prefix + "." + fieldName;
            List<Field> path = new ArrayList<>(parentPath);
            path.add(field);

            if (isLeaf(field.getType())) {
                result.add(new ResolvedField(path, header));
            } else {
                result.addAll(resolve(field.getType(), path, header));
            }
        }
        return result;
    }

    public static Object getValue(Object root, List<Field> path) throws IllegalAccessException {
        Object current = root;
        for (Field field : path) {
            if (current == null) return null;
            current = field.get(current);
        }
        return current;
    }

    public static void setValue(Object root, List<Field> path, Object value) throws Exception {
        Object current = root;
        for (int i = 0; i < path.size() - 1; i++) {
            Field field = path.get(i);
            Object next = field.get(current);
            if (next == null) {
                next = field.getType().getDeclaredConstructor().newInstance();
                field.set(current, next);
            }
            current = next;
        }
        path.get(path.size() - 1).set(current, value);
    }

    private static String getFieldName(Field field) {
        CsvName annotation = field.getAnnotation(CsvName.class);
        return annotation != null ? annotation.value() : field.getName();
    }

    private static boolean isLeaf(Class<?> type) {
        return type == String.class
            || type == int.class || type == Integer.class
            || type == long.class || type == Long.class
            || type == double.class || type == Double.class
            || type == boolean.class || type == Boolean.class
            || List.class.isAssignableFrom(type);
    }
}
