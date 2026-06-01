package orm.core;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class EntityMetadata {

    private final Class<?> entityClass;
    private final String tableName;
    private final ColumnMetadata idColumn;
    private final List<ColumnMetadata> columns;
    private final List<ColumnMetadata> allColumns;
    private final Map<String, ColumnMetadata> columnsByFieldName;
    private final Constructor<?> noArgConstructor;

    private final String createTableSql;
    private final String insertSql;
    private final String selectByIdSql;
    private final String selectAllSql;
    private final String updateSql;
    private final String deleteSql;
    private final String countSql;
    private final String existsByIdSql;

    private EntityMetadata(Class<?> entityClass,
                           String tableName,
                           ColumnMetadata idColumn,
                           List<ColumnMetadata> columns,
                           Map<String, ColumnMetadata> columnsByFieldName,
                           Constructor<?> noArgConstructor) {
        this.entityClass = entityClass;
        this.tableName = tableName;
        this.idColumn = idColumn;
        this.columns = columns;
        this.columnsByFieldName = columnsByFieldName;
        this.noArgConstructor = noArgConstructor;
        List<ColumnMetadata> all = new ArrayList<>();
        all.add(idColumn);
        all.addAll(columns);
        this.allColumns = Collections.unmodifiableList(all);

        this.createTableSql = buildCreateTableSql();
        this.insertSql = buildInsertSql();
        this.selectByIdSql = buildSelectByIdSql();
        this.selectAllSql = buildSelectAllSql();
        this.updateSql = buildUpdateSql();
        this.deleteSql = buildDeleteSql();
        this.countSql = "SELECT COUNT(*) FROM " + tableName;
        this.existsByIdSql = "SELECT 1 FROM " + tableName + " WHERE " + idColumn.columnName() + " = ? LIMIT 1";
    }

    public static EntityMetadata parse(Class<?> clazz) {
        Table tableAnn = clazz.getAnnotation(Table.class);
        if (tableAnn == null) {
            throw new OrmException("Class " + clazz.getName() + " is not annotated with @Table");
        }
        String tableName = tableAnn.name().isEmpty() ? clazz.getSimpleName().toLowerCase() : tableAnn.name();

        ColumnMetadata idColumn = null;
        List<ColumnMetadata> columns = new ArrayList<>();
        Map<String, ColumnMetadata> byField = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            Id idAnn = field.getAnnotation(Id.class);
            Column colAnn = field.getAnnotation(Column.class);

            if (idAnn == null && colAnn == null) {
                continue;
            }

            if (idAnn != null) {
                Class<?> type = field.getType();
                if (type != Long.class && type != long.class) {
                    throw new OrmException("@Id field " + field.getName() + " must be Long or long");
                }
                if (idColumn != null) {
                    throw new OrmException("Multiple @Id fields in " + clazz.getName());
                }
                String name = (colAnn != null && !colAnn.name().isEmpty())
                        ? colAnn.name()
                        : field.getName();
                idColumn = new ColumnMetadata(field, name, "BIGINT", true, true);
                byField.put(field.getName(), idColumn);
            } else {
                if (!SqlTypeMapper.isSupported(field.getType())) {
                    throw new OrmException("Unsupported field type: " + field.getType().getName()
                            + " for field " + field.getName());
                }
                String name = colAnn.name().isEmpty() ? field.getName() : colAnn.name();
                ColumnMetadata col = new ColumnMetadata(
                        field, name, SqlTypeMapper.sqlTypeFor(field.getType()), colAnn.nullable(), false);
                columns.add(col);
                byField.put(field.getName(), col);
            }
        }

        if (idColumn == null) {
            throw new OrmException("Class " + clazz.getName() + " has no @Id field");
        }

        Constructor<?> ctor;
        try {
            ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new OrmException("Class " + clazz.getName() + " has no no-arg constructor", e);
        }

        return new EntityMetadata(clazz, tableName, idColumn,
                Collections.unmodifiableList(columns), byField, ctor);
    }

    public Object newInstance() {
        try {
            return noArgConstructor.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Failed to instantiate " + entityClass.getName(), e);
        }
    }

    public Class<?> entityClass() {
        return entityClass;
    }

    public String tableName() {
        return tableName;
    }

    public ColumnMetadata idColumn() {
        return idColumn;
    }

    public List<ColumnMetadata> columns() {
        return columns;
    }

    public List<ColumnMetadata> allColumns() {
        return allColumns;
    }

    public ColumnMetadata columnByFieldName(String fieldName) {
        return columnsByFieldName.get(fieldName);
    }

    public String createTableSql() { return createTableSql; }
    public String insertSql() { return insertSql; }
    public String selectByIdSql() { return selectByIdSql; }
    public String selectAllSql() { return selectAllSql; }
    public String updateSql() { return updateSql; }
    public String deleteSql() { return deleteSql; }
    public String countSql() { return countSql; }
    public String existsByIdSql() { return existsByIdSql; }

    public String selectWhereSql(String columnName) {
        return buildSelectColumns() + " FROM " + tableName + " WHERE " + columnName + " = ?";
    }

    private String buildCreateTableSql() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");
        sb.append("    ").append(idColumn.columnName()).append(" BIGINT AUTO_INCREMENT PRIMARY KEY");
        for (ColumnMetadata col : columns) {
            sb.append(",\n    ").append(col.columnName()).append(' ').append(col.sqlType());
            if (!col.nullable()) {
                sb.append(" NOT NULL");
            }
        }
        sb.append("\n)");
        return sb.toString();
    }

    private String buildInsertSql() {
        String cols = columns.stream().map(ColumnMetadata::columnName).collect(Collectors.joining(", "));
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + " (" + cols + ") VALUES (" + placeholders + ")";
    }

    private String buildSelectByIdSql() {
        return buildSelectColumns() + " FROM " + tableName + " WHERE " + idColumn.columnName() + " = ?";
    }

    private String buildSelectAllSql() {
        return buildSelectColumns() + " FROM " + tableName;
    }

    private String buildSelectColumns() {
        String cols = allColumns.stream().map(ColumnMetadata::columnName).collect(Collectors.joining(", "));
        return "SELECT " + cols;
    }

    private String buildUpdateSql() {
        String setClause = columns.stream()
                .map(c -> c.columnName() + " = ?")
                .collect(Collectors.joining(", "));
        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumn.columnName() + " = ?";
    }

    private String buildDeleteSql() {
        return "DELETE FROM " + tableName + " WHERE " + idColumn.columnName() + " = ?";
    }
}
