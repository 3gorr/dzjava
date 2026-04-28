package com.fractal.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CliConfigParserTest {

    @Test
    void parsesWidthAndHeight() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-w", "800", "-h", "600"});
        AppConfig config = parser.buildPartialConfig();
        assertEquals(800, config.getWidth());
        assertEquals(600, config.getHeight());
    }

    @Test
    void parsesSeedAndIterations() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"--seed", "42", "-i", "5000"});
        AppConfig config = parser.buildPartialConfig();
        assertEquals(42L, config.getSeed());
        assertEquals(5000, config.getIterationCount());
    }

    @Test
    void parsesThreadsAndOutputPath() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-t", "4", "-o", "out/test.png"});
        AppConfig config = parser.buildPartialConfig();
        assertEquals(4, config.getThreads());
        assertEquals("out/test.png", config.getOutputPath());
    }

    @Test
    void parsesFunctions() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-f", "swirl:1.0,horseshoe:0.8"});
        AppConfig config = parser.buildPartialConfig();
        assertNotNull(config.getFunctions());
        assertEquals(2, config.getFunctions().size());
        assertEquals("swirl", config.getFunctions().get(0).name());
        assertEquals(1.0, config.getFunctions().get(0).weight());
        assertEquals("horseshoe", config.getFunctions().get(1).name());
        assertEquals(0.8, config.getFunctions().get(1).weight(), 1e-9);
    }

    @Test
    void parsesAffineParams() {
        CliConfigParser parser = CliConfigParser.parse(
            new String[]{"-ap", "1.0,0.5,-0.2,0.3,0.8,0.1"});
        AppConfig config = parser.buildPartialConfig();
        assertNotNull(config.getAffineParams());
        assertEquals(1, config.getAffineParams().size());
        assertEquals(1.0, config.getAffineParams().get(0).a(), 1e-9);
        assertEquals(-0.2, config.getAffineParams().get(0).c(), 1e-9);
    }

    @Test
    void parsesSymmetryLevel() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-s", "4"});
        AppConfig config = parser.buildPartialConfig();
        assertEquals(4, config.getSymmetryLevel());
    }

    @Test
    void parsesConfigPath() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"--config", "config.json"});
        assertEquals("config.json", parser.getConfigPath());
    }

    @Test
    void defaultsAreApplied() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{});
        AppConfig config = parser.buildPartialConfig();
        assertEquals(1920, config.getWidth());
        assertEquals(1080, config.getHeight());
        assertEquals(5L, config.getSeed());
        assertEquals(2500, config.getIterationCount());
        assertEquals("result.png", config.getOutputPath());
        assertEquals(1, config.getThreads());
    }

    @Test
    void invalidFunctionFormatThrows() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-f", "badformat"});
        assertThrows(IllegalArgumentException.class, parser::buildPartialConfig);
    }

    @Test
    void invalidAffineParamsThrows() {
        CliConfigParser parser = CliConfigParser.parse(new String[]{"-ap", "1.0,2.0"});
        assertThrows(IllegalArgumentException.class, parser::buildPartialConfig);
    }
}
