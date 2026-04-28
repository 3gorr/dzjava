package com.fractal.render;

import com.fractal.config.AppConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BenchmarkTest {

    private AppConfig benchConfig(int threads) {
        AppConfig config = new AppConfig();
        config.setWidth(800);
        config.setHeight(600);
        config.setIterationCount(50000);
        config.setSeed(99L);
        config.setThreads(threads);
        config.setSymmetryLevel(1);
        config.setFunctions(List.of(
            new AppConfig.FunctionConfig("swirl", 1.0),
            new AppConfig.FunctionConfig("horseshoe", 0.8)
        ));
        return config;
    }

    @Test
    void multiThreadFasterThanSingleThread() {
        // single thread
        long t1 = System.currentTimeMillis();
        new SingleThreadRenderer().render(benchConfig(1));
        long singleTime = System.currentTimeMillis() - t1;

        // multi thread (4 threads)
        long t2 = System.currentTimeMillis();
        new MultiThreadRenderer().render(benchConfig(4));
        long multiTime = System.currentTimeMillis() - t2;

        System.out.printf("Benchmark: 1 thread=%dms, 4 threads=%dms%n", singleTime, multiTime);
        assertTrue(multiTime < singleTime,
            "Multi-thread (%dms) should be faster than single-thread (%dms)".formatted(multiTime, singleTime));
    }

    @Test
    void benchmarkAllThreadCounts() {
        int[] threadCounts = {1, 2, 4, 8};
        long[] times = new long[threadCounts.length];

        for (int i = 0; i < threadCounts.length; i++) {
            long start = System.currentTimeMillis();
            if (threadCounts[i] == 1) {
                new SingleThreadRenderer().render(benchConfig(1));
            } else {
                new MultiThreadRenderer().render(benchConfig(threadCounts[i]));
            }
            times[i] = System.currentTimeMillis() - start;
        }

        System.out.println("=== Benchmark results ===");
        for (int i = 0; i < threadCounts.length; i++) {
            System.out.printf("  %d thread(s): %dms%n", threadCounts[i], times[i]);
        }
        assertTrue(true, "Benchmark completed");
    }
}
