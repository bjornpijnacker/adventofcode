package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

@SolutionDay(year = 2015, day = 2)
@Slf4j
public class Day2 extends AdventOfCodeSolution {
    public Day2() {
        super(2, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var total = input.lines().mapToLong(present -> {
            var sides = present.split("x");
            var l = Long.parseLong(sides[0]);
            var w = Long.parseLong(sides[1]);
            var h = Long.parseLong(sides[2]);
            var side1 = l * w;
            var side2 = w * h;
            var side3 = h * l;
            var smallestSide = Math.min(Math.min(side1, side2), side3);
            var surfaceArea = 2 * side1 + 2 * side2 + 2 * side3;
            return surfaceArea + smallestSide;
        }).sum();

        return "%,d".formatted(total);
    }

    public String runSolutionB(String input) {
        var total = input.lines().mapToLong(present -> {
            var sides = present.split("x");
            var l = Long.parseLong(sides[0]);
            var w = Long.parseLong(sides[1]);
            var h = Long.parseLong(sides[2]);
            var side1 = l * w;
            var side2 = w * h;
            var side3 = h * l;
            var smallestPerimeter = Math.min(Math.min(2 * l + 2 * w, 2 * w + 2 * h), 2 * h + 2 * l);
            var volume = l * w * h;
            return smallestPerimeter + volume;
        }).sum();

        return "%,d".formatted(total);
    }
}
