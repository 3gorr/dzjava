package com.homework.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogReader {

    private static final Logger log = LoggerFactory.getLogger(LogReader.class);

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".log", ".txt");

    public Stream<String> readLines(String[] paths) {
        List<Path> filePaths = new ArrayList<>();
        for (String pathStr : paths) {
            filePaths.addAll(resolve(pathStr));
        }
        if (filePaths.isEmpty()) {
            throw new IllegalArgumentException("No log files found for the given paths.");
        }
        return filePaths.stream().flatMap(this::streamFile);
    }

    private List<Path> resolve(String pathStr) {
        boolean isGlob = pathStr.contains("*") || pathStr.contains("?")
            || pathStr.contains("{") || pathStr.contains("[");

        if (!isGlob) {
            Path p = Path.of(pathStr);
            if (!Files.exists(p)) {
                throw new IllegalArgumentException("File not found: " + pathStr);
            }
            if (!Files.isRegularFile(p)) {
                throw new IllegalArgumentException("Not a regular file: " + pathStr);
            }
            validateExtension(p);
            return List.of(p);
        }

        return resolveGlob(pathStr);
    }

    private List<Path> resolveGlob(String pathStr) {
        Path pathObj = Path.of(pathStr);
        Path parent = pathObj.getParent();
        final Path baseDir = (parent != null) ? parent : Path.of(".");

        String globFileName = pathObj.getFileName().toString();
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globFileName);

        List<Path> result = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(baseDir, 1)) {
            walk.filter(p -> !p.equals(baseDir))
                .filter(p -> matcher.matches(p.getFileName()))
                .filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString().toLowerCase();
                    return ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith);
                })
                .forEach(p -> {
                    log.info("Found file matching glob: {}", p);
                    result.add(p);
                });
        } catch (IOException e) {
            throw new UncheckedIOException("Error walking directory: " + baseDir, e);
        }

        if (result.isEmpty()) {
            throw new IllegalArgumentException("No matching files found for glob pattern: " + pathStr);
        }
        return result;
    }

    private void validateExtension(Path p) {
        String name = p.getFileName().toString().toLowerCase();
        boolean valid = ALLOWED_EXTENSIONS.stream().anyMatch(name::endsWith);
        if (!valid) {
            throw new IllegalArgumentException(
                "Unsupported file format: " + p + ". Allowed extensions: .log, .txt");
        }
    }

    private Stream<String> streamFile(Path p) {
        try {
            log.info("Reading file: {}", p);
            return Files.lines(p);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read file: " + p, e);
        }
    }
}
