package com.spacex.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class SpaceXHttpClient {

    public String get(String url) throws IOException {
        HttpURLConnection connection = openConnection(url, "GET");
        connection.connect();
        return readResponse(connection);
    }

    public String post(String url, String jsonBody) throws IOException {
        HttpURLConnection connection = openConnection(url, "POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        return readResponse(connection);
    }

    HttpURLConnection openConnection(String url, String method) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        return connection;
    }

    String readResponse(HttpURLConnection connection) throws IOException {
        int statusCode = connection.getResponseCode();
        String body = readBody(connection, statusCode);
        return processResponse(statusCode, body);
    }

    String processResponse(int statusCode, String body) throws IOException {
        if (statusCode == 200) {
            return body;
        }
        throw new IOException("HTTP " + statusCode + ": " + body);
    }

    private String readBody(HttpURLConnection connection, int statusCode) throws IOException {
        var stream = statusCode >= 400
                ? connection.getErrorStream()
                : connection.getInputStream();

        if (stream == null) {
            return "";
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public String buildUrl(String baseUrl, String path) {
        if (baseUrl.endsWith("/") && path.startsWith("/")) {
            return baseUrl + path.substring(1);
        }
        if (!baseUrl.endsWith("/") && !path.startsWith("/")) {
            return baseUrl + "/" + path;
        }
        return baseUrl + path;
    }
}
