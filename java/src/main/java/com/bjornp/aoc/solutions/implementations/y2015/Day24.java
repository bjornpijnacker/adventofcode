package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Sets;

@SolutionDay(year = 2015, day = 24)
@Slf4j
public class Day24 extends AdventOfCodeSolution {
    private final ArrayList<Triple<String, String, Integer>> instructions = new ArrayList<>();
    private final Map<String, Integer> registers = new HashMap<>();

    public Day24() {
        super(24, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }



    public String runSolutionA(String input) {
        var weights = input.lines().map(Long::parseLong).collect(Collectors.toSet());
        var groupSize = weights.stream().mapToLong(Long::longValue).sum() / 3;

        var minimalResult = IntStream.range(0, weights.size())
                .mapToObj(i -> {
                    OptionalLong minQE = Sets.combinations(weights, i)
                            .stream().filter(set -> set.stream().mapToLong(Long::longValue).sum() == groupSize)
                            .mapToLong(set -> set.stream().mapToLong(Long::longValue).reduce(1, (a, b) -> a * b))
                            .min();
                    return minQE;
                })
                .filter(l -> l.isPresent())
                .findFirst();

        return "%,d".formatted(minimalResult.get().getAsLong());
    }

    public String runSolutionB(String input) {
        var weights = input.lines().map(Long::parseLong).collect(Collectors.toSet());
        var groupSize = weights.stream().mapToLong(Long::longValue).sum() / 4;

        var minimalResult = IntStream.range(0, weights.size())
                .mapToObj(i -> {
                    OptionalLong minQE = Sets.combinations(weights, i)
                            .stream().filter(set -> set.stream().mapToLong(Long::longValue).sum() == groupSize)
                            .mapToLong(set -> set.stream().mapToLong(Long::longValue).reduce(1, (a, b) -> a * b))
                            .min();
                    return minQE;
                })
                .filter(l -> l.isPresent())
                .findFirst();

        return "%,d".formatted(minimalResult.get().getAsLong());
    }

}
