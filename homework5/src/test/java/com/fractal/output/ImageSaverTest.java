package com.fractal.output;

import com.fractal.config.AppConfig;
import com.fractal.model.FractalCanvas;
import com.fractal.render.SingleThreadRenderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ImageSaverTest {

    @TempDir
    Path tempDir;

    @Test
    void savedImageHasCorrectDimensions() throws IOException {
        AppConfig config = new AppConfig();
        config.setWidth(100);
        config.setHeight(80);
        config.setIterationCount(500);
        config.setSeed(1L);
        config.setThreads(1);
        config.setSymmetryLevel(1);
        config.setFunctions(List.of(new AppConfig.FunctionConfig("linear", 1.0)));

        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        String path = tempDir.resolve("test.png").toString();
        new ImageSaver().save(canvas, path);

        File file = new File(path);
        assertTrue(file.exists(), "Output file should exist");

        BufferedImage img = ImageIO.read(file);
        assertEquals(100, img.getWidth(), "Image width should match config");
        assertEquals(80, img.getHeight(), "Image height should match config");
    }

    @Test
    void savedImageIsRgb() throws IOException {
        AppConfig config = new AppConfig();
        config.setWidth(50);
        config.setHeight(50);
        config.setIterationCount(200);
        config.setSeed(2L);
        config.setThreads(1);
        config.setSymmetryLevel(1);

        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        String path = tempDir.resolve("rgb_test.png").toString();
        new ImageSaver().save(canvas, path);

        BufferedImage img = ImageIO.read(new File(path));
        assertEquals(BufferedImage.TYPE_INT_RGB, img.getType(), "Image should be TYPE_INT_RGB");
    }

    @Test
    void createsParentDirectoriesIfNeeded() throws IOException {
        AppConfig config = new AppConfig();
        config.setWidth(50);
        config.setHeight(50);
        config.setIterationCount(100);
        config.setSeed(3L);
        config.setThreads(1);
        config.setSymmetryLevel(1);

        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        String path = tempDir.resolve("subdir/nested/output.png").toString();
        new ImageSaver().save(canvas, path);

        assertTrue(new File(path).exists(), "Should create nested directories");
    }
}
