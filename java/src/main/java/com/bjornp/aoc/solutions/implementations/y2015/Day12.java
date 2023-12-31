package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 12)
@Slf4j
public class Day12 extends AdventOfCodeSolution {
    public Day12() {
        super(12, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var pattern = Pattern.compile("(?<num>-?\\d+)");
        var matcher = pattern.matcher(input);

        var matches = new ArrayList<Integer>();

        while (matcher.find()) {
            matches.add(Integer.parseInt(matcher.group()));
        }

        return "%,d".formatted(matches.stream().mapToInt(i -> i).sum());
    }

    public String runSolutionB(String input) {
        var obj = JsonParser.parseString(input);
        return "%,d".formatted(sum(obj));
    }

    public int sum(JsonElement json) {
        if (json.isJsonArray()) {
            var sum = 0;
            for (JsonElement jsonElement : json.getAsJsonArray()) {
                sum += sum(jsonElement);
            }
            return sum;
        } else if (json.isJsonObject()) {
            if (json.getAsJsonObject().entrySet().stream().anyMatch(entry -> entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().getAsString().equals("red"))) {
                return 0;
            } else {
                return json.getAsJsonObject().entrySet().stream().mapToInt(entry -> {
                    if (entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isNumber()) {
                        return entry.getValue().getAsJsonPrimitive().getAsInt();
                    } else if (entry.getValue().isJsonObject() || entry.getValue().isJsonArray()) {
                        return sum(entry.getValue());
                    } else {
                        return 0;
                    }
                }).sum();
            }
        } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsJsonPrimitive().getAsInt();
        }
        return 0;
    }
}
