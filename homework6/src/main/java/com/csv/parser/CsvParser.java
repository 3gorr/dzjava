package com.csv.parser;

import com.csv.io.StreamFactory;

import java.util.Collection;
import java.util.List;

public class CsvParser {

    private final CsvReader reader = new CsvReader();
    private final CsvWriter writer = new CsvWriter();

    public <T> List<T> parseFromCsv(String filename, Class<T> klass) {
        try {
            return reader.read(StreamFactory.inputStream(filename), klass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + filename, e);
        }
    }

    public <T> void saveToCsv(String filename, Collection<T> collection, Class<T> klass) {
        try {
            writer.write(StreamFactory.outputStream(filename), collection, klass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save " + filename, e);
        }
    }
}
