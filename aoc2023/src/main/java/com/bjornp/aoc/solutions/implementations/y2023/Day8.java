package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@SolutionDay(year = 2023, day = 8)
public class Day8 extends AdventOfCodeSolution {
    public Day8() {
        super(8, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var lines = input.split("\n");
        var directions = lines[0].split("");

        Map<String, Pair<String, String>> map = new HashMap<>();

        Arrays.stream(lines).skip(2).forEach(node -> {
            map.put(node.substring(0, 3), Pair.of(node.substring(7, 10), node.substring(12, 15)));
        });

        var current = "AAA";
        var i = 0;

        while (!current.equals("ZZZ")) {
            var direction = directions[i % directions.length];
            if (direction.equals("L")) {
                current = map.get(current).getLeft();
            } else if (direction.equals("R")) {
                current = map.get(current).getRight();
            }
            ++i;
        }

        return "%,d".formatted(i);
    }

    @SneakyThrows
    private String runSolutionB(String input) {
        var lines = input.split("\n");
        var directions = lines[0].split("");

        Map<String, Pair<String, String>> map = new HashMap<>();

        Arrays.stream(lines).skip(2).forEach(node -> {
            map.put(node.substring(0, 3), Pair.of(node.substring(7, 10), node.substring(12, 15)));
        });

        var startNodes = map.keySet().stream().filter(key -> key.endsWith("A")).toList();
        var endNodes = map.keySet().stream().filter(key -> key.endsWith("Z")).toList();

        /* The input has cycles, however each cycle starts exactly on the "..A" node and ends exactly on the "..Z" node.
         * Therefore, we can find the length of each cycle and then find the least common multiple of all the cycles as
         * the answer such that each cycle is traversed completely, and they all end at the same time
         */
        var combinedCycleLength = startNodes.stream().mapToLong(node -> {
            var current = node;
            var i = 0;

            while (!endNodes.contains(current)) {
                var direction = directions[i % directions.length];
                if (direction.equals("L")) {
                    current = map.get(current).getLeft();
                } else if (direction.equals("R")) {
                    current = map.get(current).getRight();
                }
                ++i;
            }

            return i;
        }).reduce(1, ArithmeticUtils::lcm);

        return "%,d".formatted(combinedCycleLength);
    }
}
