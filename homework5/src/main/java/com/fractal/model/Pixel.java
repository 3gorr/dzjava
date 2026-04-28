package com.fractal.model;

public class Pixel {
    private int r;
    private int g;
    private int b;
    private int hitCount;

    public Pixel() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
        this.hitCount = 0;
    }

    public synchronized void addColor(int r, int g, int b) {
        this.r = (this.r * hitCount + r) / (hitCount + 1);
        this.g = (this.g * hitCount + g) / (hitCount + 1);
        this.b = (this.b * hitCount + b) / (hitCount + 1);
        hitCount++;
    }

    public int getR() { return r; }
    public int getG() { return g; }
    public int getB() { return b; }
    public int getHitCount() { return hitCount; }

    public void setR(int r) { this.r = r; }
    public void setG(int g) { this.g = g; }
    public void setB(int b) { this.b = b; }
}
