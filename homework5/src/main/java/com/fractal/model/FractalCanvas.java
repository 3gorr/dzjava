package com.fractal.model;

public class FractalCanvas {
    private final int width;
    private final int height;
    private final Pixel[][] pixels;

    public FractalCanvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Pixel[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = new Pixel();
            }
        }
    }

    public void addColor(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x].addColor(r, g, b);
        }
    }

    public Pixel getPixel(int x, int y) {
        return pixels[y][x];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
