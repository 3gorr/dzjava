package orm.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MetadataRegistry {

    private final ConcurrentMap<Class<?>, EntityMetadata> cache = new ConcurrentHashMap<>();

    public EntityMetadata get(Class<?> clazz) {
        return cache.computeIfAbsent(clazz, EntityMetadata::parse);
    }
}
