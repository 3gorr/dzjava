package com.spacex.menu;

import com.spacex.builder.JsonBuilder;
import com.spacex.client.SpaceXHttpClient;
import com.spacex.model.Core;
import com.spacex.model.Failure;
import com.spacex.model.Launch;
import com.spacex.parser.JsonParser;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    private static final String BASE_URL = "https://api.spacexdata.com";

    private final SpaceXHttpClient httpClient;
    private final JsonParser jsonParser;
    private final JsonBuilder jsonBuilder;
    private final Scanner scanner;

    public ConsoleMenu(SpaceXHttpClient httpClient, JsonParser jsonParser, JsonBuilder jsonBuilder, Scanner scanner) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("=== SpaceX Launch Explorer ===");

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> showAllLaunches();
                case "2" -> showLatestLaunch();
                case "3" -> searchByDate();
                case "4" -> showBySuccess(true);
                case "5" -> showBySuccess(false);
                case "6" -> {
                    System.out.println("Поки чмоки");
                    return;
                }
                default -> System.out.println("Неверный пункт меню. Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Показать все запуски");
        System.out.println("2. Показать последний запуск");
        System.out.println("3. Поиск запусков по дате");
        System.out.println("4. Показать только успешные запуски");
        System.out.println("5. Показать только неудачные запуски");
        System.out.println("6. Выход");
        System.out.print("Выберите пункт: ");
    }

    private void showAllLaunches() {
        try {
            String json = httpClient.get(httpClient.buildUrl(BASE_URL, "/v5/launches"));
            List<Launch> launches = jsonParser.parseList(json);
            System.out.println();
            printLaunchList(launches);
        } catch (IOException e) {
            System.out.println("Ошибка сети: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка данных: " + e.getMessage());
        }
    }

    private void showLatestLaunch() {
        try {
            String json = httpClient.get(httpClient.buildUrl(BASE_URL, "/v5/launches/latest"));
            Launch launch = jsonParser.parseOne(json);
            System.out.println();
            printLaunchDetails(launch);
        } catch (IOException e) {
            System.out.println("Ошибка сети: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка данных: " + e.getMessage());
        }
    }

    private void searchByDate() {
        System.out.print("Введите дату начала (YYYY-MM-DD): ");
        String dateFrom = scanner.nextLine().trim();
        System.out.print("Введите дату конца (YYYY-MM-DD): ");
        String dateTo = scanner.nextLine().trim();

        try {
            String body = jsonBuilder.buildDateFilter(dateFrom, dateTo);
            String json = httpClient.post(httpClient.buildUrl(BASE_URL, "/v5/launches/query"), body);
            List<Launch> launches = parseQueryResponse(json);
            System.out.println();
            if (launches.isEmpty()) {
                System.out.println("Запуски за указанный период не найдены.");
            } else {
                printLaunchList(launches);
            }
        } catch (IOException e) {
            System.out.println("Ошибка сети: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка данных: " + e.getMessage());
        }
    }

    private void showBySuccess(boolean success) {
        try {
            String body = jsonBuilder.buildSuccessFilter(success);
            String json = httpClient.post(httpClient.buildUrl(BASE_URL, "/v5/launches/query"), body);
            List<Launch> launches = parseQueryResponse(json);
            System.out.println();
            if (launches.isEmpty()) {
                System.out.println("Запуски не найдены.");
            } else {
                printLaunchList(launches);
            }
        } catch (IOException e) {
            System.out.println("Ошибка сети: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка данных: " + e.getMessage());
        }
    }

    private List<Launch> parseQueryResponse(String json) {
        int docsStart = json.indexOf("\"docs\"");
        if (docsStart == -1) {
            throw new IllegalArgumentException("Неожиданный формат ответа: поле 'docs' не найдено");
        }
        int arrayStart = json.indexOf('[', docsStart);
        if (arrayStart == -1) {
            throw new IllegalArgumentException("Неожиданный формат ответа: массив 'docs' не найден");
        }
        int depth = 0;
        int arrayEnd = arrayStart;
        for (int i = arrayStart; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) {
                    arrayEnd = i;
                    break;
                }
            }
        }
        String docsJson = json.substring(arrayStart, arrayEnd + 1);
        return jsonParser.parseList(docsJson);
    }

    private void printLaunchList(List<Launch> launches) {
        for (Launch launch : launches) {
            String date = launch.getDateUtc() != null
                    ? launch.getDateUtc().substring(0, 10)
                    : "неизвестно";
            String successStr = formatSuccess(launch.getSuccess());
            System.out.printf("#%-4d %-30s | %s | Успех: %s%n",
                    launch.getFlightNumber(), launch.getName(), date, successStr);
        }
        System.out.println("Всего: " + launches.size());
    }

    private void printLaunchDetails(Launch launch) {
        System.out.println("Запуск:   " + launch.getName());
        System.out.println("Номер:    " + launch.getFlightNumber());
        System.out.println("Дата:     " + (launch.getDateUtc() != null ? launch.getDateUtc() : "неизвестно"));
        System.out.println("Успех:    " + formatSuccess(launch.getSuccess()));
        System.out.println("Описание: " + (launch.getDetails() != null ? launch.getDetails() : "нет описания"));

        if (launch.getCores() != null && !launch.getCores().isEmpty()) {
            System.out.println("Ступени:");
            for (Core core : launch.getCores()) {
                System.out.println("  - " + core);
            }
        }

        if (launch.getFailures() != null && !launch.getFailures().isEmpty()) {
            System.out.println("Отказы:");
            for (Failure failure : launch.getFailures()) {
                System.out.println("  - " + failure);
            }
        }
    }

    private String formatSuccess(Boolean success) {
        if (success == null) return "неизвестно";
        return success ? "да" : "нет";
    }
}
