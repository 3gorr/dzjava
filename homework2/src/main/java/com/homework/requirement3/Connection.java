package com.homework.requirement3;

public interface Connection extends AutoCloseable {
    void execute(String command);
    @Override void close();
}