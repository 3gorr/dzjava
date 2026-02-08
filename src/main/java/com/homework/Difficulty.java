package com.homework;

import java.util.Random;

public enum Difficulty {
    EASY(7),
    MEDIUM(5),
    HARD(4);

    private final int maxMistakes;

    Difficulty(int maxMistakes) {
        this.maxMistakes = maxMistakes;
    }

    public int getMaxMistakes() {
        return maxMistakes;
    }

    public static Difficulty random(Random rnd) {
        Difficulty[] values = values();
        return values[rnd.nextInt(values.length)];
    }

    public static Difficulty fromStringOrNull(String s) {
        if (s == null) return null;
        s = s.trim().toLowerCase();
        return switch (s) {
            case "easy", "легко", "лёгко" -> EASY;
            case "medium", "средне" -> MEDIUM;
            case "hard", "сложно" -> HARD;
            default -> null;
        };
    }
}
