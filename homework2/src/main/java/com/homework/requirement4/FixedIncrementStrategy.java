package com.homework.requirement4;

public class FixedIncrementStrategy implements CapacityStrategy {
    private final int increment;

    public FixedIncrementStrategy(int increment) {
        if (increment <= 0) throw new IllegalArgumentException("increment must be > 0");
        this.increment = increment;
    }

    @Override
    public int calculateNewCapacity(int currentCapacity, int requiredCapacity) {
        int cap = Math.max(1, currentCapacity);
        while (cap < requiredCapacity) {
            cap += increment;
            if (cap < 0) return requiredCapacity;
        }
        return cap;
    }
}