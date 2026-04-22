package com.fractal.transform;

import com.fractal.model.Point;

public class HeartVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());
        return new Point(
            r * Math.sin(theta * r),
            -r * Math.cos(theta * r)
        );
    }

    @Override
    public String getName() { return "heart"; }
}
