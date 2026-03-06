package com.homework.analyzer;

import com.homework.model.LogRecord;
import com.homework.model.LogReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LogAnalyzerTest {

    private final LogAnalyzer analyzer = new LogAnalyzer();

    @Test
    void analyzesEmptyStream() {
        LogReport report = analyzer.analyze(Stream.empty(), null, null, List.of());

        assertEquals(0L, report.totalRequestsCount());
        assertEquals(0.0, report.avgResponseSize());
        assertEquals(0L, report.maxResponseSize());
        assertEquals(0.0, report.p95ResponseSize());
        assertTrue(report.resources().isEmpty());
        assertTrue(report.responseCodes().isEmpty());
        assertTrue(report.requestsPerDate().isEmpty());
    }

    @Test
    void calculatesCorrectTotalCount() {
        List<LogRecord> records = List.of(
            record(200, 100),
            record(200, 200),
            record(404, 300)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of("test.log"));

        assertEquals(3L, report.totalRequestsCount());
    }

    @Test
    void calculatesCorrectAverageResponseSize() {
        List<LogRecord> records = List.of(
            record(200, 100),
            record(200, 200),
            record(200, 300)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(200.0, report.avgResponseSize());
    }

    @Test
    void calculatesCorrectMaxResponseSize() {
        List<LogRecord> records = List.of(
            record(200, 100),
            record(200, 500),
            record(200, 300)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(500L, report.maxResponseSize());
    }

    @Test
    void calculatesP95ResponseSize() {
        List<Long> sizes = List.of(10L, 20L, 30L, 40L, 50L, 60L, 70L, 80L, 90L, 100L);
        List<LogRecord> records = sizes.stream()
            .map(s -> record(200, s))
            .toList();

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(100.0, report.p95ResponseSize());
    }

    @Test
    void countsTopResourcesCorrectly() {
        List<LogRecord> records = List.of(
            record("/a", 200, 100),
            record("/a", 200, 100),
            record("/a", 200, 100),
            record("/b", 200, 100),
            record("/b", 200, 100),
            record("/c", 200, 100)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(3, report.resources().size());
        assertEquals("/a", report.resources().get(0).resource());
        assertEquals(3L, report.resources().get(0).count());
        assertEquals("/b", report.resources().get(1).resource());
        assertEquals(2L, report.resources().get(1).count());
    }

    @Test
    void limitsTopResourcesToTen() {
        List<LogRecord> records = java.util.stream.IntStream.rangeClosed(1, 15)
            .mapToObj(i -> record("/resource-" + i, 200, 100))
            .toList();

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertTrue(report.resources().size() <= 10);
    }

    @Test
    void countsResponseCodesCorrectly() {
        List<LogRecord> records = List.of(
            record(200, 100),
            record(200, 200),
            record(404, 50),
            record(500, 10)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(3, report.responseCodes().size());
        assertEquals(200, report.responseCodes().get(0).code());
        assertEquals(2L, report.responseCodes().get(0).count());
    }

    @Test
    void filtersRecordsByFromDate() {
        ZonedDateTime earlyDate = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime lateDate = ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        List<LogRecord> records = List.of(
            record(earlyDate, 200, 100),
            record(lateDate, 200, 200)
        );

        LogReport report = analyzer.analyze(
            records.stream(),
            LocalDate.of(2024, 3, 1),
            null,
            List.of()
        );

        assertEquals(1L, report.totalRequestsCount());
        assertEquals(200.0, report.avgResponseSize());
    }

    @Test
    void filtersRecordsByToDate() {
        ZonedDateTime earlyDate = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime lateDate = ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);

        List<LogRecord> records = List.of(
            record(earlyDate, 200, 100),
            record(lateDate, 200, 200)
        );

        LogReport report = analyzer.analyze(
            records.stream(),
            null,
            LocalDate.of(2024, 3, 1),
            List.of()
        );

        assertEquals(1L, report.totalRequestsCount());
        assertEquals(100.0, report.avgResponseSize());
    }

    @Test
    void filtersRecordsByDateRange() {
        List<LogRecord> records = List.of(
            record(ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), 200, 100),
            record(ZonedDateTime.of(2024, 3, 15, 0, 0, 0, 0, ZoneOffset.UTC), 200, 200),
            record(ZonedDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC), 200, 300)
        );

        LogReport report = analyzer.analyze(
            records.stream(),
            LocalDate.of(2024, 2, 1),
            LocalDate.of(2024, 4, 1),
            List.of()
        );

        assertEquals(1L, report.totalRequestsCount());
        assertEquals(200.0, report.avgResponseSize());
    }

    @Test
    void includesFromToInReport() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);

        LogReport report = analyzer.analyze(Stream.empty(), from, to, List.of("test.log"));

        assertEquals(from, report.from());
        assertEquals(to, report.to());
    }

    @Test
    void collectsRequestsPerDate() {
        List<LogRecord> records = List.of(
            record(ZonedDateTime.of(2024, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC), 200, 100),
            record(ZonedDateTime.of(2024, 3, 1, 12, 0, 0, 0, ZoneOffset.UTC), 200, 100),
            record(ZonedDateTime.of(2024, 3, 2, 10, 0, 0, 0, ZoneOffset.UTC), 200, 100)
        );

        LogReport report = analyzer.analyze(records.stream(), null, null, List.of());

        assertEquals(2, report.requestsPerDate().size());
        LogReport.DateStat first = report.requestsPerDate().get(0);
        assertEquals(LocalDate.of(2024, 3, 1), first.date());
        assertEquals(2L, first.count());
        assertEquals(66.67, first.percentage(), 0.01);
    }

    private LogRecord record(int status, long bodyBytes) {
        return record("/resource", status, bodyBytes);
    }

    private LogRecord record(String resource, int status, long bodyBytes) {
        ZonedDateTime dt = ZonedDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        return new LogRecord("1.2.3.4", "-", dt, "GET", resource, "HTTP/1.1",
            status, bodyBytes, "-", "TestAgent");
    }

    private LogRecord record(ZonedDateTime dt, int status, long bodyBytes) {
        return new LogRecord("1.2.3.4", "-", dt, "GET", "/resource", "HTTP/1.1",
            status, bodyBytes, "-", "TestAgent");
    }
}
