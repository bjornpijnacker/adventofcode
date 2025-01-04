package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Grid2D;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SolutionDay(year = 2024, day = 25)
@Slf4j
public class Day25 extends AdventOfCodeSolution {

    public Day25(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var locks = new ArrayList<int[]>();
        var keys = new ArrayDeque<int[]>();

        for (var lock : input.split("\n\n")) {
            var grid = new Grid2D<>(lock.lines().map(line -> Arrays.stream(line.split("")).toArray(String[]::new)).toArray(String[][]::new));

            int[] filled = new int[grid.getWidth()];
            for (int x = 0; x < grid.getWidth(); ++x) {
                filled[x] = (int) grid.yLine(x).stream().filter(g -> g.equals("#")).count() - 1;
            }
            if (grid.xLine(0).equals(Collections.nCopies(grid.getWidth(), "#"))) {
                locks.add(filled);
            } else {
                keys.add(filled);
            }
        }

        int fits = 0;
        for (var lock : locks) {
            k: for (var key : keys) {
                for (int i = 0; i < lock.length; ++i) {
                    if (lock[i] + key[i] > 5) continue k;
                }
                fits++;
            }
        }

        return "%d".formatted(fits);
    }

    protected String runSolutionB(String input) {

        return "";
    }
}