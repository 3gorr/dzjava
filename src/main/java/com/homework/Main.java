package com.homework;

import java.util.*;

public class Main {
    private static final int MIN_LEN = 3;
    private static final int MAX_LEN = 20;

    public static void main(String[] args) {
        Random rnd = new Random();
        Dictionary dict = new Dictionary(rnd);
        String category = null;
        Difficulty difficulty = null;

        if (args.length == 0) {
            Scanner sc = new Scanner(System.in);
            category = chooseCategoryInteractive(sc, dict);
            difficulty = chooseDifficultyInteractive(sc, rnd);
        } else {
            if (args.length >= 1) {
                category = args[0].trim().toLowerCase();
            }
            if (args.length >= 2) {
                difficulty = Difficulty.fromStringOrNull(args[1]);
            }
            if (category == null || category.isBlank() || !dict.hasCategory(category)) {
                category = dict.randomCategory();
            }
            if (difficulty == null) {
                difficulty = Difficulty.random(rnd);
            }
        }
        String word = pickValidWord(dict, category);
        if (word == null) {
            System.out.println("Не удалось выбрать корректное слово. Проверьте словарь.");
            return;
        }
        String hint = dict.hintOrNull(category, word);
        GameEngine game = new GameEngine(category, difficulty, word, hint);
        runInteractive(game);
    }

    private static String pickValidWord(Dictionary dict, String category) {
        for (int i = 0; i < 50; i++) {
            String w = dict.randomWord(category);
            if (w == null) return null;
            int len = w.length();
            if (len >= MIN_LEN && len <= MAX_LEN) return w;
        }
        return null;
    }

    private static void runInteractive(GameEngine game) {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- ВИСЕЛИЦА ---");
        System.out.println("Категория: " + game.getCategory());
        System.out.println("Сложность: " + game.getDifficulty());
        System.out.println("Максимум ошибок: " + game.getMaxMistakes());
        System.out.println("Подсказка: напишите '?' , чтобы посмотреть подсказку.");
        System.out.println();
        while (game.getState() == GameEngine.State.RUNNING) {
            redraw(game);
            System.out.print("Введите букву: ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("Пустой ввод.");
                continue;
            }
            if (line.equals("?")) {
                if (game.canShowHint()) {
                    System.out.println("Подсказка: " + game.showHint());
                } else {
                    System.out.println("Подсказка недоступна или уже показана.");
                }
                continue;
            }
            if (line.length() != 1) {
                System.out.println("Введите одну букву, а не строку.");
                continue;
            }
            char ch = line.charAt(0);
            String msg = game.guess(ch);
            System.out.println(msg);
        }
        redraw(game);
        if (game.getState() == GameEngine.State.WIN) {
            System.out.println("Победа! Загаданное слово: " + game.answer());
        } else {
            System.out.println("Поражение! Загаданное слово: " + game.answer());
        }
    }

    private static void redraw(GameEngine game) {
        clearConsole();
        int mistakes = game.getMistakes();
        System.out.println(HangmanArt.draw(mistakes, game.getMaxMistakes()));
        System.out.println("Слово: " + game.progressString());
        System.out.println("Использовано: " + game.usedLettersString());
        int left = game.getMaxMistakes() - mistakes;
        System.out.println("Ошибок: " + mistakes + " / " + game.getMaxMistakes() + " | Осталось попыток: " + left);
        System.out.println();
    }

    private static void clearConsole() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }

    private static String chooseCategoryInteractive(Scanner sc, Dictionary dict) {
        List<String> cats = new ArrayList<>(dict.categories());
        System.out.println("Выберите категорию (нажмите Enter для случайной категории):");
        for (int i = 0; i < cats.size(); i++) {
            System.out.println((i + 1) + ") " + cats.get(i));
        }
        System.out.print("> ");
        String line = sc.nextLine().trim().toLowerCase();
        if (line.isEmpty()) {
            return dict.randomCategory();
        }
        try {
            int idx = Integer.parseInt(line);
            if (idx >= 1 && idx <= cats.size()) {
                return cats.get(idx - 1);
            }
        } catch (NumberFormatException ignored) {}
        if (dict.hasCategory(line)) {
            return line;
        }
        return dict.randomCategory();
    }

    private static Difficulty chooseDifficultyInteractive(Scanner sc, Random rnd) {
        System.out.println("Выберите сложность (нажмите Enter для случайной сложности):");
        System.out.println("1 = EASY");
        System.out.println("2 = MEDIUM");
        System.out.println("3 = HARD");
        System.out.print("> ");
        String line = sc.nextLine().trim().toLowerCase();
        if (line.isEmpty()) {
            return Difficulty.random(rnd);
        }
        if (line.equals("1")) {
            return Difficulty.EASY;
        }
        if (line.equals("2")) {
            return Difficulty.MEDIUM;
        }
        if (line.equals("3")) {
            return Difficulty.HARD;
        }
        Difficulty d = Difficulty.fromStringOrNull(line);
        return (d != null) ? d : Difficulty.random(rnd);
    }
}