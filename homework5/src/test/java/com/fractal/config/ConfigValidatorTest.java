package com.fractal.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigValidatorTest {

    private final ConfigValidator validator = new ConfigValidator();

    @Test
    void validConfigPassesValidation() {
        AppConfig config = new AppConfig();
        assertDoesNotThrow(() -> validator.validate(config));
    }

    @Test
    void zeroWidthFails() {
        AppConfig config = new AppConfig();
        config.setWidth(0);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void negativeHeightFails() {
        AppConfig config = new AppConfig();
        config.setHeight(-1);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void zeroIterationsFails() {
        AppConfig config = new AppConfig();
        config.setIterationCount(0);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void zeroThreadsFails() {
        AppConfig config = new AppConfig();
        config.setThreads(0);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void zeroSymmetryLevelFails() {
        AppConfig config = new AppConfig();
        config.setSymmetryLevel(0);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void emptyOutputPathFails() {
        AppConfig config = new AppConfig();
        config.setOutputPath("");
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }

    @Test
    void functionWithZeroWeightFails() {
        AppConfig config = new AppConfig();
        config.setFunctions(java.util.List.of(new AppConfig.FunctionConfig("swirl", 0.0)));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }
}
