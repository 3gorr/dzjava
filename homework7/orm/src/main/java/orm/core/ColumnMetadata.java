package orm.core;

import java.lang.reflect.Field;

public final class ColumnMetadata {

    private final Field field;
    private final String columnName;
    private final String sqlType;
    private final boolean nullable;
    private final boolean id;

    public ColumnMetadata(Field field, String columnName, String sqlType, boolean nullable, boolean id) {
        this.field = field;
        this.columnName = columnName;
        this.sqlType = sqlType;
        this.nullable = nullable;
        this.id = id;
        field.setAccessible(true);
    }

    public Field field() {
        return field;
    }

    public String columnName() {
        return columnName;
    }

    public String sqlType() {
        return sqlType;
    }

    public boolean nullable() {
        return nullable;
    }

    public boolean isId() {
        return id;
    }

    public Class<?> javaType() {
        return field.getType();
    }

    public Object getValue(Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new OrmException("Failed to read field " + field.getName(), e);
        }
    }

    public void setValue(Object entity, Object value) {
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new OrmException("Failed to set field " + field.getName(), e);
        }
    }
}
