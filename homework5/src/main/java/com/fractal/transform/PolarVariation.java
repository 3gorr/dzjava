package com.fractal.transform;

import com.fractal.model.Point;

public class PolarVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());
        return new Point(theta / Math.PI, r - 1.0);
    }

    @Override
    public String getName() { return "polar"; }
}
