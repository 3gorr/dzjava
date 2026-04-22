package com.fractal.render;

import com.fractal.config.AppConfig;
import com.fractal.model.FractalCanvas;

public interface FractalRenderer {
    FractalCanvas render(AppConfig config);
}
