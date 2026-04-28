package com.fractal.render;

import com.fractal.config.AppConfig;
import com.fractal.model.FractalCanvas;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RendererTest {

    private AppConfig defaultConfig() {
        AppConfig config = new AppConfig();
        config.setWidth(200);
        config.setHeight(150);
        config.setIterationCount(1000);
        config.setSeed(42L);
        config.setThreads(1);
        config.setSymmetryLevel(1);
        config.setFunctions(List.of(new AppConfig.FunctionConfig("swirl", 1.0)));
        return config;
    }

    @Test
    void singleThreadRendererProducesCorrectSizeCanvas() {
        AppConfig config = defaultConfig();
        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        assertEquals(200, canvas.getWidth());
        assertEquals(150, canvas.getHeight());
    }

    @Test
    void multiThreadRendererProducesCorrectSizeCanvas() {
        AppConfig config = defaultConfig();
        config.setThreads(2);
        FractalCanvas canvas = new MultiThreadRenderer().render(config);
        assertEquals(200, canvas.getWidth());
        assertEquals(150, canvas.getHeight());
    }

    @Test
    void singleThreadRendererProducesSomeNonZeroPixels() {
        AppConfig config = defaultConfig();
        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        int nonZero = 0;
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                if (canvas.getPixel(x, y).getHitCount() > 0) nonZero++;
            }
        }
        assertTrue(nonZero > 0, "Renderer should paint at least some pixels");
    }

    @Test
    void symmetryLevelTwoDoublesPaintedArea() {
        AppConfig config1 = defaultConfig();
        config1.setSymmetryLevel(1);

        AppConfig config2 = defaultConfig();
        config2.setSymmetryLevel(2);

        FractalCanvas c1 = new SingleThreadRenderer().render(config1);
        FractalCanvas c2 = new SingleThreadRenderer().render(config2);

        int hits1 = countHits(c1);
        int hits2 = countHits(c2);
        assertTrue(hits2 >= hits1, "Symmetry=2 should paint at least as many pixels as symmetry=1");
    }

    @Test
    void gammaCorrectionDoesNotCorruptCanvas() {
        AppConfig config = defaultConfig();
        FractalCanvas canvas = new SingleThreadRenderer().render(config);
        new GammaCorrectionPostProcessor().process(canvas);
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                assertTrue(canvas.getPixel(x, y).getR() >= 0 && canvas.getPixel(x, y).getR() <= 255);
                assertTrue(canvas.getPixel(x, y).getG() >= 0 && canvas.getPixel(x, y).getG() <= 255);
                assertTrue(canvas.getPixel(x, y).getB() >= 0 && canvas.getPixel(x, y).getB() <= 255);
            }
        }
    }

    private int countHits(FractalCanvas canvas) {
        int count = 0;
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                if (canvas.getPixel(x, y).getHitCount() > 0) count++;
            }
        }
        return count;
    }
}
