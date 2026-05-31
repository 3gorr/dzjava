package com.csv.parser;

import com.csv.model.ResolvedField;
import com.csv.util.FieldResolver;
import com.csv.util.TypeConverter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvReader {

    public <T> List<T> read(InputStream inputStream, Class<T> klass) throws Exception {
        List<ResolvedField> fields = FieldResolver.resolve(klass);
        List<T> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return result;

            String[] headers = headerLine.split(",");
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim(), i);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] values = line.split(",", -1);
                T obj = klass.getDeclaredConstructor().newInstance();

                for (ResolvedField rf : fields) {
                    Integer idx = headerIndex.get(rf.header());
                    if (idx == null) continue;
                    String rawValue = idx < values.length ? values[idx].trim() : "";
                    Object converted = TypeConverter.convert(rawValue, rf.lastField());
                    FieldResolver.setValue(obj, rf.path(), converted);
                }
                result.add(obj);
            }
        }
        return result;
    }
}
