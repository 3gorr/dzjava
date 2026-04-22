package com.fractal.render;

import com.fractal.config.AppConfig;
import com.fractal.model.AffineTransform;
import com.fractal.model.FractalCanvas;
import com.fractal.render.RenderUtils.WeightedVariation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiThreadRenderer implements FractalRenderer {
    private static final Logger log = LoggerFactory.getLogger(MultiThreadRenderer.class);

    @Override
    public FractalCanvas render(AppConfig config) {
        int threads = config.getThreads();
        log.info("Starting multi-thread render: {}x{}, {} iterations, {} thread(s)",
            config.getWidth(), config.getHeight(), config.getIterationCount(), threads);

        FractalCanvas canvas = new FractalCanvas(config.getWidth(), config.getHeight());
        Random seedRng = new Random(config.getSeed());

        List<AffineTransform> affines = RenderUtils.buildAffineTransforms(config, seedRng);
        WeightedVariation[] variations = RenderUtils.buildVariations(config);

        int iterationsPerThread = config.getIterationCount() / threads;
        AtomicInteger completedThreads = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final long threadSeed = seedRng.nextLong();
            final int threadIndex = t;
            executor.submit(() -> {
                Random rng = new Random(threadSeed);
                SingleThreadRenderer.runIterations(canvas, config, affines, variations, rng,
                    iterationsPerThread, threadIndex);
                int done = completedThreads.incrementAndGet();
                log.info("Thread {}/{} completed", done, threads);
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Rendering interrupted", e);
        }

        log.info("Multi-thread render complete");
        return canvas;
    }
}
