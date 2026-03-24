package com.homework.analyzer;

import com.homework.model.LogRecord;
import com.homework.model.LogReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Stream;

public class LogAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(LogAnalyzer.class);
    private static final int TOP_RESOURCES = 10;

    public LogReport analyze(
        Stream<LogRecord> records,
        LocalDate from,
        LocalDate to,
        List<String> files
    ) {
        long count = 0;
        long sum = 0;
        long max = 0;
        List<Long> sizes = new ArrayList<>();
        Map<String, Long> resourceCounts = new HashMap<>();
        Map<Integer, Long> codeCounts = new HashMap<>();
        TreeMap<LocalDate, Long> dateCounts = new TreeMap<>();

        Iterator<LogRecord> it = records
            .filter(r -> {
                LocalDate date = r.timeLocal().toLocalDate();
                if (from != null && date.isBefore(from)) return false;
                if (to != null && date.isAfter(to)) return false;
                return true;
            })
            .iterator();

        while (it.hasNext()) {
            LogRecord r = it.next();
            count++;
            long size = r.bodyBytesSent();
            sum += size;
            if (size > max) max = size;
            sizes.add(size);
            resourceCounts.merge(r.resource(), 1L, Long::sum);
            codeCounts.merge(r.status(), 1L, Long::sum);
            dateCounts.merge(r.timeLocal().toLocalDate(), 1L, Long::sum);
        }

        log.info("Analyzed {} log records", count);

        double avg = count > 0 ? round2((double) sum / count) : 0.0;
        double p95 = computeP95(sizes);
        long maxSize = max;

        List<LogReport.ResourceStat> topResources = resourceCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(TOP_RESOURCES)
            .map(e -> new LogReport.ResourceStat(e.getKey(), e.getValue()))
            .toList();

        List<LogReport.CodeStat> codeStats = codeCounts.entrySet().stream()
            .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
            .map(e -> new LogReport.CodeStat(e.getKey(), e.getValue()))
            .toList();

        final long totalCount = count;
        List<LogReport.DateStat> dateStats = dateCounts.entrySet().stream()
            .map(e -> {
                String weekday = e.getKey()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                double pct = totalCount > 0 ? round2(e.getValue() * 100.0 / totalCount) : 0.0;
                return new LogReport.DateStat(e.getKey(), weekday, e.getValue(), pct);
            })
            .toList();

        return new LogReport(files, from, to, count, avg, maxSize, p95, topResources, codeStats, dateStats);
    }

    private double computeP95(List<Long> sizes) {
        if (sizes.isEmpty()) return 0.0;
        Collections.sort(sizes);
        int index = (int) Math.ceil(0.95 * sizes.size()) - 1;
        return round2(sizes.get(Math.max(0, index)));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
