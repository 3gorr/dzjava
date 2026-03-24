package com.homework.requirement3;

import java.util.Random;

public class DefaultConnectionManager implements ConnectionManager {
    private final Random random;
    private final double faultyProbability;
    private final double faultyFailureProbability;

    public DefaultConnectionManager(Random random, double faultyProbability, double faultyFailureProbability) {
        this.random = (random == null) ? new Random() : random;
        if (faultyProbability < 0 || faultyProbability > 1) {
            throw new IllegalArgumentException("faultyProbability must be in [0,1]");
        }
        this.faultyProbability = faultyProbability;
        this.faultyFailureProbability = faultyFailureProbability;
    }

    @Override
    public Connection getConnection() {
        if (random.nextDouble() < faultyProbability) {
            return new FaultyConnection(random, faultyFailureProbability);
        }
        return new StableConnection();
    }
}