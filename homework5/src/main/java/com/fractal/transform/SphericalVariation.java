package com.fractal.transform;

import com.fractal.model.Point;

public class SphericalVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y();
        if (r2 == 0) return new Point(0, 0);
        return new Point(p.x() / r2, p.y() / r2);
    }

    @Override
    public String getName() { return "spherical"; }
}
