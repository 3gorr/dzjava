package com.homework;

import com.homework.cli.LogAnalyzerCommand;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new LogAnalyzerCommand()).execute(args);
        System.exit(exitCode);
    }
}
