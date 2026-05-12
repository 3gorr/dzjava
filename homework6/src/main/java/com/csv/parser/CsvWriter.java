package com.csv.parser;

import com.csv.model.ResolvedField;
import com.csv.util.FieldResolver;
import com.csv.util.TypeConverter;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWriter {

    public <T> void write(OutputStream outputStream, Collection<T> collection, Class<T> klass) throws Exception {
        List<ResolvedField> fields = FieldResolver.resolve(klass);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writer.write(fields.stream().map(ResolvedField::header).collect(Collectors.joining(",")));
            writer.newLine();

            for (T obj : collection) {
                List<String> values = new ArrayList<>();
                for (ResolvedField rf : fields) {
                    Object value = FieldResolver.getValue(obj, rf.path());
                    values.add(TypeConverter.toString(value, rf.lastField()));
                }
                writer.write(String.join(",", values));
                writer.newLine();
            }
        }
    }
}
