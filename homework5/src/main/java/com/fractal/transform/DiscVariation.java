package com.fractal.transform;

import com.fractal.model.Point;

public class DiscVariation implements Variation {
    @Override
    public Point apply(Point p) {
        double r = Math.sqrt(p.x() * p.x() + p.y() * p.y());
        double theta = Math.atan2(p.y(), p.x());
        double piR = Math.PI * r;
        return new Point(
            theta / Math.PI * Math.sin(piR),
            theta / Math.PI * Math.cos(piR)
        );
    }

    @Override
    public String getName() { return "disc"; }
}
