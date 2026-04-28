package com.fractal.transform;

import com.fractal.model.Point;

public interface Variation {
    Point apply(Point p);
    String getName();
}
