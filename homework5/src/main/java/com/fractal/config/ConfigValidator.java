package com.fractal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ConfigValidator {
    private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);

    public void validate(AppConfig config) {
        List<String> errors = new ArrayList<>();

        if (config.getWidth() <= 0) errors.add("Width must be > 0, got: " + config.getWidth());
        if (config.getHeight() <= 0) errors.add("Height must be > 0, got: " + config.getHeight());
        if (config.getIterationCount() <= 0) errors.add("Iteration count must be > 0, got: " + config.getIterationCount());
        if (config.getThreads() <= 0) errors.add("Thread count must be > 0, got: " + config.getThreads());
        if (config.getSymmetryLevel() < 1) errors.add("Symmetry level must be >= 1, got: " + config.getSymmetryLevel());
        if (config.getOutputPath() == null || config.getOutputPath().isBlank()) {
            errors.add("Output path must not be empty");
        }
        if (config.getFunctions() == null || config.getFunctions().isEmpty()) {
            log.warn("No functions specified, will use default: linear");
        } else {
            for (AppConfig.FunctionConfig f : config.getFunctions()) {
                if (f.weight() <= 0) {
                    errors.add("Function weight must be > 0 for function: " + f.name());
                }
            }
        }
        if (config.getAffineParams() == null || config.getAffineParams().isEmpty()) {
            log.warn("No affine params specified, will use random generation");
        }

        if (!errors.isEmpty()) {
            String message = "Configuration validation failed:\n" + String.join("\n", errors);
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        log.info("Configuration validated successfully: {}x{}, {} iterations, {} thread(s)",
            config.getWidth(), config.getHeight(), config.getIterationCount(), config.getThreads());
    }
}
