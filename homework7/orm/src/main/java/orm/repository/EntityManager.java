package orm.repository;

import orm.core.ColumnMetadata;
import orm.core.EntityMetadata;
import orm.core.MetadataRegistry;
import orm.core.OrmException;
import orm.core.SqlTypeMapper;
import orm.core.validation.ValidationException;
import orm.core.validation.Validator;
import orm.core.validation.Violation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityManager {

    private final Connection connection;
    private final MetadataRegistry registry = new MetadataRegistry();
    private final Validator validator = new Validator();

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    public void createTable(Class<?> clazz) {
        EntityMetadata meta = registry.get(clazz);
        try (Statement st = connection.createStatement()) {
            st.execute(meta.createTableSql());
        } catch (SQLException e) {
            throw new OrmException("Failed to create table for " + clazz.getName(), e);
        }
    }

    public Long save(Object entity) {
        EntityMetadata meta = registry.get(entity.getClass());
        validate(entity);
        checkNotNullColumns(meta, entity);
        try (PreparedStatement ps = connection.prepareStatement(
                meta.insertSql(), Statement.RETURN_GENERATED_KEYS)) {
            bindColumns(ps, meta.columns(), entity, 1);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new OrmException("No generated key returned for " + meta.tableName());
                }
                long id = keys.getLong(1);
                meta.idColumn().setValue(entity, id);
                return id;
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to save entity into " + meta.tableName(), e);
        }
    }

    public <T> Optional<T> findById(Class<T> clazz, Long id) {
        EntityMetadata meta = registry.get(clazz);
        try (PreparedStatement ps = connection.prepareStatement(meta.selectByIdSql())) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(readEntity(clazz, meta, rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to find " + clazz.getName() + " by id " + id, e);
        }
    }

    public <T> List<T> findAll(Class<T> clazz) {
        EntityMetadata meta = registry.get(clazz);
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(meta.selectAllSql());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(readEntity(clazz, meta, rs));
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to load all rows from " + meta.tableName(), e);
        }
        return result;
    }

    public int update(Object entity) {
        EntityMetadata meta = registry.get(entity.getClass());
        validate(entity);
        checkNotNullColumns(meta, entity);
        Object idValue = meta.idColumn().getValue(entity);
        if (idValue == null) {
            throw new OrmException("Cannot update entity without id");
        }
        try (PreparedStatement ps = connection.prepareStatement(meta.updateSql())) {
            int idx = bindColumns(ps, meta.columns(), entity, 1);
            SqlTypeMapper.bind(ps, idx, idValue, meta.idColumn().javaType());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Failed to update " + meta.tableName(), e);
        }
    }

    public int delete(Object entity) {
        EntityMetadata meta = registry.get(entity.getClass());
        Object idValue = meta.idColumn().getValue(entity);
        if (idValue == null) {
            throw new OrmException("Cannot delete entity without id");
        }
        return deleteByIdInternal(meta, (Long) idValue);
    }

    public int deleteById(Class<?> clazz, Long id) {
        EntityMetadata meta = registry.get(clazz);
        return deleteByIdInternal(meta, id);
    }

    public void saveAll(List<?> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        Class<?> first = null;
        for (Object e : entities) {
            if (e == null) {
                throw new OrmException("saveAll does not accept null elements");
            }
            if (first == null) {
                first = e.getClass();
            } else if (e.getClass() != first) {
                throw new OrmException("saveAll requires all entities to be of the same class");
            }
        }
        EntityMetadata meta = registry.get(first);

        List<Violation> all = new ArrayList<>();
        for (Object e : entities) {
            all.addAll(validator.validate(e));
        }
        if (!all.isEmpty()) {
            throw new ValidationException(all);
        }
        for (Object e : entities) {
            checkNotNullColumns(meta, e);
        }

        boolean previousAutoCommit;
        try {
            previousAutoCommit = connection.getAutoCommit();
        } catch (SQLException e) {
            throw new OrmException("Failed to read autoCommit", e);
        }

        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(
                    meta.insertSql(), Statement.RETURN_GENERATED_KEYS)) {
                for (Object e : entities) {
                    bindColumns(ps, meta.columns(), e, 1);
                    ps.addBatch();
                }
                ps.executeBatch();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (keys.next() && i < entities.size()) {
                        meta.idColumn().setValue(entities.get(i), keys.getLong(1));
                        i++;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                safeRollback();
                throw new OrmException("Batch save failed, transaction rolled back", e);
            } catch (RuntimeException e) {
                safeRollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to begin transaction", e);
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException ignored) {
            }
        }
    }

    public <T> List<T> findAllWhere(Class<T> clazz, String fieldName, Object value) {
        EntityMetadata meta = registry.get(clazz);
        ColumnMetadata col = resolveColumn(meta, fieldName, value);
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(meta.selectWhereSql(col.columnName()))) {
            SqlTypeMapper.bind(ps, 1, value, col.javaType());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(readEntity(clazz, meta, rs));
                }
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to find " + clazz.getName() + " where " + fieldName + " = " + value, e);
        }
        return result;
    }

    public <T> Optional<T> findOneWhere(Class<T> clazz, String fieldName, Object value) {
        List<T> all = findAllWhere(clazz, fieldName, value);
        if (all.isEmpty()) {
            return Optional.empty();
        }
        if (all.size() > 1) {
            throw new OrmException("findOneWhere expected 1 row but found " + all.size());
        }
        return Optional.of(all.get(0));
    }

    public long count(Class<?> clazz) {
        EntityMetadata meta = registry.get(clazz);
        try (PreparedStatement ps = connection.prepareStatement(meta.countSql());
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new OrmException("Failed to count rows in " + meta.tableName(), e);
        }
    }

    public boolean existsById(Class<?> clazz, Long id) {
        EntityMetadata meta = registry.get(clazz);
        try (PreparedStatement ps = connection.prepareStatement(meta.existsByIdSql())) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new OrmException("Failed to check existence in " + meta.tableName(), e);
        }
    }

    private void validate(Object entity) {
        List<Violation> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void checkNotNullColumns(EntityMetadata meta, Object entity) {
        for (ColumnMetadata col : meta.columns()) {
            if (!col.nullable() && col.getValue(entity) == null) {
                throw new OrmException("Column '" + col.columnName() + "' is NOT NULL but value is null");
            }
        }
    }

    private int bindColumns(PreparedStatement ps, List<ColumnMetadata> columns, Object entity, int startIndex)
            throws SQLException {
        int i = startIndex;
        for (ColumnMetadata col : columns) {
            SqlTypeMapper.bind(ps, i++, col.getValue(entity), col.javaType());
        }
        return i;
    }

    private <T> T readEntity(Class<T> clazz, EntityMetadata meta, ResultSet rs) {
        Object instance = meta.newInstance();
        for (ColumnMetadata col : meta.allColumns()) {
            Object value = SqlTypeMapper.read(rs, col.columnName(), col.javaType());
            col.setValue(instance, value);
        }
        return clazz.cast(instance);
    }

    private int deleteByIdInternal(EntityMetadata meta, Long id) {
        try (PreparedStatement ps = connection.prepareStatement(meta.deleteSql())) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Failed to delete from " + meta.tableName() + " by id " + id, e);
        }
    }

    private ColumnMetadata resolveColumn(EntityMetadata meta, String fieldName, Object value) {
        ColumnMetadata col = meta.columnByFieldName(fieldName);
        if (col == null) {
            throw new OrmException("Field '" + fieldName + "' not found in " + meta.entityClass().getName());
        }
        if (value != null) {
            Class<?> javaType = col.javaType();
            Class<?> expected = javaType.isPrimitive() ? boxed(javaType) : javaType;
            if (!expected.isInstance(value)) {
                throw new OrmException("Value of type " + value.getClass().getName()
                        + " is not compatible with field '" + fieldName + "' of type " + javaType.getName());
            }
        }
        return col;
    }

    private static Class<?> boxed(Class<?> primitive) {
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == double.class) return Double.class;
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == float.class) return Float.class;
        if (primitive == short.class) return Short.class;
        if (primitive == byte.class) return Byte.class;
        if (primitive == char.class) return Character.class;
        return primitive;
    }

    private void safeRollback() {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }
}
