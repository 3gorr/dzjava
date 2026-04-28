package com.fractal.config;

public class ConfigMerger {
    public AppConfig merge(AppConfig base, AppConfig cli) {
        AppConfig result = new AppConfig();

        result.setWidth(cli.getWidth() != 1920 ? cli.getWidth() : base.getWidth());
        result.setHeight(cli.getHeight() != 1080 ? cli.getHeight() : base.getHeight());
        result.setSeed(cli.getSeed() != 5L ? cli.getSeed() : base.getSeed());
        result.setIterationCount(cli.getIterationCount() != 2500 ? cli.getIterationCount() : base.getIterationCount());
        result.setOutputPath(!"result.png".equals(cli.getOutputPath()) ? cli.getOutputPath() : base.getOutputPath());
        result.setThreads(cli.getThreads() != 1 ? cli.getThreads() : base.getThreads());
        result.setSymmetryLevel(cli.getSymmetryLevel() != 1 ? cli.getSymmetryLevel() : base.getSymmetryLevel());
        result.setFunctions(cli.getFunctions() != null ? cli.getFunctions() : base.getFunctions());
        result.setAffineParams(cli.getAffineParams() != null ? cli.getAffineParams() : base.getAffineParams());

        return result;
    }
}
