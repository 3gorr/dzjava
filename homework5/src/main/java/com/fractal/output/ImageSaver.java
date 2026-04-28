package com.fractal.output;

import com.fractal.model.FractalCanvas;
import com.fractal.model.Pixel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSaver {
    private static final Logger log = LoggerFactory.getLogger(ImageSaver.class);

    public void save(FractalCanvas canvas, String path, int supersample) throws IOException {
        log.info("Saving image to: {}", path);
        int outW = canvas.getWidth() / supersample;
        int outH = canvas.getHeight() / supersample;
        BufferedImage image = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < outH; y++) {
            for (int x = 0; x < outW; x++) {
                int rSum = 0, gSum = 0, bSum = 0;
                for (int dy = 0; dy < supersample; dy++) {
                    for (int dx = 0; dx < supersample; dx++) {
                        Pixel p = canvas.getPixel(x * supersample + dx, y * supersample + dy);
                        rSum += p.getR();
                        gSum += p.getG();
                        bSum += p.getB();
                    }
                }
                int count = supersample * supersample;
                int rgb = (clamp(rSum / count) << 16) | (clamp(gSum / count) << 8) | clamp(bSum / count);
                image.setRGB(x, y, rgb);
            }
        }

        File outputFile = new File(path);
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        ImageIO.write(image, "PNG", outputFile);
        log.info("Image saved: {}x{} → {}", outW, outH, path);
    }

    public void save(FractalCanvas canvas, String path) throws IOException {
        save(canvas, path, 1);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
