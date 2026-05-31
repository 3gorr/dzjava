package com.csv.model;

import java.lang.reflect.Field;
import java.util.List;

public record ResolvedField(List<Field> path, String header) {
    public Field lastField() {
        return path.get(path.size() - 1);
    }
}
