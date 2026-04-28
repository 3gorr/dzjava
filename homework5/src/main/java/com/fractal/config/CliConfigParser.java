package com.fractal.config;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "fractal-flame", mixinStandardHelpOptions = true,
    description = "Generates fractal flame images using the Chaos Game algorithm")
public class CliConfigParser implements Callable<AppConfig> {

    @Option(names = {"-w", "--width"}, description = "Image width (default: 1920)")
    private Integer width;

    @Option(names = {"-h", "--height"}, description = "Image height (default: 1080)")
    private Integer height;

    @Option(names = {"--seed"}, description = "Random seed (default: 5)")
    private Long seed;

    @Option(names = {"-i", "--iteration-count"}, description = "Number of iterations (default: 2500)")
    private Integer iterationCount;

    @Option(names = {"-o", "--output-path"}, description = "Output PNG file path (default: result.png)")
    private String outputPath;

    @Option(names = {"-t", "--threads"}, description = "Number of threads (default: 1)")
    private Integer threads;

    @Option(names = {"-s", "--symmetry-level"}, description = "Symmetry level, N >= 1 (default: 1)")
    private Integer symmetryLevel;

    @Option(names = {"-f", "--functions"},
        description = "Functions config: <name>:<weight>,<name>:<weight>, e.g. swirl:1.0,horseshoe:0.8")
    private String functions;

    @Option(names = {"-ap", "--affine-params"},
        description = "Affine params: a,b,c,d,e,f/a,b,c,d,e,f/...")
    private String affineParams;

    @Option(names = {"--supersample", "-ss"}, description = "Supersampling factor (default: 2)")
    private Integer supersample;

    @Option(names = {"--config"}, description = "Path to JSON config file")
    private String configPath;

    @Override
    public AppConfig call() {
        return buildPartialConfig();
    }

    public String getConfigPath() { return configPath; }

    public AppConfig buildPartialConfig() {
        AppConfig config = new AppConfig();
        if (width != null) config.setWidth(width);
        if (height != null) config.setHeight(height);
        if (seed != null) config.setSeed(seed);
        if (iterationCount != null) config.setIterationCount(iterationCount);
        if (outputPath != null) config.setOutputPath(outputPath);
        if (threads != null) config.setThreads(threads);
        if (symmetryLevel != null) config.setSymmetryLevel(symmetryLevel);
        if (functions != null) config.setFunctions(parseFunctions(functions));
        if (affineParams != null) config.setAffineParams(parseAffineParams(affineParams));
        if (supersample != null) config.setSupersample(supersample);
        return config;
    }

    private List<AppConfig.FunctionConfig> parseFunctions(String raw) {
        List<AppConfig.FunctionConfig> list = new ArrayList<>();
        for (String part : raw.split(",")) {
            String[] kv = part.trim().split(":");
            if (kv.length != 2) throw new IllegalArgumentException("Invalid function format: " + part);
            list.add(new AppConfig.FunctionConfig(kv[0].trim(), Double.parseDouble(kv[1].trim())));
        }
        return list;
    }

    private List<AppConfig.AffineParamConfig> parseAffineParams(String raw) {
        List<AppConfig.AffineParamConfig> list = new ArrayList<>();
        for (String part : raw.split("/")) {
            String[] vals = part.trim().split(",");
            if (vals.length != 6) throw new IllegalArgumentException("Affine params need 6 values: " + part);
            list.add(new AppConfig.AffineParamConfig(
                Double.parseDouble(vals[0].trim()),
                Double.parseDouble(vals[1].trim()),
                Double.parseDouble(vals[2].trim()),
                Double.parseDouble(vals[3].trim()),
                Double.parseDouble(vals[4].trim()),
                Double.parseDouble(vals[5].trim())
            ));
        }
        return list;
    }

    public static CliConfigParser parse(String[] args) {
        CliConfigParser parser = new CliConfigParser();
        CommandLine cmd = new CommandLine(parser);
        cmd.parseArgs(args);
        return parser;
    }
}
