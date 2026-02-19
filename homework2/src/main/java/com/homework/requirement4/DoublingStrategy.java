package com.homework.requirement4;

public class DoublingStrategy implements CapacityStrategy {
    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int cap = Math.max(1, currentCapacity);
        while (cap < requiredCapacity) {
            cap = cap * 2;
            if (cap < 0) {
                return requiredCapacity;
            }
        }
        return cap;
    }
}