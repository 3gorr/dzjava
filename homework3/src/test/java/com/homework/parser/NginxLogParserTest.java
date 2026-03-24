package com.homework.parser;

import com.homework.model.LogRecord;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NginxLogParserTest {

    private final NginxLogParser parser = new NginxLogParser();

    @Test
    void parsesValidLine() {
        String line = "93.180.71.3 - - [17/May/2015:08:05:32 +0000] "
            + "\"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" "
            + "\"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)\"";

        Optional<LogRecord> result = parser.parse(line);

        assertTrue(result.isPresent());
        LogRecord r = result.get();
        assertEquals("93.180.71.3", r.remoteAddr());
        assertEquals("-", r.remoteUser());
        assertEquals("GET", r.method());
        assertEquals("/downloads/product_1", r.resource());
        assertEquals("HTTP/1.1", r.protocol());
        assertEquals(304, r.status());
        assertEquals(0L, r.bodyBytesSent());
        assertEquals("-", r.httpReferer());
        assertEquals("Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.21)", r.httpUserAgent());
    }

    @Test
    void parsesLineWithNonZeroBodySize() {
        String line = "192.168.1.1 - user [01/Jan/2024:12:00:00 +0000] "
            + "\"POST /api/data HTTP/1.1\" 200 1234 "
            + "\"https://example.com\" \"Mozilla/5.0\"";

        Optional<LogRecord> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals(200, result.get().status());
        assertEquals(1234L, result.get().bodyBytesSent());
        assertEquals("user", result.get().remoteUser());
        assertEquals("POST", result.get().method());
        assertEquals("/api/data", result.get().resource());
    }

    @Test
    void returnsEmptyForMalformedLine() {
        Optional<LogRecord> result = parser.parse("this is not a valid nginx log line");
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyForNullLine() {
        Optional<LogRecord> result = parser.parse(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyForBlankLine() {
        Optional<LogRecord> result = parser.parse("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void returnsEmptyForEmptyLine() {
        Optional<LogRecord> result = parser.parse("");
        assertTrue(result.isEmpty());
    }

    @Test
    void parsesLineWithDashAsRemoteUser() {
        String line = "10.0.0.1 - - [15/Mar/2025:10:30:00 +0000] "
            + "\"GET /index.html HTTP/1.1\" 200 512 \"-\" \"curl/7.68.0\"";

        Optional<LogRecord> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals("-", result.get().remoteUser());
        assertEquals("/index.html", result.get().resource());
    }

    @Test
    void parsesDateCorrectly() {
        String line = "1.2.3.4 - - [17/May/2015:08:05:32 +0000] "
            + "\"GET /path HTTP/1.1\" 200 100 \"-\" \"Agent\"";

        Optional<LogRecord> result = parser.parse(line);

        assertTrue(result.isPresent());
        var dt = result.get().timeLocal();
        assertEquals(2015, dt.getYear());
        assertEquals(5, dt.getMonthValue());
        assertEquals(17, dt.getDayOfMonth());
        assertEquals(8, dt.getHour());
        assertEquals(5, dt.getMinute());
        assertEquals(32, dt.getSecond());
    }

    @Test
    void parsesRequestWithSpacesInPath() {
        String line = "1.2.3.4 - - [01/Jan/2024:00:00:00 +0000] "
            + "\"GET /path/to/resource HTTP/2.0\" 200 0 \"-\" \"Agent\"";

        Optional<LogRecord> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals("/path/to/resource", result.get().resource());
        assertEquals("HTTP/2.0", result.get().protocol());
    }
}
