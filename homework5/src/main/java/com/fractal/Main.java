package com.fractal;

import com.fractal.config.AppConfig;
import com.fractal.config.CliConfigParser;
import com.fractal.config.ConfigMerger;
import com.fractal.config.ConfigValidator;
import com.fractal.config.JsonConfigParser;
import com.fractal.model.FractalCanvas;
import com.fractal.output.ImageSaver;
import com.fractal.render.FractalRenderer;
import com.fractal.render.GammaCorrectionPostProcessor;
import com.fractal.render.MultiThreadRenderer;
import com.fractal.render.SingleThreadRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            run(args);
        } catch (Exception e) {
            log.error("Fatal error: {} at {}", e.getMessage(),
                e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "unknown");
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    static void run(String[] args) throws Exception {
        CliConfigParser cliParser = CliConfigParser.parse(args);
        AppConfig cliConfig = cliParser.buildPartialConfig();

        AppConfig base = new AppConfig();
        if (cliParser.getConfigPath() != null) {
            JsonConfigParser jsonParser = new JsonConfigParser();
            base = jsonParser.parse(cliParser.getConfigPath());
            log.info("Loaded JSON config from: {}", cliParser.getConfigPath());
        }

        ConfigMerger merger = new ConfigMerger();
        AppConfig config = merger.merge(base, cliConfig);

        ConfigValidator validator = new ConfigValidator();
        validator.validate(config);

        long startTime = System.currentTimeMillis();

        FractalRenderer renderer = config.getThreads() > 1
            ? new MultiThreadRenderer()
            : new SingleThreadRenderer();

        int ss = config.getSupersample();
        config.setWidth(config.getWidth() * ss);
        config.setHeight(config.getHeight() * ss);

        FractalCanvas canvas = renderer.render(config);

        new GammaCorrectionPostProcessor().process(canvas);

        new ImageSaver().save(canvas, config.getOutputPath(), ss);

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("Total time: {}ms ({} thread(s))", elapsed, config.getThreads());
    }
}
