package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.Combinations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

@SolutionDay(year = 2024, day = 8)
@Slf4j
public class Day8 extends AdventOfCodeSolution {

    public Day8(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
        register("check", this::testAssumption);
    }

    protected String runSolutionA(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var antennaTypes = grid.getGrid().stream().filter(g -> !g.equals(".")).collect(Collectors.toUnmodifiableSet());

        var antinodes = new HashSet<IntVector2D>();
        antennaTypes.forEach(antennaType -> {
            var antennas = grid.findAll(antennaType).toArray(IntVector2D[]::new);

            new Combinations(antennas.length, 2)
                    .iterator().forEachRemaining(combination -> {
                var a = antennas[combination[0]];
                var b = antennas[combination[1]];
                var c1 = new IntVector2D(2 * b.getX() - a.getX(), 2 * b.getY() - a.getY());
                var c2 = new IntVector2D(2 * a.getX() - b.getX(), 2 * a.getY() - b.getY());

                if (grid.inBounds(c1)) antinodes.add(c1);
                if (grid.inBounds(c2)) antinodes.add(c2);
            });
        });

        return "%,d".formatted(antinodes.size());
    }

    protected String runSolutionB(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var antennaTypes = grid.getGrid().stream().filter(g -> !g.equals(".")).collect(Collectors.toUnmodifiableSet());

        var antinodes = new HashSet<IntVector2D>();
        antennaTypes.forEach(antennaType -> {
            var antennas = grid.findAll(antennaType).toArray(IntVector2D[]::new);
            antinodes.addAll(Arrays.asList(antennas));

            new Combinations(antennas.length, 2)
                    .iterator().forEachRemaining(combination -> {
                        var a = antennas[combination[0]];
                        var b = antennas[combination[1]];

                        /* assume that two antennas are always as close together as possible,
                         * c.q. there's no integer grid space between them:
                         * A . .
                         * . . .
                         * . . A
                         * ^ does not occur.
                         */

                        for (int i = 1;; i++) {
                            var c1 = new IntVector2D((i + 1) * b.getX() - i * a.getX(), (i + 1) * b.getY() - i * a.getY());
                            if (!grid.inBounds(c1)) break;
                            antinodes.add(c1);
                        }
                        for (int i = 1;; i++) {
                            var c2 = new IntVector2D((i + 1) * a.getX() - i * b.getX(), (i + 1) * a.getY() - i * b.getY());
                            if (!grid.inBounds(c2)) break;
                            antinodes.add(c2);
                        }
                    });
        });

        return "%,d".formatted(antinodes.size());
    }

    public String testAssumption(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var antennaTypes = grid.getGrid().stream().filter(g -> !g.equals(".")).collect(Collectors.toUnmodifiableSet());

        var antinodes = new HashSet<IntVector2D>();
        antennaTypes.forEach(antennaType -> {
            var antennas = grid.findAll(antennaType).toArray(IntVector2D[]::new);
            antinodes.addAll(Arrays.asList(antennas));

            new Combinations(antennas.length, 2)
                    .iterator().forEachRemaining(combination -> {
                        var a = antennas[combination[0]];
                        var b = antennas[combination[1]];

                        var den = b.getX() - a.getX();
                        var num = b.getY() - a.getY();

                        var gcd = ArithmeticUtils.gcd(den, num);
                        if (gcd != 1) {
                            log.warn("{} {}", a, b);
                        }
                    });
        });

        return "";
    }
}