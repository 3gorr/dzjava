package com.homework.parser;

import com.homework.model.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NginxLogParser {

    private static final Logger log = LoggerFactory.getLogger(NginxLogParser.class);

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+) - (\\S+) \\[([^\\]]+)] \"([^\"]*)\" (\\d+) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$"
    );

    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public Optional<LogRecord> parse(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }

        Matcher m = LOG_PATTERN.matcher(line);
        if (!m.matches()) {
            log.warn("Failed to parse log line: {}", line);
            return Optional.empty();
        }

        try {
            String remoteAddr = m.group(1);
            String remoteUser = m.group(2);
            ZonedDateTime timeLocal = ZonedDateTime.parse(m.group(3), DATE_FORMATTER);
            String rawRequest = m.group(4);
            int status = Integer.parseInt(m.group(5));
            long bodyBytesSent = Long.parseLong(m.group(6));
            String httpReferer = m.group(7);
            String httpUserAgent = m.group(8);

            String method = "";
            String resource = rawRequest;
            String protocol = "";

            String[] requestParts = rawRequest.split(" ", 3);
            if (requestParts.length >= 2) {
                method = requestParts[0];
                resource = requestParts[1];
            }
            if (requestParts.length >= 3) {
                protocol = requestParts[2];
            }

            return Optional.of(new LogRecord(
                remoteAddr, remoteUser, timeLocal,
                method, resource, protocol,
                status, bodyBytesSent, httpReferer, httpUserAgent
            ));
        } catch (Exception e) {
            log.warn("Error parsing log line: {} — {}", line, e.getMessage());
            return Optional.empty();
        }
    }
}
