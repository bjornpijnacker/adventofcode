package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SolutionDay(day = 9)
@Slf4j
public class Day9 extends AdventOfCodeSolution {
    public Day9() {
        super(9);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var values = Arrays.stream(input.split("\n")).map(value -> Arrays.stream(value.split(" ")).map(Integer::parseInt).toList()).toList();

        var summed = values.stream().mapToInt(this::getNext).sum();

        return "%,d".formatted(summed);
    }

    private String runSolutionB(String input) {
        var values = Arrays.stream(input.split("\n")).map(value -> Arrays.stream(value.split(" ")).map(Integer::parseInt).toList()).toList();

        var summed = values.stream().mapToInt(this::getPrevious).sum();

        return "%,d".formatted(summed);
    }

    private int getNext(List<Integer> values) {
        if (values.stream().allMatch(value -> value == 0)) {
            return 0;
        }

        var differences = new ArrayList<Integer>();

        for (int i = 0; i < values.size() - 1; i++) {
            differences.add(values.get(i + 1) - values.get(i));
        }

        return values.get(values.size() - 1) + getNext(differences);
    }

    private int getPrevious(List<Integer> values) {
        if (values.stream().allMatch(value -> value == 0)) {
            return 0;
        }

        var differences = new ArrayList<Integer>();

        for (int i = 0; i < values.size() - 1; i++) {
            differences.add(values.get(i + 1) - values.get(i));
        }

        return values.get(0) - getPrevious(differences);
    }
}
