package com.fractal.transform;

import com.fractal.model.Point;

public class LinearVariation implements Variation {
    @Override
    public Point apply(Point p) {
        return p;
    }

    @Override
    public String getName() { return "linear"; }
}
