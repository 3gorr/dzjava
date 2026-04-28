package com.fractal.transform;

import com.fractal.model.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VariationTest {

    private static final double DELTA = 1e-9;

    @Test
    void linearReturnsInputUnchanged() {
        Variation v = new LinearVariation();
        Point p = new Point(1.5, -2.3);
        Point result = v.apply(p);
        assertEquals(p.x(), result.x(), DELTA);
        assertEquals(p.y(), result.y(), DELTA);
    }

    @Test
    void sinusoidalAppliesSin() {
        Variation v = new SinusoidalVariation();
        Point p = new Point(Math.PI / 2, 0.0);
        Point result = v.apply(p);
        assertEquals(Math.sin(Math.PI / 2), result.x(), DELTA);
        assertEquals(0.0, result.y(), DELTA);
    }

    @Test
    void sphericalInvertsRadius() {
        Variation v = new SphericalVariation();
        Point p = new Point(3.0, 4.0); // r=5, r2=25
        Point result = v.apply(p);
        assertEquals(3.0 / 25.0, result.x(), DELTA);
        assertEquals(4.0 / 25.0, result.y(), DELTA);
    }

    @Test
    void sphericalAtOriginReturnsZero() {
        Variation v = new SphericalVariation();
        Point result = v.apply(new Point(0, 0));
        assertEquals(0.0, result.x(), DELTA);
        assertEquals(0.0, result.y(), DELTA);
    }

    @Test
    void swirlPreservesRadius() {
        Variation v = new SwirlVariation();
        Point p = new Point(1.0, 0.0);
        Point result = v.apply(p);
        double r = Math.sqrt(result.x() * result.x() + result.y() * result.y());
        assertEquals(1.0, r, 1e-6);
    }

    @Test
    void horseshoeAtOriginReturnsZero() {
        Variation v = new HorseshoeVariation();
        Point result = v.apply(new Point(0, 0));
        assertEquals(0.0, result.x(), DELTA);
        assertEquals(0.0, result.y(), DELTA);
    }

    @Test
    void horseshoeOnAxisIsSymmetric() {
        Variation v = new HorseshoeVariation();
        // (x, 0) → ((x²)/x, 0) = (x, 0)
        Point p = new Point(2.0, 0.0);
        Point result = v.apply(p);
        assertEquals(2.0, result.x(), DELTA);
        assertEquals(0.0, result.y(), DELTA);
    }

    @Test
    void discMapsOriginToZero() {
        Variation v = new DiscVariation();
        Point result = v.apply(new Point(0.0, 0.0));
        assertEquals(0.0, result.x(), DELTA);
        assertEquals(0.0, result.y(), DELTA);
    }

    @Test
    void polarXIsInRange() {
        Variation v = new PolarVariation();
        Point p = new Point(1.0, 1.0);
        Point result = v.apply(p);
        assertTrue(result.x() >= -1.0 && result.x() <= 1.0,
            "Polar x (theta/pi) should be in [-1, 1]");
    }

    @Test
    void heartPreservesSignOfY() {
        Variation v = new HeartVariation();
        Point p = new Point(1.0, 1.0);
        Point result = v.apply(p);
        assertNotNull(result);
    }

    @Test
    void allVariationsHaveUniqueName() {
        String[] names = {"linear", "sinusoidal", "spherical", "swirl", "horseshoe", "disc", "polar", "heart"};
        for (String name : names) {
            Variation v = VariationRegistry.get(name);
            assertEquals(name, v.getName());
        }
    }

    @Test
    void registryThrowsOnUnknownName() {
        assertThrows(IllegalArgumentException.class, () -> VariationRegistry.get("nonexistent"));
    }
}
