package com.fractal.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonConfigParser {
    private static final Logger log = LoggerFactory.getLogger(JsonConfigParser.class);
    private final ObjectMapper mapper = new ObjectMapper();

    public AppConfig parse(String path) throws IOException {
        log.info("Loading config from JSON file: {}", path);
        JsonRoot root = mapper.readValue(new File(path), JsonRoot.class);
        return toAppConfig(root);
    }

    private AppConfig toAppConfig(JsonRoot root) {
        AppConfig config = new AppConfig();
        if (root.size != null) {
            if (root.size.width != null) config.setWidth(root.size.width);
            if (root.size.height != null) config.setHeight(root.size.height);
        }
        if (root.iterationCount != null) config.setIterationCount(root.iterationCount);
        if (root.outputPath != null) config.setOutputPath(root.outputPath);
        if (root.threads != null) config.setThreads(root.threads);
        if (root.seed != null) config.setSeed(root.seed.longValue());
        if (root.symmetryLevel != null) config.setSymmetryLevel(root.symmetryLevel);
        if (root.functions != null) {
            config.setFunctions(root.functions.stream()
                .map(f -> new AppConfig.FunctionConfig(f.name, f.weight))
                .toList());
        }
        if (root.affineParams != null) {
            config.setAffineParams(root.affineParams.stream()
                .map(p -> new AppConfig.AffineParamConfig(p.a, p.b, p.c, p.d, p.e, p.f))
                .toList());
        }
        return config;
    }

    static class JsonRoot {
        public SizeNode size;
        @JsonProperty("iteration_count") public Integer iterationCount;
        @JsonProperty("output_path") public String outputPath;
        public Integer threads;
        public Double seed;
        @JsonProperty("symmetry_level") public Integer symmetryLevel;
        public List<FunctionNode> functions;
        @JsonProperty("affine_params") public List<AffineNode> affineParams;
    }

    static class SizeNode {
        public Integer width;
        public Integer height;
    }

    static class FunctionNode {
        public String name;
        public double weight;
    }

    static class AffineNode {
        public double a, b, c, d, e, f;
    }
}
