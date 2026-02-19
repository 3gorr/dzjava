package com.homework.requirement3;

import java.util.Random;

public class FaultyConnectionManager implements ConnectionManager {
    private final Random random;
    private final double faultyFailureProbability;

    public FaultyConnectionManager(Random random, double faultyFailureProbability) {
        this.random = (random == null) ? new Random() : random;
        this.faultyFailureProbability = faultyFailureProbability;
    }

    @Override
    public Connection getConnection() {
        return new FaultyConnection(random, faultyFailureProbability);
    }
}