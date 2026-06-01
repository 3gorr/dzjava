package orm.core;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Map;

public final class SqlTypeMapper {

    private static final Map<Class<?>, String> SQL_TYPES = Map.ofEntries(
            Map.entry(Long.class, "BIGINT"),
            Map.entry(long.class, "BIGINT"),
            Map.entry(Integer.class, "INT"),
            Map.entry(int.class, "INT"),
            Map.entry(Double.class, "DOUBLE"),
            Map.entry(double.class, "DOUBLE"),
            Map.entry(Boolean.class, "BOOLEAN"),
            Map.entry(boolean.class, "BOOLEAN"),
            Map.entry(String.class, "VARCHAR(255)"),
            Map.entry(LocalDate.class, "DATE")
    );

    private SqlTypeMapper() {
    }

    public static String sqlTypeFor(Class<?> javaType) {
        String sqlType = SQL_TYPES.get(javaType);
        if (sqlType == null) {
            throw new OrmException("Unsupported Java type: " + javaType.getName());
        }
        return sqlType;
    }

    public static boolean isSupported(Class<?> javaType) {
        return SQL_TYPES.containsKey(javaType);
    }

    public static void bind(PreparedStatement ps, int index, Object value, Class<?> javaType) {
        try {
            if (value == null) {
                ps.setNull(index, sqlTypeCode(javaType));
                return;
            }
            if (javaType == LocalDate.class) {
                ps.setDate(index, Date.valueOf((LocalDate) value));
            } else if (javaType == String.class) {
                ps.setString(index, (String) value);
            } else if (javaType == Long.class || javaType == long.class) {
                ps.setLong(index, (Long) value);
            } else if (javaType == Integer.class || javaType == int.class) {
                ps.setInt(index, (Integer) value);
            } else if (javaType == Double.class || javaType == double.class) {
                ps.setDouble(index, (Double) value);
            } else if (javaType == Boolean.class || javaType == boolean.class) {
                ps.setBoolean(index, (Boolean) value);
            } else {
                throw new OrmException("Cannot bind type: " + javaType.getName());
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to bind value at index " + index, e);
        }
    }

    public static Object read(ResultSet rs, String columnName, Class<?> javaType) {
        try {
            if (javaType == LocalDate.class) {
                Date date = rs.getDate(columnName);
                return date == null ? null : date.toLocalDate();
            }
            if (javaType == String.class) {
                return rs.getString(columnName);
            }
            if (javaType == Long.class) {
                long v = rs.getLong(columnName);
                return rs.wasNull() ? null : v;
            }
            if (javaType == long.class) {
                return rs.getLong(columnName);
            }
            if (javaType == Integer.class) {
                int v = rs.getInt(columnName);
                return rs.wasNull() ? null : v;
            }
            if (javaType == int.class) {
                return rs.getInt(columnName);
            }
            if (javaType == Double.class) {
                double v = rs.getDouble(columnName);
                return rs.wasNull() ? null : v;
            }
            if (javaType == double.class) {
                return rs.getDouble(columnName);
            }
            if (javaType == Boolean.class) {
                boolean v = rs.getBoolean(columnName);
                return rs.wasNull() ? null : v;
            }
            if (javaType == boolean.class) {
                return rs.getBoolean(columnName);
            }
            throw new OrmException("Cannot read type: " + javaType.getName());
        } catch (SQLException e) {
            throw new OrmException("Failed to read column " + columnName, e);
        }
    }

    private static int sqlTypeCode(Class<?> javaType) {
        if (javaType == LocalDate.class) return Types.DATE;
        if (javaType == String.class) return Types.VARCHAR;
        if (javaType == Long.class || javaType == long.class) return Types.BIGINT;
        if (javaType == Integer.class || javaType == int.class) return Types.INTEGER;
        if (javaType == Double.class || javaType == double.class) return Types.DOUBLE;
        if (javaType == Boolean.class || javaType == boolean.class) return Types.BOOLEAN;
        return Types.OTHER;
    }
}
