package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.Solution;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Solution(day = 1)
@Slf4j
public class Day1 extends AdventOfCodeSolution {

    public Day1() {
        super(1);
    }

    @Override
    protected String runSolutionA(String input) {
        var value = Arrays.stream(input.split("\n")).map(line -> {
            var numchars = Arrays.stream(line.split("")).filter(ch -> ch.matches("[0-9]+")).toList();
            var first = numchars.get(0);
            var last = numchars.get(numchars.size() - 1);
            return "%s%s".formatted(first, last);
        }).mapToInt(Integer::parseInt).sum();
        return String.valueOf(value);
    }

    @Override
    protected String runSolutionB(String input) {
        var value = Arrays.stream(input.split("\n")).map(line -> {
            AtomicInteger begin = new AtomicInteger(Integer.MAX_VALUE);
            AtomicInteger end = new AtomicInteger(-1);
            AtomicReference<String> beginValue = new AtomicReference<>("");
            AtomicReference<String> endValue = new AtomicReference<>("");

            List.of(
                    "one",
                    "two",
                    "three",
                    "four",
                    "five",
                    "six",
                    "seven",
                    "eight",
                    "nine",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9"
            ).forEach(candidate -> {
                var beginIndex = line.indexOf(candidate);
                if (beginIndex != -1 && beginIndex < begin.get()) {
                    begin.set(beginIndex);
                    beginValue.set(candidate);
                }

                var endIndex = line.lastIndexOf(candidate);
                if (endIndex != -1 && endIndex > end.get()) {
                    end.set(endIndex);
                    endValue.set(candidate);
                }
            });

            if (!beginValue.get().matches("[0-9]")) {
                beginValue.set(parseWordNum(beginValue.get()));
            }

            if (!endValue.get().matches("[0-9]")) {
                endValue.set(parseWordNum(endValue.get()));
            }

            return "%s%s".formatted(beginValue.get(), endValue.get());
        }).mapToInt(Integer::parseInt).sum();

        return String.valueOf(value);
    }

    private String parseWordNum(String numString) {
        return switch (numString) {
            case "one" -> "1";
            case "two" -> "2";
            case "three" -> "3";
            case "four" -> "4";
            case "five" -> "5";
            case "six" -> "6";
            case "seven" -> "7";
            case "eight" -> "8";
            case "nine" -> "9";
            default -> throw new IllegalArgumentException(numString);
        };
    }
}
