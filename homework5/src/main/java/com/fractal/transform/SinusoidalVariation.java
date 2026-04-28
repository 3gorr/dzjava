package com.fractal.transform;

import com.fractal.model.Point;

public class SinusoidalVariation implements Variation {
    @Override
    public Point apply(Point p) {
        return new Point(Math.sin(p.x()), Math.sin(p.y()));
    }

    @Override
    public String getName() { return "sinusoidal"; }
}
