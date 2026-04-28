package com.fractal.render;

import com.fractal.config.AppConfig;
import com.fractal.model.AffineTransform;
import com.fractal.model.FractalCanvas;
import com.fractal.model.Point;
import com.fractal.render.RenderUtils.WeightedVariation;
import com.fractal.transform.Variation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class SingleThreadRenderer implements FractalRenderer {
    private static final Logger log = LoggerFactory.getLogger(SingleThreadRenderer.class);
    private static final int WARMUP_ITERATIONS = 20;
    private static final int LOG_INTERVAL_PERCENT = 10;

    @Override
    public FractalCanvas render(AppConfig config) {
        log.info("Starting single-thread render: {}x{}, {} iterations",
            config.getWidth(), config.getHeight(), config.getIterationCount());

        FractalCanvas canvas = new FractalCanvas(config.getWidth(), config.getHeight());
        Random rng = new Random(config.getSeed());

        List<AffineTransform> affines = RenderUtils.buildAffineTransforms(config, rng);
        WeightedVariation[] variations = RenderUtils.buildVariations(config);

        runIterations(canvas, config, affines, variations, rng, config.getIterationCount(), 0);

        log.info("Render complete");
        return canvas;
    }

    static void runIterations(FractalCanvas canvas, AppConfig config,
                              List<AffineTransform> affines, WeightedVariation[] variations,
                              Random rng, int iterations, int offset) {
        double x = rng.nextDouble() * 4 - 2;
        double y = rng.nextDouble() * 4 - 2;

        int logStep = Math.max(1, iterations / (100 / LOG_INTERVAL_PERCENT));
        int symmetry = config.getSymmetryLevel();

        for (int i = 0; i < WARMUP_ITERATIONS + iterations; i++) {
            AffineTransform affine = affines.get(rng.nextInt(affines.size()));
            Point p = affine.apply(new Point(x, y));
            Variation variation = RenderUtils.selectVariation(variations, rng);
            p = variation.apply(p);
            x = p.x();
            y = p.y();

            if (i < WARMUP_ITERATIONS) continue;

            int iter = i - WARMUP_ITERATIONS;
            if (iter > 0 && iter % logStep == 0 && offset == 0) {
                log.info("Progress: {}%", (iter * 100 / iterations));
            }

            plotWithSymmetry(canvas, x, y, affine, symmetry);
        }
    }

    private static void plotWithSymmetry(FractalCanvas canvas, double x, double y,
                                          AffineTransform affine, int symmetry) {
        double angleStep = 2 * Math.PI / symmetry;
        for (int s = 0; s < symmetry; s++) {
            double angle = s * angleStep;
            double rx = x * Math.cos(angle) - y * Math.sin(angle);
            double ry = x * Math.sin(angle) + y * Math.cos(angle);
            int px = RenderUtils.toImageX(rx, canvas.getWidth());
            int py = RenderUtils.toImageY(ry, canvas.getHeight());
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    canvas.addColor(px + dx, py + dy, affine.colorR(), affine.colorG(), affine.colorB());
                }
            }
        }
    }
}
