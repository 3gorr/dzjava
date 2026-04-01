package com.spacex;

import com.google.gson.Gson;
import com.spacex.builder.JsonBuilder;
import com.spacex.client.SpaceXHttpClient;
import com.spacex.menu.ConsoleMenu;
import com.spacex.parser.JsonParser;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();

        SpaceXHttpClient httpClient = new SpaceXHttpClient();
        JsonParser jsonParser = new JsonParser(gson);
        JsonBuilder jsonBuilder = new JsonBuilder(gson);
        Scanner scanner = new Scanner(System.in);

        ConsoleMenu menu = new ConsoleMenu(httpClient, jsonParser, jsonBuilder, scanner);
        menu.run();
    }
}
