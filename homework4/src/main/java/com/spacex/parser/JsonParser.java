package com.spacex.parser;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.spacex.model.Launch;

import java.lang.reflect.Type;
import java.util.List;

public class JsonParser {

    private final Gson gson;

    public JsonParser(Gson gson) {
        this.gson = gson;
    }

    public List<Launch> parseList(String json) {
        try {
            Type listType = new TypeToken<List<Launch>>() {}.getType();
            List<Launch> result = gson.fromJson(json, listType);
            if (result == null) {
                throw new IllegalArgumentException("Не удалось разобрать JSON: результат пустой");
            }
            return result;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Некорректный JSON: " + e.getMessage(), e);
        }
    }

    public Launch parseOne(String json) {
        try {
            Launch result = gson.fromJson(json, Launch.class);
            if (result == null) {
                throw new IllegalArgumentException("Не удалось разобрать JSON: результат пустой");
            }
            return result;
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Некорректный JSON: " + e.getMessage(), e);
        }
    }
}
