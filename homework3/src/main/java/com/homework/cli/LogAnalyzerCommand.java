package com.homework.cli;

import com.homework.analyzer.LogAnalyzer;
import com.homework.formatter.JsonFormatter;
import com.homework.formatter.MarkdownFormatter;
import com.homework.formatter.ReportFormatter;
import com.homework.model.LogRecord;
import com.homework.model.LogReport;
import com.homework.parser.NginxLogParser;
import com.homework.reader.LogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

@Command(
    name = "log-analyzer",
    mixinStandardHelpOptions = true,
    description = "Analyzes NGINX access log files and produces statistics reports."
)
public class LogAnalyzerCommand implements Callable<Integer> {

    private static final Logger log = LoggerFactory.getLogger(LogAnalyzerCommand.class);

    private static final Set<String> SUPPORTED_FORMATS = Set.of("json", "markdown");

    @Option(
        names = {"--path", "-p"},
        required = true,
        description = "Path(s) to NGINX log files (.log or .txt). Glob patterns are supported."
    )
    private String[] paths;

    @Option(
        names = {"--format", "-f"},
        required = true,
        description = "Output format: json | markdown"
    )
    private String format;

    @Option(
        names = {"--output", "-o"},
        required = true,
        description = "Path to the output file"
    )
    private String output;

    @Option(
        names = {"--from"},
        description = "Start date filter (ISO-8601, e.g. 2025-03-01)"
    )
    private LocalDate from;

    @Option(
        names = {"--to"},
        description = "End date filter (ISO-8601, e.g. 2025-03-31)"
    )
    private LocalDate to;

    @Override
    public Integer call() {
        try {
            validateParams();
            run();
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Usage error: " + e.getMessage());
            log.error("Usage error: {}", e.getMessage());
            return 2;
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            return 1;
        }
    }

    private void validateParams() {
        String fmt = format.trim().toLowerCase();
        if (!SUPPORTED_FORMATS.contains(fmt)) {
            throw new IllegalArgumentException(
                "Unsupported format: '" + format + "'. Supported formats: json, markdown");
        }
        format = fmt;

        if (from != null && to != null && !from.isBefore(to)) {
            throw new IllegalArgumentException(
                "--from date must be strictly before --to date");
        }

        validateOutputPath();
    }

    private void validateOutputPath() {
        Path outputPath = Path.of(output);

        if (Files.exists(outputPath)) {
            throw new IllegalArgumentException(
                "Output file already exists: " + output);
        }

        Path parent = outputPath.getParent();
        if (parent != null && !Files.isWritable(parent)) {
            throw new IllegalArgumentException(
                "Output directory is not writable: " + parent);
        }

        String expectedExt = switch (format) {
            case "json" -> ".json";
            case "markdown" -> ".md";
            default -> null;
        };

        if (expectedExt != null && !output.toLowerCase().endsWith(expectedExt)) {
            throw new IllegalArgumentException(
                "Output file must have extension '" + expectedExt + "' for format '" + format + "'");
        }
    }

    private void run() throws IOException {
        log.info("Starting log analysis. Paths: {}, Format: {}, Output: {}", paths, format, output);

        LogReader reader = new LogReader();
        NginxLogParser parser = new NginxLogParser();
        LogAnalyzer analyzer = new LogAnalyzer();

        List<String> filesList = Arrays.asList(paths);

        try (Stream<String> lines = reader.readLines(paths)) {
            Stream<LogRecord> records = lines.flatMap(line -> parser.parse(line).stream());
            LogReport report = analyzer.analyze(records, from, to, filesList);

            ReportFormatter formatter = switch (format) {
                case "json" -> new JsonFormatter();
                case "markdown" -> new MarkdownFormatter();
                default -> throw new IllegalStateException("Unreachable");
            };

            String result = formatter.format(report);
            Files.writeString(Path.of(output), result);
            log.info("Report written to {}", output);
            log.info("Total records analyzed: {}", report.totalRequestsCount());
        }
    }
}
