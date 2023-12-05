package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SolutionDay(day = 2)
@Slf4j
public class Day2 extends AdventOfCodeSolution {
    public Day2() {
        super(2);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var green = ColorMax.builder().pattern(Pattern.compile("([0-9]+) green")).max(13).build();
        var red = ColorMax.builder().pattern(Pattern.compile("([0-9]+) red")).max(12).build();
        var blue =  ColorMax.builder().pattern(Pattern.compile("([0-9]+) blue")).max(14).build();

        return String.valueOf(Arrays.stream(input.split("\n")).mapToInt(line -> {
            var splitLine = line.split(":");
            var gameId = Integer.parseInt(splitLine[0].substring(5));

            var possible = Arrays.stream(splitLine[1].split(";")).allMatch(singleGrab -> {
                return Arrays.stream(singleGrab.split(",")).allMatch(singleCube -> {
                    return Stream.of(green, red, blue).allMatch(colorMax -> colorMax.test(singleCube));
                });
            });

            return possible ? gameId : 0;
        }).sum());
    }

    protected String runSolutionB(String input) {
        var greenRegex = Pattern.compile("([0-9]+) green");
        var redRegex = Pattern.compile("([0-9]+) red");
        var blueRegex = Pattern.compile("([0-9]+) blue");

        return String.valueOf(Arrays.stream(input.split("\n")).mapToInt(line -> {
            var splitLine = line.split(":");

            AtomicInteger product = new AtomicInteger(1);

            Stream.of(greenRegex, redRegex, blueRegex).mapToInt(regex -> {
                return Arrays.stream(splitLine[1].split(";")).flatMapToInt(singleGrab -> {
                    return Arrays.stream(singleGrab.split(",")).mapToInt(singleCube -> {
                        var num = regex.matcher(singleCube.trim());
                        if (num.matches()) {
                            return Integer.parseInt(num.group(1));
                        }
                        return 0;
                    });
                }).max().orElse(1);
            }).forEach(value -> product.updateAndGet(v -> v * value));

            return product.get();
        }).sum());
    }

    @Builder
    @Data
    private static class ColorMax {
        private int max;

        private Pattern pattern;

        public boolean test(String string) {
            var matcher = pattern.matcher(string.trim());
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1)) <= max;
            }
            return true;
        }
    }
}
