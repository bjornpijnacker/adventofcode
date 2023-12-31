package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 6)
@Slf4j
public class Day6 extends AdventOfCodeSolution {
    public Day6() {
        super(6, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        boolean[][] lights = new boolean[1000][1000];

        var pattern = Pattern.compile("^(?<command>toggle|turn off|turn on) (?<x1>\\d+),(?<y1>\\d+) through (?<x2>\\d+),(?<y2>\\d+)");
        input.lines().forEach(command -> {
            var matcher = pattern.matcher(command);
            if (matcher.matches()) {
                for (int x = Integer.parseInt(matcher.group("x1")); x <= Integer.parseInt(matcher.group("x2")); x++) {
                    for (int y = Integer.parseInt(matcher.group("y1")); y <= Integer.parseInt(matcher.group("y2")); y++) {
                        switch (matcher.group("command")) {
                            case "toggle" -> lights[x][y] = !lights[x][y];
                            case "turn off" -> lights[x][y] = false;
                            case "turn on" -> lights[x][y] = true;
                        }
                    }
                }
            }
        });

        var sum = 0;
        for (var row : lights) {
            for (var light : row) {
                if (light) {
                    sum++;
                }
            }
        }

        return "%,d".formatted(sum);
    }

    public String runSolutionB(String input) {
        int[][] lights = new int[1000][1000];

        var pattern = Pattern.compile("^(?<command>toggle|turn off|turn on) (?<x1>\\d+),(?<y1>\\d+) through (?<x2>\\d+),(?<y2>\\d+)");
        input.lines().forEach(command -> {
            var matcher = pattern.matcher(command);
            if (matcher.matches()) {
                for (int x = Integer.parseInt(matcher.group("x1")); x <= Integer.parseInt(matcher.group("x2")); x++) {
                    for (int y = Integer.parseInt(matcher.group("y1")); y <= Integer.parseInt(matcher.group("y2")); y++) {
                        switch (matcher.group("command")) {
                            case "toggle" -> lights[x][y] += 2;
                            case "turn off" -> lights[x][y] = Math.max(0, lights[x][y] - 1);
                            case "turn on" -> lights[x][y]++;
                        }
                    }
                }
            }
        });

        var sum = 0;
        for (var row : lights) {
            for (var light : row) {
                sum += light;
            }
        }

        return "%,d".formatted(sum);
    }
}
