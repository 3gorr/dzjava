package com.fractal.transform;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VariationRegistry {
    private static final Map<String, Variation> REGISTRY = new HashMap<>();

    static {
        register(new LinearVariation());
        register(new SinusoidalVariation());
        register(new SphericalVariation());
        register(new SwirlVariation());
        register(new HorseshoeVariation());
        register(new DiscVariation());
        register(new PolarVariation());
        register(new HeartVariation());
    }

    private static void register(Variation v) {
        REGISTRY.put(v.getName(), v);
    }

    public static Variation get(String name) {
        Variation v = REGISTRY.get(name.toLowerCase());
        if (v == null) {
            throw new IllegalArgumentException("Unknown variation: '" + name
                + "'. Available: " + REGISTRY.keySet());
        }
        return v;
    }

    public static Set<String> availableNames() {
        return REGISTRY.keySet();
    }
}
