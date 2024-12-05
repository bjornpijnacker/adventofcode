package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SolutionDay(year = 2024, day = 2)
@Slf4j
public class Day2 extends AdventOfCodeSolution {

    public Day2() {
        super(2, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var reports = Arrays.stream(input.split("\n")).toList();
        var numSafe = reports.stream()
                .map(report -> report.split(" "))
                .map(report -> Arrays.stream(report).map(Integer::valueOf).toList())
                .filter(report -> isSafeAsc(report) || isSafeDesc(report))
                .count();
        return String.valueOf(numSafe);
    }

    private boolean isSafeAsc(List<Integer> report) {
        for (int i = 1; i < report.size(); i++) {
            if (report.get(i) <= report.get(i - 1) || report.get(i) - report.get(i - 1) > 3) {
                return false;
            }
        }
        return true;
    }

    private boolean isSafeDesc(List<Integer> report) {
        for (int i = 1; i < report.size(); i++) {
            if (report.get(i) >= report.get(i - 1) || report.get(i - 1) - report.get(i) > 3) {
                return false;
            }
        }
        return true;
    }

    protected String runSolutionB(String input) {
        var reports = Arrays.stream(input.split("\n")).toList();
        var numSafe = reports.stream()
                .map(report -> report.split(" "))
                .map(report -> Arrays.stream(report).map(Integer::valueOf).toList())
                .filter(report -> {
                    if (isSafeAsc(report) || isSafeDesc(report)) return true;
                    for (int skip = 0; skip < report.size(); skip++) {
                        var dampedReport = new ArrayList<Integer>();
                        for (int i = 0; i < report.size(); i++) {
                            if (i == skip) continue;
                            dampedReport.add(report.get(i));
                        }
                        if (isSafeAsc(dampedReport) || isSafeDesc(dampedReport)) return true;
                    }
                    return false;
                })
                .count();
        return String.valueOf(numSafe);
    }
}
