package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@SolutionDay(year = 2015, day = 20)
@Slf4j
public class Day20 extends AdventOfCodeSolution {
    public Day20() {
        super(20, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        var target = Integer.parseInt(input);
        var houses = new long[target / 10]; // target/10 is definitely a solution

        for (int i = 1; i < target / 10; ++i) {
            for (int j = i; j < target / 10; j += i) {
                houses[j] += i * 10;
            }
        }

        for (int i = 1; i < target / 10; ++i) {
            if (houses[i] >= target) {
                return "%,d".formatted(i);
            }
        }

        throw new RuntimeException("No solution found");
    }

    public String runSolutionB(String input) {
        var target = Integer.parseInt(input);
        var houses = new long[target / 11];

        for (int i = 1; i < target / 11; ++i) {
            for (int j = 0; j < 50 && j * i < target / 11; ++j) {
                houses[j * i] += i * 11;
            }
        }

        for (int i = 1; i < target / 11; ++i) {
            if (houses[i] >= target) {
                return "%,d".formatted(i);
            }
        }

        throw new RuntimeException("No solution found");
    }
}
