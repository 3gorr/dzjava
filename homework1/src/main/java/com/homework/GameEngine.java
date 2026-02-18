package com.homework;

import java.util.*;

public class GameEngine {
    public enum State { RUNNING, WIN, LOSE }

    private final String category;
    private final Difficulty difficulty;
    private final String answer;
    private final char[] progress;
    private final Set<Character> used = new LinkedHashSet<>();
    private final int maxMistakes;
    private int mistakes;
    private State state = State.RUNNING;

    private boolean hintShown = false;
    private final String hint;

    public GameEngine(String category, Difficulty difficulty, String answer, String hint) {
        this.category = category;
        this.difficulty = difficulty;
        this.answer = answer.toLowerCase(Locale.ROOT);
        this.hint = hint;

        this.maxMistakes = difficulty.getMaxMistakes();
        this.progress = new char[this.answer.length()];
        Arrays.fill(this.progress, '_');
    }

    public String getCategory() { return category; }
    public Difficulty getDifficulty() { return difficulty; }
    public int getMaxMistakes() { return maxMistakes; }
    public int getMistakes() { return mistakes; }
    public State getState() { return state; }

    public String progressString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < progress.length; i++) {
            sb.append(progress[i]);
            if (i + 1 < progress.length) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    public String usedLettersString() {
        if (used.isEmpty()) return "-";
        StringBuilder sb = new StringBuilder();
        for (char c : used) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean canShowHint() {
        return hint != null && !hintShown;
    }

    public String showHint() {
        if (hint == null) return null;
        hintShown = true;
        return hint;
    }

    public String guess(char ch) {
        if (state != State.RUNNING) {
            return "Игра уже завершена.";
        }
        ch = Character.toLowerCase(ch);
        if (!Character.isLetter(ch)) {
            return "Нужно ввести букву.";
        }
        if (used.contains(ch)) {
            return "Эта буква уже была.";
        }
        used.add(ch);
        boolean ok = false;
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == ch) {
                progress[i] = ch;
                ok = true;
            }
        }
        if (!ok) {
            mistakes++;
            if (mistakes >= maxMistakes) {
                state = State.LOSE;
                return "Неверно. Попыток больше нет.";
            }
            return "Неверно.";
        }
        if (isSolved()) {
            state = State.WIN;
            return "Верно! Слово угадано!";
        }
        return "Верно!";
    }

    private boolean isSolved() {
        for (char c : progress) {
            if (c == '_') return false;
        }
        return true;
    }

    public String answer() {
        return answer;
    }
}