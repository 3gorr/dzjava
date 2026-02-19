package com.homework.requirement3;

public final class PopularCommandExecutor {
    private final ConnectionManager manager;
    private final int maxAttempts;

    public PopularCommandExecutor(ConnectionManager manager, int maxAttempts) {
        if (manager == null) throw new IllegalArgumentException("manager is null");
        if (maxAttempts <= 0) throw new IllegalArgumentException("maxAttempts must be > 0");
        this.manager = manager;
        this.maxAttempts = maxAttempts;
    }

    public void updatePackages() {
        tryExecute("apt update && apt upgrade -y");
    }

    void tryExecute(String command) {
        ConnectionException last = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try (Connection c = manager.getConnection()) {
                c.execute(command);
                return;
            } catch (ConnectionException e) {
                last = e;
            }
        }
        throw new ConnectionException("Failed after " + maxAttempts + " attempts", last);
    }
}