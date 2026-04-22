package com.fractal.model;

public record AffineTransform(double a, double b, double c, double d, double e, double f,
                               int colorR, int colorG, int colorB) {

    public Point apply(Point p) {
        double nx = a * p.x() + b * p.y() + c;
        double ny = d * p.x() + e * p.y() + f;
        return new Point(nx, ny);
    }

    public AffineTransform(double a, double b, double c, double d, double e, double f) {
        this(a, b, c, d, e, f, 255, 255, 255);
    }
}
