package com.fractal.transform;

import com.fractal.model.Point;

public class HorseshoeVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        if (r == 0) return new Point(0, 0);
        return new Point(
            (p.x() - p.y()) * (p.x() + p.y()) / r,
            2.0 * p.x() * p.y() / r
        );
    }

    @Override
    public String getName() { return "horseshoe"; }
}
