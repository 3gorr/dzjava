package com.homework.requirement4;

public class GoldenRatioStrategy implements CapacityStrategy {
    private static final double PHI = 1.61803398875;

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int cap = Math.max(1, currentCapacity);
        while (cap < requiredCapacity) {
            int next = (int) Math.ceil(cap * PHI);
            if (next <= cap) next = cap + 1;
            cap = next;
            if (cap < 0) return requiredCapacity;
        }
        return cap;
    }
}