package com.homework.model;

import java.time.LocalDate;
import java.util.List;

public class LogReport {

    private final List<String> files;
    private final LocalDate from;
    private final LocalDate to;
    private final long totalRequestsCount;
    private final double avgResponseSize;
    private final long maxResponseSize;
    private final double p95ResponseSize;
    private final List<ResourceStat> resources;
    private final List<CodeStat> responseCodes;
    private final List<DateStat> requestsPerDate;

    public LogReport(
        List<String> files,
        LocalDate from,
        LocalDate to,
        long totalRequestsCount,
        double avgResponseSize,
        long maxResponseSize,
        double p95ResponseSize,
        List<ResourceStat> resources,
        List<CodeStat> responseCodes,
        List<DateStat> requestsPerDate
    ) {
        this.files = files;
        this.from = from;
        this.to = to;
        this.totalRequestsCount = totalRequestsCount;
        this.avgResponseSize = avgResponseSize;
        this.maxResponseSize = maxResponseSize;
        this.p95ResponseSize = p95ResponseSize;
        this.resources = resources;
        this.responseCodes = responseCodes;
        this.requestsPerDate = requestsPerDate;
    }

    public List<String> files() { return files; }
    public LocalDate from() { return from; }
    public LocalDate to() { return to; }
    public long totalRequestsCount() { return totalRequestsCount; }
    public double avgResponseSize() { return avgResponseSize; }
    public long maxResponseSize() { return maxResponseSize; }
    public double p95ResponseSize() { return p95ResponseSize; }
    public List<ResourceStat> resources() { return resources; }
    public List<CodeStat> responseCodes() { return responseCodes; }
    public List<DateStat> requestsPerDate() { return requestsPerDate; }

    public record ResourceStat(String resource, long count) {}
    public record CodeStat(int code, long count) {}
    public record DateStat(LocalDate date, String weekday, long count, double percentage) {}
}
