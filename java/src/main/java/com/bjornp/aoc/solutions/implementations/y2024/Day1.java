package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;

@SolutionDay(year = 2024, day = 1)
@Slf4j
public class Day1 extends AdventOfCodeSolution {

    public Day1() {
        super(1, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var l = new ArrayList<Integer>();
        var r = new ArrayList<Integer>();

        for (String line : input.split("\n")) {
            var lr = line.split(" {3}");
            l.add(Integer.parseInt(lr[0]));
            r.add(Integer.parseInt(lr[1]));
        }

        l.sort(Comparator.comparingInt(Integer::intValue));
        r.sort(Comparator.comparingInt(Integer::intValue));

        long sum = 0;
        for (int i = 0; i < l.size(); i++) {
            sum += Math.abs(l.get(i) - r.get(i));
        }
        return String.valueOf(sum);
    }

    protected String runSolutionB(String input) {
        var l = new ArrayList<Integer>();
        var r = new ArrayList<Integer>();

        for (String line : input.split("\n")) {
            var lr = line.split(" {3}");
            l.add(Integer.parseInt(lr[0]));
            r.add(Integer.parseInt(lr[1]));
        }

        var similarity = l.stream()
                .mapToLong(num -> r.stream().filter(rNum -> rNum.equals(num)).count() * num)
                .sum();
        return String.valueOf(similarity);
    }
}
