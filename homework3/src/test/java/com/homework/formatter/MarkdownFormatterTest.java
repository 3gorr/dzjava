package com.homework.formatter;

import com.homework.model.LogReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarkdownFormatterTest {

    private final MarkdownFormatter formatter = new MarkdownFormatter();

    @Test
    void containsGeneralInfoHeader() {
        String result = formatter.format(buildReport());
        assertTrue(result.contains("#### Общая информация"));
    }

    @Test
    void containsResourcesHeader() {
        String result = formatter.format(buildReport());
        assertTrue(result.contains("#### Запрашиваемые ресурсы"));
    }

    @Test
    void containsResponseCodesHeader() {
        String result = formatter.format(buildReport());
        assertTrue(result.contains("#### Коды ответа"));
    }

    @Test
    void showsDashWhenFromDateIsNull() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            0L, 0.0, 0L, 0.0, List.of(), List.of(), List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("Начальная дата"));
        assertTrue(result.contains("| - |") || result.contains("| - |\n"));
    }

    @Test
    void showsFormattedFromDate() {
        LogReport report = new LogReport(
            List.of("test.log"),
            LocalDate.of(2024, 8, 31),
            null,
            0L, 0.0, 0L, 0.0, List.of(), List.of(), List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("31.08.2024"));
    }

    @Test
    void formatsLargeNumbersWithUnderscore() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            10000L, 0.0, 0L, 0.0, List.of(), List.of(), List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("10_000"));
    }

    @Test
    void formatsSmallNumbersWithoutUnderscore() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            500L, 0.0, 0L, 0.0, List.of(), List.of(), List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("500"));
        assertFalse(result.contains("_500"));
    }

    @Test
    void showsResponseSizeWithBSuffix() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            1L, 512.0, 1024L, 950.0, List.of(), List.of(), List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("512b"));
        assertTrue(result.contains("950b"));
    }

    @Test
    void showsResourcesInReport() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            100L, 0.0, 0L, 0.0,
            List.of(
                new LogReport.ResourceStat("/index.html", 60L),
                new LogReport.ResourceStat("/about.html", 40L)
            ),
            List.of(),
            List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("/index.html"));
        assertTrue(result.contains("/about.html"));
    }

    @Test
    void showsStatusCodeNameInReport() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            100L, 0.0, 0L, 0.0,
            List.of(),
            List.of(
                new LogReport.CodeStat(200, 80L),
                new LogReport.CodeStat(404, 20L)
            ),
            List.of()
        );
        String result = formatter.format(report);
        assertTrue(result.contains("200"));
        assertTrue(result.contains("OK"));
        assertTrue(result.contains("404"));
        assertTrue(result.contains("Not Found"));
    }

    @Test
    void showsDateDistributionWhenPresent() {
        LogReport report = new LogReport(
            List.of("test.log"), null, null,
            100L, 0.0, 0L, 0.0,
            List.of(), List.of(),
            List.of(new LogReport.DateStat(
                LocalDate.of(2024, 3, 1), "Friday", 50L, 50.0
            ))
        );
        String result = formatter.format(report);
        assertTrue(result.contains("#### Распределение запросов по датам"));
        assertTrue(result.contains("01.03.2024"));
        assertTrue(result.contains("Friday"));
    }

    @Test
    void doesNotShowDateDistributionHeaderWhenEmpty() {
        String result = formatter.format(buildReport());
        assertFalse(result.contains("Распределение запросов по датам"));
    }

    private LogReport buildReport() {
        return new LogReport(
            List.of("access.log"),
            null,
            null,
            1000L,
            500.0,
            1000L,
            950.0,
            List.of(new LogReport.ResourceStat("/index.html", 500L)),
            List.of(new LogReport.CodeStat(200, 1000L)),
            List.of()
        );
    }
}
