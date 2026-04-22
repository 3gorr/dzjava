package com.fractal.config;

import java.util.List;

public class AppConfig {
    private int width = 1920;
    private int height = 1080;
    private long seed = 5L;
    private int iterationCount = 2500;
    private String outputPath = "result.png";
    private int threads = 1;
    private int symmetryLevel = 1;
    private int supersample = 2;
    private List<FunctionConfig> functions;
    private List<AffineParamConfig> affineParams;

    public record FunctionConfig(String name, double weight) {}

    public record AffineParamConfig(double a, double b, double c, double d, double e, double f) {}

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public long getSeed() { return seed; }
    public void setSeed(long seed) { this.seed = seed; }

    public int getIterationCount() { return iterationCount; }
    public void setIterationCount(int iterationCount) { this.iterationCount = iterationCount; }

    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }

    public int getThreads() { return threads; }
    public void setThreads(int threads) { this.threads = threads; }

    public int getSymmetryLevel() { return symmetryLevel; }
    public void setSymmetryLevel(int symmetryLevel) { this.symmetryLevel = symmetryLevel; }

    public List<FunctionConfig> getFunctions() { return functions; }
    public void setFunctions(List<FunctionConfig> functions) { this.functions = functions; }

    public List<AffineParamConfig> getAffineParams() { return affineParams; }
    public void setAffineParams(List<AffineParamConfig> affineParams) { this.affineParams = affineParams; }

    public int getSupersample() { return supersample; }
    public void setSupersample(int supersample) { this.supersample = supersample; }
}
