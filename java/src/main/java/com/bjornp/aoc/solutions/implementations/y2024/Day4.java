package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Coordinate2D;
import com.bjornp.aoc.util.Grid2D;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@SolutionDay(year = 2024, day = 4)
@Slf4j
public class Day4 extends AdventOfCodeSolution {

    public Day4() {
        super(4, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var grid = new Grid2D<>(Arrays.stream(input.split("\n")).map(i -> i.split("")).toArray(String[][]::new));
        long numXmas = 0;

        // count horizontal
        for (int x = 0; x < grid.getWidth() - 3; ++x) {
            for (int y = 0; y < grid.getHeight(); ++y) {
                var coord = new Coordinate2D(x, y);
                String xmas = String.join(
                        "",
                        grid.get(coord),
                        grid.get(coord.e()),
                        grid.get(coord.e(2)),
                        grid.get(coord.e(3))
                );
                if (xmas.equals("XMAS") || xmas.equals("SAMX")) {
                    numXmas++;
                }
            }
        }

        // count vertical
        for (int x = 0; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight() - 3; ++y) {
                var coord = new Coordinate2D(x, y);
                String xmas = String.join(
                        "",
                        grid.get(coord),
                        grid.get(coord.s()),
                        grid.get(coord.s(2)),
                        grid.get(coord.s(3))
                );
                if (xmas.equals("XMAS") || xmas.equals("SAMX")) {
                    numXmas++;
                }
            }
        }

        // count vertical SE
        for (int x = 0; x < grid.getWidth() - 3; ++x) {
            for (int y = 0; y < grid.getHeight() - 3; ++y) {
                var coord = new Coordinate2D(x, y);
                String xmas = String.join(
                        "",
                        grid.get(coord),
                        grid.get(coord.se()),
                        grid.get(coord.se(2)),
                        grid.get(coord.se(3))
                );
                if (xmas.equals("XMAS") || xmas.equals("SAMX")) {
                    numXmas++;
                }
            }
        }

        // count vertical SW
        for (int x = 3; x < grid.getWidth(); ++x) {
            for (int y = 0; y < grid.getHeight() - 3; ++y) {
                var coord = new Coordinate2D(x, y);
                String xmas = String.join(
                        "",
                        grid.get(coord),
                        grid.get(coord.sw()),
                        grid.get(coord.sw(2)),
                        grid.get(coord.sw(3))
                );
                if (xmas.equals("XMAS") || xmas.equals("SAMX")) {
                    numXmas++;
                }
            }
        }

        return "%d".formatted(numXmas);
    }

    protected String runSolutionB(String input) {
        var grid = new Grid2D<>(Arrays.stream(input.split("\n")).map(i -> i.split("")).toArray(String[][]::new));
        long numXmas = 0;

        for (int x = 0; x < grid.getWidth() - 2; ++x) {
            for (int y = 0; y < grid.getHeight() - 2; ++y) {
                var coord = new Coordinate2D(x, y);
                String xmas1 = String.join("", grid.get(coord), grid.get(coord.se()), grid.get(coord.se(2)));
                String xmas2 = String.join("", grid.get(coord.e(2)), grid.get(coord.se()), grid.get(coord.s(2)));
                if ((xmas1.equals("MAS") || xmas1.equals("SAM")) && (xmas2.equals("MAS") || xmas2.equals("SAM"))) {
                    numXmas++;
                }
            }
        }

        return "%d".formatted(numXmas);
    }
}
