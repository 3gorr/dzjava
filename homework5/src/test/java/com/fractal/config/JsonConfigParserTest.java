package com.fractal.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonConfigParserTest {

    @TempDir
    Path tempDir;

    private Path writeJson(String content) throws IOException {
        Path file = tempDir.resolve("config.json");
        Files.writeString(file, content);
        return file;
    }

    @Test
    void parsesFullConfig() throws IOException {
        String json = """
            {
              "size": {"width": 1280, "height": 720},
              "iteration_count": 3000,
              "output_path": "output/flame.png",
              "threads": 4,
              "seed": 42.0,
              "symmetry_level": 3,
              "functions": [
                {"name": "swirl", "weight": 1.0},
                {"name": "horseshoe", "weight": 0.7}
              ],
              "affine_params": [
                {"a": 0.5, "b": 0.1, "c": 0.0, "d": 0.1, "e": 0.5, "f": 0.0}
              ]
            }
            """;
        Path file = writeJson(json);
        AppConfig config = new JsonConfigParser().parse(file.toString());

        assertEquals(1280, config.getWidth());
        assertEquals(720, config.getHeight());
        assertEquals(3000, config.getIterationCount());
        assertEquals("output/flame.png", config.getOutputPath());
        assertEquals(4, config.getThreads());
        assertEquals(42L, config.getSeed());
        assertEquals(3, config.getSymmetryLevel());
        assertEquals(2, config.getFunctions().size());
        assertEquals("swirl", config.getFunctions().get(0).name());
        assertEquals(1, config.getAffineParams().size());
        assertEquals(0.5, config.getAffineParams().get(0).a(), 1e-9);
    }

    @Test
    void parsesMinimalConfig() throws IOException {
        String json = """
            {
              "size": {"width": 640, "height": 480}
            }
            """;
        Path file = writeJson(json);
        AppConfig config = new JsonConfigParser().parse(file.toString());
        assertEquals(640, config.getWidth());
        assertEquals(480, config.getHeight());
        assertNull(config.getFunctions());
    }

    @Test
    void throwsOnMissingFile() {
        assertThrows(IOException.class,
            () -> new JsonConfigParser().parse("/nonexistent/path/config.json"));
    }
}
