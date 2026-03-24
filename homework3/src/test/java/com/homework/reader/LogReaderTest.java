package com.homework.reader;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogReaderTest {

    private final LogReader reader = new LogReader();

    @Test
    void readsLinesFromExistingLogFile(@TempDir Path tempDir) throws IOException {
        Path logFile = tempDir.resolve("access.log");
        Files.writeString(logFile, "line1\nline2\nline3\n");

        List<String> lines = reader.readLines(new String[]{logFile.toString()}).toList();

        assertEquals(3, lines.size());
        assertEquals("line1", lines.get(0));
        assertEquals("line2", lines.get(1));
        assertEquals("line3", lines.get(2));
    }

    @Test
    void readsLinesFromTxtFile(@TempDir Path tempDir) throws IOException {
        Path txtFile = tempDir.resolve("access.txt");
        Files.writeString(txtFile, "line1\nline2\n");

        List<String> lines = reader.readLines(new String[]{txtFile.toString()}).toList();

        assertEquals(2, lines.size());
    }

    @Test
    void throwsForNonExistentFile() {
        assertThrows(IllegalArgumentException.class,
            () -> reader.readLines(new String[]{"/non/existent/file.log"}).toList());
    }

    @Test
    void throwsForUnsupportedExtension(@TempDir Path tempDir) throws IOException {
        Path badFile = tempDir.resolve("access.csv");
        Files.writeString(badFile, "data");

        assertThrows(IllegalArgumentException.class,
            () -> reader.readLines(new String[]{badFile.toString()}).toList());
    }

    @Test
    void readsMultipleFiles(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve("access1.log");
        Path file2 = tempDir.resolve("access2.log");
        Files.writeString(file1, "a\nb\n");
        Files.writeString(file2, "c\nd\n");

        List<String> lines = reader.readLines(
            new String[]{file1.toString(), file2.toString()}
        ).toList();

        assertEquals(4, lines.size());
    }

    @Test
    void resolvesGlobPattern(@TempDir Path tempDir) throws IOException {
        Path file1 = tempDir.resolve("server1.log");
        Path file2 = tempDir.resolve("server2.log");
        Path otherFile = tempDir.resolve("unrelated.txt");
        Files.writeString(file1, "log1line\n");
        Files.writeString(file2, "log2line\n");
        Files.writeString(otherFile, "other\n");

        String glob = tempDir.toString() + "/server*.log";
        List<String> lines = reader.readLines(new String[]{glob}).toList();

        assertEquals(2, lines.size());
        assertTrue(lines.contains("log1line"));
        assertTrue(lines.contains("log2line"));
    }

    @Test
    void throwsWhenGlobMatchesNoFiles(@TempDir Path tempDir) {
        String glob = tempDir.toString() + "/nonexistent*.log";
        assertThrows(IllegalArgumentException.class,
            () -> reader.readLines(new String[]{glob}).toList());
    }
}
