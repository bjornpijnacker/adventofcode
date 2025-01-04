package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Direction2D;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SolutionDay(year = 2024, day = 21)
@Slf4j
public class Day21 extends AdventOfCodeSolution {
    private final Map<Pair<String, Direction2D>, String> keypad = new HashMap<>();

    private final Map<Pair<String, Direction2D>, String> dpad = new HashMap<>();

    private final Map<Pair<String, String>, Long> distance = new HashMap<>();

    private final Map<Pair<List<List<String>>, Integer>, Long> cache = new HashMap<>();


    public Day21(int day, int year) {
        super(day, year);

        register("a", input -> runSolution(input, 2));
        register("b", input -> runSolution(input, 25));
        register("test", this::test);

        keypad.put(Pair.of("0", Direction2D.EAST), "A");
        keypad.put(Pair.of("0", Direction2D.NORTH), "2");
        keypad.put(Pair.of("A", Direction2D.WEST), "0");
        keypad.put(Pair.of("A", Direction2D.NORTH), "3");
        keypad.put(Pair.of("1", Direction2D.NORTH), "4");
        keypad.put(Pair.of("1", Direction2D.EAST), "2");
        keypad.put(Pair.of("2", Direction2D.NORTH), "5");
        keypad.put(Pair.of("2", Direction2D.EAST), "3");
        keypad.put(Pair.of("2", Direction2D.SOUTH), "0");
        keypad.put(Pair.of("2", Direction2D.WEST), "1");
        keypad.put(Pair.of("3", Direction2D.NORTH), "6");
        keypad.put(Pair.of("3", Direction2D.WEST), "2");
        keypad.put(Pair.of("3", Direction2D.SOUTH), "A");
        keypad.put(Pair.of("4", Direction2D.NORTH), "7");
        keypad.put(Pair.of("4", Direction2D.EAST), "5");
        keypad.put(Pair.of("4", Direction2D.SOUTH), "1");
        keypad.put(Pair.of("5", Direction2D.NORTH), "8");
        keypad.put(Pair.of("5", Direction2D.EAST), "6");
        keypad.put(Pair.of("5", Direction2D.SOUTH), "2");
        keypad.put(Pair.of("5", Direction2D.WEST), "4");
        keypad.put(Pair.of("6", Direction2D.NORTH), "9");
        keypad.put(Pair.of("6", Direction2D.SOUTH), "3");
        keypad.put(Pair.of("6", Direction2D.WEST), "5");
        keypad.put(Pair.of("7", Direction2D.EAST), "8");
        keypad.put(Pair.of("7", Direction2D.SOUTH), "4");
        keypad.put(Pair.of("8", Direction2D.EAST), "9");
        keypad.put(Pair.of("8", Direction2D.SOUTH), "5");
        keypad.put(Pair.of("8", Direction2D.WEST), "7");
        keypad.put(Pair.of("9", Direction2D.SOUTH), "6");
        keypad.put(Pair.of("9", Direction2D.WEST), "8");

        dpad.put(Pair.of("<", Direction2D.EAST), "v");
        dpad.put(Pair.of("v", Direction2D.WEST), "<");
        dpad.put(Pair.of("v", Direction2D.NORTH), "^");
        dpad.put(Pair.of("v", Direction2D.EAST), ">");
        dpad.put(Pair.of(">", Direction2D.WEST), "v");
        dpad.put(Pair.of(">", Direction2D.NORTH), "A");
        dpad.put(Pair.of("^", Direction2D.SOUTH), "v");
        dpad.put(Pair.of("^", Direction2D.EAST), "A");
        dpad.put(Pair.of("A", Direction2D.SOUTH), ">");
        dpad.put(Pair.of("A", Direction2D.WEST), "^");
    }

    protected String test(String input) {
        log.debug(String.valueOf(findAllPaths("0", "9", List.of(), keypad)));
        return "";
    }

    protected String runSolution(String input, int depth) {
        initDistance(depth);

        AtomicLong score = new AtomicLong();
        input.lines().forEach(code -> {
            var c = code.split("");
            long dist = 0;
            for (int i = 0; i < c.length; ++i) {
                long d = distance.get(Pair.of(i == 0 ? "A" : c[i-1], c[i]));
                dist += d;
            }

            long codeScore = dist * Long.parseLong(code.replaceAll("A", ""));
            log.info("{} {} -> {}", code, dist, codeScore);
            score.addAndGet(codeScore);

        });

        return "%d".formatted(score.get());
    }

    private void initDistance(int depth) {
        for (var start : List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A")) {
            for (var end : List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A")) {
                List<String> L0 = findAllPaths(start, end, List.of(), keypad)
                        .stream()
                        .map(path -> path.stream().map(Direction2D::toChar).collect(Collectors.joining()) + "A")
                        .toList();
                // L0 holds every possible path to get from `start` to `end`, using a directional keypad. It is encoded as path text

                distance.put(Pair.of(start, end), getShortest(List.of(L0), depth));
            }
        }
    }

    private long getShortest(List<List<String>> steps, int depth) {
        if (cache.containsKey(Pair.of(steps, depth))) {
            return cache.get(Pair.of(steps, depth));
        }

        if (depth == 0) {
            long shortestCombination = steps.stream()
                    .map(step -> step.stream().min(Comparator.comparingLong(String::length)).orElseThrow())
                    .collect(Collectors.joining())
                    .length();
            cache.put(Pair.of(steps, depth), shortestCombination);
            return shortestCombination;
        }

        long path = 0;
        for (var step : steps) {
            Long stepShortest = null;
            for (var candidate : step) {
                var L1 = iterateKeypad(candidate);
                var shortest = getShortest(L1, depth - 1);
                if (stepShortest == null || stepShortest > shortest) stepShortest = shortest;
            }
            assert stepShortest != null;
            path += stepShortest;
        }
        cache.put(Pair.of(steps, depth), path);
        return path;
    }

    private List<List<String>> iterateKeypad(String path) {
        var pathLs = path.split("");
        List<List<String>> paths = new ArrayList<>();

        // for every step in the path, calculate possible ways to do it.
        for (int i = 0; i < pathLs.length; ++i) {
            var start = i == 0 ? "A" : pathLs[i - 1];
            var end = pathLs[i];
            var sePaths = findAllPaths(start, end, List.of(), dpad);
            paths.add(sePaths.stream().map(p -> p.stream().map(Direction2D::toChar).collect(Collectors.joining()) + "A").toList());
        }

        return paths;
    }

    private List<List<Direction2D>> findAllPaths(
            String start,
            String end,
            List<String> visited,
            Map<Pair<String, Direction2D>, String> graph
    ) {
        if (start.equals(end)) {
            List<List<Direction2D>> empty = new ArrayList<>();
            empty.add(new ArrayList<>());
            return empty;
        }

        var subpaths = new ArrayList<List<Direction2D>>();

        var newVisisted = Stream.concat(visited.stream(), Stream.of(start)).toList();
        for (Direction2D direction : Direction2D.values()) {
            if (graph.containsKey(Pair.of(start, direction))) {
                var next = graph.get(Pair.of(start, direction));
                if (visited.contains(next)) {
                    continue;
                }

                var subpath = findAllPaths(graph.get(Pair.of(start, direction)), end, newVisisted, graph);
                subpath.forEach(path -> path.addFirst(direction));
                subpaths.addAll(subpath);
            }
        }

        return subpaths;
    }
}
