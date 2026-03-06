package com.homework.formatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.homework.model.LogReport;

import java.util.List;

public class JsonFormatter implements ReportFormatter {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public String format(LogReport report) {
        try {
            Output output = buildOutput(report);
            return MAPPER.writeValueAsString(output);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize report to JSON", e);
        }
    }

    private Output buildOutput(LogReport report) {
        Output out = new Output();
        out.files = report.files();
        out.totalRequestsCount = report.totalRequestsCount();
        out.responseSizeInBytes = new ResponseSize(
            report.avgResponseSize(),
            report.maxResponseSize(),
            report.p95ResponseSize()
        );
        out.resources = report.resources().stream()
            .map(r -> new ResourceDto(r.resource(), r.count()))
            .toList();
        out.responseCodes = report.responseCodes().stream()
            .map(c -> new CodeDto(c.code(), c.count()))
            .toList();
        if (!report.requestsPerDate().isEmpty()) {
            out.requestsPerDate = report.requestsPerDate().stream()
                .map(d -> new DateDto(
                    d.date().toString(),
                    d.weekday(),
                    d.count(),
                    d.percentage()
                ))
                .toList();
        }
        return out;
    }

    static class Output {
        @JsonProperty("files")
        public List<String> files;

        @JsonProperty("totalRequestsCount")
        public long totalRequestsCount;

        @JsonProperty("responseSizeInBytes")
        public ResponseSize responseSizeInBytes;

        @JsonProperty("resources")
        public List<ResourceDto> resources;

        @JsonProperty("responseCodes")
        public List<CodeDto> responseCodes;

        @JsonProperty("requestsPerDate")
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public List<DateDto> requestsPerDate;
    }

    record ResponseSize(
        @JsonProperty("average") double average,
        @JsonProperty("max") double max,
        @JsonProperty("p95") double p95
    ) {}

    record ResourceDto(
        @JsonProperty("resource") String resource,
        @JsonProperty("totalRequestsCount") long totalRequestsCount
    ) {}

    record CodeDto(
        @JsonProperty("code") int code,
        @JsonProperty("totalResponsesCount") long totalResponsesCount
    ) {}

    record DateDto(
        @JsonProperty("date") String date,
        @JsonProperty("weekday") String weekday,
        @JsonProperty("totalRequestsCount") long totalRequestsCount,
        @JsonProperty("totalRequestsPercentage") double totalRequestsPercentage
    ) {}
}
