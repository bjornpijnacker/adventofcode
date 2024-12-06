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
import java.util.Set;
import java.util.stream.Collectors;

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

        var seen = walkGrid(grid, start);

        return "%,d".formatted(seen.stream().map(Pair::getLeft).distinct().count());
    }

    protected String runSolutionB(String input) {
        var grid = new Grid2D<>(Arrays
                .stream(input.split("\n"))
                .map(line -> Arrays.stream(line.split("")).toArray(String[]::new))
                .toArray(String[][]::new));
        var start = grid.indexOf("^").get();
        grid.replaceAll("^", ".");

        var path = walkGrid(grid, start).stream().map(Pair::getLeft).distinct().collect(Collectors.toList());
        path.remove(start);

//        var nWorkingObstructions = 0;

        var nWorkingObstructions = path.parallelStream().filter(obstruction -> {
            if (obstruction.equals(start)) {
                return false;
            }

            var obstructedGrid = new Grid2D<>(grid);
            obstructedGrid.set(obstruction, "#");

            try {
                walkGrid(obstructedGrid, start);
                return false;
                // if returns, grid was left.
            } catch (GoingInCirclesException e) {
                return true;
            }
        }).count();

        return "%,d".formatted(nWorkingObstructions);
    }

    private Set<Pair<Coordinate2D, Direction2D>> walkGrid(Grid2D<String> grid, Coordinate2D start) {
        var seen = new HashSet<Pair<Coordinate2D, Direction2D>>();

        var currentPos = start;
        var direction = Direction2D.NORTH;

        while (grid.inBounds(currentPos)) {
            if (seen.contains(Pair.of(currentPos, direction))) {
                throw new GoingInCirclesException(currentPos);
            }
            seen.add(Pair.of(currentPos, direction));

            var nextPos = currentPos.move(direction);
            if (!grid.inBounds(nextPos)) {
                break;
            }
            // check if the next spot is valid to move to
            if (!grid.get(nextPos).equals("#")) {
                currentPos = nextPos;
            } else {  // if not, rotate right and continue
                direction = direction.right();
            }
        }

        return seen;
    }

    private static class GoingInCirclesException extends RuntimeException {
        public GoingInCirclesException(Coordinate2D pos) {
            super("Going in circles at %s".formatted(pos));
        }
    }
}
