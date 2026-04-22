package com.fractal.render;

import com.fractal.model.FractalCanvas;
import com.fractal.model.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GammaCorrectionPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(GammaCorrectionPostProcessor.class);
    private static final double GAMMA = 2.2;

    public void process(FractalCanvas canvas) {
        log.info("Applying gamma correction (gamma={})", GAMMA);
        int maxHits = 1;
        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                maxHits = Math.max(maxHits, canvas.getPixel(x, y).getHitCount());
            }
        }

        double logMax = Math.log10(maxHits);
        if (logMax == 0) return;

        for (int y = 0; y < canvas.getHeight(); y++) {
            for (int x = 0; x < canvas.getWidth(); x++) {
                Pixel pixel = canvas.getPixel(x, y);
                if (pixel.getHitCount() == 0) continue;
                double alpha = Math.log10(pixel.getHitCount()) / logMax;
                double factor = Math.pow(alpha, 1.0 / GAMMA);
                pixel.setR((int) (pixel.getR() * factor));
                pixel.setG((int) (pixel.getG() * factor));
                pixel.setB((int) (pixel.getB() * factor));
            }
        }
        log.info("Gamma correction applied");
    }
}
