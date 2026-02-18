package com.homework;

public class HangmanArt {
    private static final String[] STAGES = new String[]{
            """
              +---+
              |   |
                  |
                  |
                  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
                  |
                  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
              |   |
                  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
             /|   |
                  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
             /|\\  |
                  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
             /|\\  |
             /    |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
             /|\\  |
             / \\  |
                  |
            =========
            """,
            """
              +---+
              |   |
              O   |
             /|\\  |
             / \\  |
                  |
            RIP   |
            =========
            """
    };

    public static String draw(int mistakes, int maxMistakes) {
        if (maxMistakes <= 0) {
            return STAGES[STAGES.length - 1];
        }
        int maxStage = STAGES.length - 1;
        double ratio = (double) mistakes / (double) maxMistakes;
        int stage = (int) Math.round(ratio * maxStage);
        if (stage < 0) {
            stage = 0;
        }
        if (stage > maxStage) {
            stage = maxStage;
        }
        return STAGES[stage];
    }
}
