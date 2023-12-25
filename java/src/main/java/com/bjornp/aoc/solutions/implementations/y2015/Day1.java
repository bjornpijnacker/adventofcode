package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@SolutionDay(year = 2015, day = 1)
@Slf4j
public class Day1 extends AdventOfCodeSolution {
    public Day1() {
        super(1, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        return "%,d".formatted(input.chars().filter(c -> c == '(').count() - input.chars().filter(c -> c == ')').count());
    }

    public String runSolutionB(String input) {
        long floor = 0;
        long position = 0;
        for (char ch : input.toCharArray()) {
            position++;
            if (ch == '(') {
                floor++;
            } else if (ch == ')') {
                floor--;
            }
            if (floor < 0) {
                break;
            }
        }
        return "%,d".formatted(position);
    }
}
