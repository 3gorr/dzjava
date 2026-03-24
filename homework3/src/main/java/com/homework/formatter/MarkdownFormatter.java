package com.homework.formatter;

import com.homework.model.LogReport;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class MarkdownFormatter implements ReportFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private static final Map<Integer, String> HTTP_STATUS_NAMES = Map.ofEntries(
        Map.entry(100, "Continue"),
        Map.entry(200, "OK"),
        Map.entry(201, "Created"),
        Map.entry(204, "No Content"),
        Map.entry(206, "Partial Content"),
        Map.entry(301, "Moved Permanently"),
        Map.entry(302, "Found"),
        Map.entry(304, "Not Modified"),
        Map.entry(400, "Bad Request"),
        Map.entry(401, "Unauthorized"),
        Map.entry(403, "Forbidden"),
        Map.entry(404, "Not Found"),
        Map.entry(405, "Method Not Allowed"),
        Map.entry(408, "Request Timeout"),
        Map.entry(410, "Gone"),
        Map.entry(429, "Too Many Requests"),
        Map.entry(500, "Internal Server Error"),
        Map.entry(502, "Bad Gateway"),
        Map.entry(503, "Service Unavailable"),
        Map.entry(504, "Gateway Timeout")
    );

    @Override
    public String format(LogReport report) {
        StringBuilder sb = new StringBuilder();
        appendGeneralInfo(sb, report);
        appendResources(sb, report);
        appendResponseCodes(sb, report);
        if (!report.requestsPerDate().isEmpty()) {
            appendRequestsPerDate(sb, report);
        }
        return sb.toString();
    }

    private void appendGeneralInfo(StringBuilder sb, LogReport report) {
        sb.append("#### Общая информация\n");
        sb.append("| Метрика | Значение |\n");
        sb.append("|:-------:|---------:|\n");

        String filesStr = String.join(", ", report.files());
        sb.append("| Файл(-ы) | ").append(filesStr).append(" |\n");
        sb.append("| Начальная дата | ")
            .append(report.from() != null ? report.from().format(DATE_FMT) : "-")
            .append(" |\n");
        sb.append("| Конечная дата | ")
            .append(report.to() != null ? report.to().format(DATE_FMT) : "-")
            .append(" |\n");
        sb.append("| Количество запросов | ")
            .append(formatNumber(report.totalRequestsCount()))
            .append(" |\n");
        sb.append("| Средний размер ответа | ")
            .append(formatBytes(report.avgResponseSize()))
            .append(" |\n");
        sb.append("| Максимальный размер ответа | ")
            .append(formatBytes(report.maxResponseSize()))
            .append(" |\n");
        sb.append("| 95p размера ответа | ")
            .append(formatBytes(report.p95ResponseSize()))
            .append(" |\n");
        sb.append("\n");
    }

    private void appendResources(StringBuilder sb, LogReport report) {
        sb.append("#### Запрашиваемые ресурсы\n");
        sb.append("| Ресурс | Количество |\n");
        sb.append("|:------:|-----------:|\n");
        for (LogReport.ResourceStat r : report.resources()) {
            sb.append("| ").append(r.resource())
                .append(" | ").append(formatNumber(r.count()))
                .append(" |\n");
        }
        sb.append("\n");
    }

    private void appendResponseCodes(StringBuilder sb, LogReport report) {
        sb.append("#### Коды ответа\n");
        sb.append("| Код | Имя | Количество |\n");
        sb.append("|:---:|:---:|-----------:|\n");
        for (LogReport.CodeStat c : report.responseCodes()) {
            String name = HTTP_STATUS_NAMES.getOrDefault(c.code(), "Unknown");
            sb.append("| ").append(c.code())
                .append(" | ").append(name)
                .append(" | ").append(formatNumber(c.count()))
                .append(" |\n");
        }
        sb.append("\n");
    }

    private void appendRequestsPerDate(StringBuilder sb, LogReport report) {
        sb.append("#### Распределение запросов по датам\n");
        sb.append("| Дата | День недели | Количество | % от общего |\n");
        sb.append("|:----:|:-----------:|-----------:|------------:|\n");
        for (LogReport.DateStat d : report.requestsPerDate()) {
            sb.append("| ").append(d.date().format(DATE_FMT))
                .append(" | ").append(d.weekday())
                .append(" | ").append(formatNumber(d.count()))
                .append(" | ").append(String.format(java.util.Locale.US, "%.2f", d.percentage())).append("% |\n");
        }
        sb.append("\n");
    }

    private String formatNumber(long n) {
        if (n < 1000) {
            return String.valueOf(n);
        }
        String s = String.valueOf(n);
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            if (i > 0 && (len - i) % 3 == 0) {
                sb.append('_');
            }
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    private String formatBytes(double bytes) {
        long rounded = Math.round(bytes);
        return formatNumber(rounded) + "b";
    }
}
