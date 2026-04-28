package com.fractal.transform;

import com.fractal.model.Point;

public class SwirlVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r2 = p.x() * p.x() + p.y() * p.y();
        double sinR2 = Math.sin(r2);
        double cosR2 = Math.cos(r2);
        return new Point(
            p.x() * sinR2 - p.y() * cosR2,
            p.x() * cosR2 + p.y() * sinR2
        );
    }

    @Override
    public String getName() { return "swirl"; }
}
