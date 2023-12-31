package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@SolutionDay(year = 2015, day = 17)
@Slf4j
public class Day17 extends AdventOfCodeSolution {
    public Day17() {
        super(17, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        var bins = input.lines().map(Long::parseLong).toList();
        var sum = combinations(bins).stream()
                .mapToLong(combination -> combination.stream().mapToLong(Long::longValue).sum() == 150 ? 1 : 0)
                .sum();

        return "%,d".formatted(sum);
    }

    public String runSolutionB(String input) {
        var bins = input.lines().map(Long::parseLong).toList();

        var min = combinations(bins).stream()
                .filter(combination -> combination.stream().mapToLong(Long::longValue).sum() == 150)
                .min(Comparator.comparingInt(List::size))
                .get().size();

        var sum = combinations(bins).stream()
                .filter(combination -> combination.size() == min)
                .mapToLong(combination -> combination.stream().mapToLong(Long::longValue).sum() == 150 ? 1 : 0)
                .sum();

        return "%,d".formatted(sum);
    }

    private ArrayList<List<Long>> combinations(List<Long> list) {
        if (list.size() == 1) {
            var h = new ArrayList<List<Long>>();
            h.add(List.of());
            h.add(list);
            return h;
        }
        var head = list.get(0);
        var tails = combinations(list.subList(1, list.size()));
        var result = new ArrayList<List<Long>>();
        tails.forEach(tail -> {
            result.add(tail);
            var ls = new ArrayList<>(tail);
            ls.add(head);
            result.add(ls);
        });
        return result;
    }
}
