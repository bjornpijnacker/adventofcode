package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Coordinate2D;
import com.bjornp.aoc.util.Direction2D;
import com.bjornp.aoc.util.Grid2D;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashSet;

@SolutionDay(year = 2024, day = 6)
@Slf4j
public class Day6 extends AdventOfCodeSolution {

    public Day6() {
        super(6, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var start = grid.indexOf("^").get();
        grid.replaceAll("^", ".");

        var seen = new HashSet<Pair<Coordinate2D, Direction2D>>();

        var currentPos = start;
        var direction = Direction2D.NORTH;

        while (grid.inBounds(currentPos)) {
            if (seen.contains(Pair.of(currentPos, direction))) {
                throw new RuntimeException("Going in circles at %s".formatted(currentPos));
            }
            seen.add(Pair.of(currentPos, direction));

            var nextPos = currentPos.move(direction);
            if (!grid.inBounds(nextPos)) break;
            // check if the next spot is valid to move to
            if (!grid.get(nextPos).equals("#")) {
                currentPos = nextPos;
            } else {  // if not, rotate right and continue
                direction = direction.right();
            }
        }

        return "%,d".formatted(seen.stream().map(Pair::getLeft).distinct().count());
    }

    protected String runSolutionB(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var start = grid.indexOf("^").get();
        grid.replaceAll("^", ".");

        var nWorkingObstructions = 0;

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                var obstruction = new Coordinate2D(x, y);
                if (obstruction.equals(start)) continue;

                var obstructedGrid = new Grid2D<>(grid);
                obstructedGrid.set(obstruction, "#");
                var seen = new HashSet<Pair<Coordinate2D, Direction2D>>();

                var currentPos = start;
                var direction = Direction2D.NORTH;

                try {
                    while (obstructedGrid.inBounds(currentPos)) {
                        if (seen.contains(Pair.of(currentPos, direction))) {
                            throw new GoingInCirclesException(currentPos);
                        }
                        seen.add(Pair.of(currentPos, direction));

                        var nextPos = currentPos.move(direction);
                        if (!obstructedGrid.inBounds(nextPos)) break;
                        // check if the next spot is valid to move to
                        if (!obstructedGrid.get(nextPos).equals("#")) {
                            currentPos = nextPos;
                        } else {  // if not, rotate right and continue
                            direction = direction.right();
                        }
                    }
                } catch (GoingInCirclesException e) {
                    nWorkingObstructions++;
                }
            }
        }

        return "%,d".formatted(nWorkingObstructions);
    }

    private static class GoingInCirclesException extends RuntimeException {
        public GoingInCirclesException(Coordinate2D pos) {
            super("Going in circles at %s".formatted(pos));
        }
    }
}
