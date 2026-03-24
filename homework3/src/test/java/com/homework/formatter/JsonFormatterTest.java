package com.homework.formatter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.model.LogReport;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonFormatterTest {

    private final JsonFormatter formatter = new JsonFormatter();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void producesValidJson() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);

        assertDoesNotThrow(() -> mapper.readTree(json));
    }

    @Test
    void containsRequiredTopLevelFields() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        assertTrue(root.has("files"));
        assertTrue(root.has("totalRequestsCount"));
        assertTrue(root.has("responseSizeInBytes"));
        assertTrue(root.has("resources"));
        assertTrue(root.has("responseCodes"));
    }

    @Test
    void hasCorrectFilesArray() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        JsonNode files = root.get("files");
        assertTrue(files.isArray());
        assertEquals(1, files.size());
        assertEquals("access.log", files.get(0).asText());
    }

    @Test
    void hasCorrectTotalRequestsCount() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        assertEquals(1000, root.get("totalRequestsCount").asLong());
    }

    @Test
    void hasCorrectResponseSizeFields() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        JsonNode rsize = root.get("responseSizeInBytes");
        assertTrue(rsize.has("average"));
        assertTrue(rsize.has("max"));
        assertTrue(rsize.has("p95"));
        assertEquals(500.0, rsize.get("average").asDouble(), 0.01);
        assertEquals(1000.0, rsize.get("max").asDouble(), 0.01);
        assertEquals(950.0, rsize.get("p95").asDouble(), 0.01);
    }

    @Test
    void hasCorrectResourcesArray() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        JsonNode resources = root.get("resources");
        assertTrue(resources.isArray());
        assertEquals(1, resources.size());
        assertEquals("/downloads/product_1", resources.get(0).get("resource").asText());
        assertEquals(1000, resources.get(0).get("totalRequestsCount").asLong());
    }

    @Test
    void hasCorrectResponseCodesArray() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        JsonNode codes = root.get("responseCodes");
        assertTrue(codes.isArray());
        assertEquals(1, codes.size());
        assertEquals(200, codes.get(0).get("code").asInt());
        assertEquals(1000, codes.get(0).get("totalResponsesCount").asLong());
    }

    @Test
    void includesRequestsPerDateWhenPresent() throws Exception {
        LogReport report = buildReportWithDates();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        assertTrue(root.has("requestsPerDate"));
        JsonNode dates = root.get("requestsPerDate");
        assertTrue(dates.isArray());
        assertEquals(1, dates.size());

        JsonNode entry = dates.get(0);
        assertTrue(entry.has("date"));
        assertTrue(entry.has("weekday"));
        assertTrue(entry.has("totalRequestsCount"));
        assertTrue(entry.has("totalRequestsPercentage"));
    }

    @Test
    void omitsRequestsPerDateWhenEmpty() throws Exception {
        LogReport report = buildReport();
        String json = formatter.format(report);
        JsonNode root = mapper.readTree(json);

        assertFalse(root.has("requestsPerDate") && root.get("requestsPerDate") != null
            && !root.get("requestsPerDate").isNull());
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
            List.of(new LogReport.ResourceStat("/downloads/product_1", 1000L)),
            List.of(new LogReport.CodeStat(200, 1000L)),
            List.of()
        );
    }

    private LogReport buildReportWithDates() {
        return new LogReport(
            List.of("access.log"),
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 3, 31),
            100L,
            500.0,
            1000L,
            950.0,
            List.of(),
            List.of(),
            List.of(new LogReport.DateStat(
                LocalDate.of(2024, 3, 1), "Friday", 100L, 100.0
            ))
        );
    }
}
