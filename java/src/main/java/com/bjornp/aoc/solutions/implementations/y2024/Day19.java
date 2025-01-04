package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SolutionDay(year = 2024, day = 19)
@Slf4j
public class Day19 extends AdventOfCodeSolution {
    private final Map<String, Long> cache = new HashMap<>();

    public Day19(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("a_regex", this::runSolutionARegex);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var towels = Arrays.stream(input.lines().findFirst().get().split(",")).map(String::trim).toList();
        var patterns = input.lines().skip(2).toList();

        var result = patterns.stream().filter(pattern -> getNPossiblePatterns(pattern, towels) > 0).count();

        return "%d".formatted(result);
    }

    protected String runSolutionARegex(String input) {
        var towels = Arrays.stream(input.lines().findFirst().get().split(",")).map(String::trim).toList();
        var patterns = input.lines().skip(2).toList();

        var regex = "(?:%s)+".formatted(String.join("|", towels));
        var result = patterns.stream().filter(pattern -> pattern.matches(regex)).count();

        return "%d".formatted(result);
    }

    private long getNPossiblePatterns(String pattern, List<String> towels) {
        if (cache.containsKey(pattern)) return cache.get(pattern);
        if (StringUtils.isBlank(pattern)) return 1;

        var result = towels.stream()
                .filter(pattern::startsWith)
                .mapToLong(towel -> getNPossiblePatterns(pattern.substring(towel.length()), towels))
                .sum();

        cache.put(pattern, result);
        return result;
    }

    protected String runSolutionB(String input) {
        var towels = Arrays.stream(input.lines().findFirst().get().split(",")).map(String::trim).toList();
        var patterns = input.lines().skip(2).toList();

        var result = patterns.stream().mapToLong(pattern -> getNPossiblePatterns(pattern, towels)).sum();

        return "%d".formatted(result);
    }
}
