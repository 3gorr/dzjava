package com.homework;

import java.util.*;

public class Dictionary {
    private final Map<String, List<String>> wordsByCategory = new LinkedHashMap<>();
    private final Map<String, Map<String, String>> hints = new HashMap<>();
    private final Random rnd;

    public Dictionary(Random rnd) {
        this.rnd = rnd;
        init();
    }

    private void init() {
        wordsByCategory.put("Животные", List.of(
                "тигр", "слон", "жираф", "волк", "дельфин", "попугай"
        ));
        wordsByCategory.put("Еда", List.of(
                "пицца", "суп", "картошка", "шоколад", "яблоко", "банан"
        ));
        wordsByCategory.put("Айтишка", List.of(
                "компьютер", "алгоритм", "программа", "переменная", "компилятор"
        ));

        putHint("Животные", "тигр", "полосатый хищник");
        putHint("Животные", "слон", "самое крупное сухопутное животное");
        putHint("Еда", "пицца", "круглое блюдо с начинкой");
        putHint("Айтишка", "алгоритм", "набор шагов для решения задачи");
        putHint("Айтишка", "компилятор", "переводит код в машинный формат");
    }

    private void putHint(String category, String word, String hint) {
        hints.computeIfAbsent(category, k -> new HashMap<>()).put(word, hint);
    }

    public String randomCategory() {
        List<String> cats = new ArrayList<>(wordsByCategory.keySet());
        return cats.get(rnd.nextInt(cats.size()));
    }

    public boolean hasCategory(String category) {
        return wordsByCategory.containsKey(category);
    }

    public Set<String> categories() {
        return wordsByCategory.keySet();
    }

    public String randomWord(String category) {
        List<String> list = wordsByCategory.get(category);
        if (list == null || list.isEmpty()) return null;
        return list.get(rnd.nextInt(list.size()));
    }

    public String hintOrNull(String category, String word) {
        Map<String, String> m = hints.get(category);
        if (m == null) return null;
        return m.get(word);
    }
}
