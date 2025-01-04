package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SolutionDay(year = 2024, day = 20)
@Slf4j
public class Day20 extends AdventOfCodeSolution {

    public Day20(int day, int year) {
        super(day, year);

        register("a", input -> runSolution(input, 2));
        register("b", input -> runSolution(input, 20));
    }

    private List<IntVector2D> getGridPath(String input) {
        var grid = new Grid2D<>(input
                .lines()
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));

        var start = grid.findAll("S").stream().findFirst().orElseThrow();
        var end = grid.findAll("E").stream().findFirst().orElseThrow();

        var path = new ArrayList<IntVector2D>();
        // walk the path

        var cur = start;
        while (!cur.equals(end)) {
            path.add(cur);
            cur = Stream.of(cur.n(), cur.e(), cur.s(), cur.w())
                    .filter(n -> !grid.get(n).equals("#"))
                    .filter(n -> !path.contains(n))
                    .findFirst().orElseThrow();
        }
        path.add(end);

        return path;
    }

    protected String runSolution(String input, int cheatPicos) {
        var path = getGridPath(input);

        var cheat100 = IntStream.range(0, path.size())
                .flatMap(i -> IntStream.range(i + 100, path.size())
                        .map(j -> {
                            var cheatStart = path.get(i);
                            var cheatEnd = path.get(j);
                            var dist = cheatStart.dist(cheatEnd);
                            return dist <= cheatPicos && j - i - dist >= 100 ? 1 : 0;
                        }))
                .sum();

        return "%d".formatted(cheat100);
    }
}
