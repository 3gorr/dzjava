package com.fractal.render;

import com.fractal.config.AppConfig;
import com.fractal.model.AffineTransform;
import com.fractal.model.Point;
import com.fractal.transform.Variation;
import com.fractal.transform.VariationRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RenderUtils {

    public static List<AffineTransform> buildAffineTransforms(AppConfig config, Random rng) {
        List<AffineTransform> transforms = new ArrayList<>();
        if (config.getAffineParams() != null && !config.getAffineParams().isEmpty()) {
            int i = 0;
            for (AppConfig.AffineParamConfig p : config.getAffineParams()) {
                int r = (i * 80 + 50) % 256;
                int g = (i * 130 + 100) % 256;
                int b = (i * 200 + 150) % 256;
                transforms.add(new AffineTransform(p.a(), p.b(), p.c(), p.d(), p.e(), p.f(), r, g, b));
                i++;
            }
        } else {
            for (int i = 0; i < 6; i++) {
                transforms.add(randomAffine(rng, i));
            }
        }
        return transforms;
    }

    private static AffineTransform randomAffine(Random rng, int index) {
        double a, b, c, d, e, f;
        do {
            a = rng.nextDouble() * 2 - 1;
            b = rng.nextDouble() * 2 - 1;
            d = rng.nextDouble() * 2 - 1;
            e = rng.nextDouble() * 2 - 1;
        } while (Math.abs(a * e - b * d) >= 1.0);
        c = rng.nextDouble() * 2 - 1;
        f = rng.nextDouble() * 2 - 1;

        float hue = (index * 0.618034f) % 1.0f;
        float saturation = 0.75f + rng.nextFloat() * 0.25f;
        float brightness = 0.85f + rng.nextFloat() * 0.15f;
        int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int bl = rgb & 0xFF;
        return new AffineTransform(a, b, c, d, e, f, r, g, bl);
    }

    public static WeightedVariation[] buildVariations(AppConfig config) {
        if (config.getFunctions() == null || config.getFunctions().isEmpty()) {
            Variation linear = VariationRegistry.get("linear");
            return new WeightedVariation[]{new WeightedVariation(linear, 1.0)};
        }
        return config.getFunctions().stream()
            .map(f -> new WeightedVariation(VariationRegistry.get(f.name()), f.weight()))
            .toArray(WeightedVariation[]::new);
    }

    public static Variation selectVariation(WeightedVariation[] variations, Random rng) {
        double totalWeight = 0;
        for (WeightedVariation wv : variations) totalWeight += wv.weight();
        double pick = rng.nextDouble() * totalWeight;
        double cumulative = 0;
        for (WeightedVariation wv : variations) {
            cumulative += wv.weight();
            if (pick <= cumulative) return wv.variation();
        }
        return variations[variations.length - 1].variation();
    }

    public record WeightedVariation(Variation variation, double weight) {}

    public static int toImageX(double x, int width) {
        return (int) ((x + 2.0) / 4.0 * width);
    }

    public static int toImageY(double y, int height) {
        return (int) ((y + 2.0) / 4.0 * height);
    }
}
