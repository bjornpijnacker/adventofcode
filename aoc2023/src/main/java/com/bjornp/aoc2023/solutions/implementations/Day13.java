package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.MatrixUtils;

import java.util.Arrays;

@SolutionDay(day = 13)
@Slf4j
public class Day13 extends AdventOfCodeSolution {
    public Day13() {
        super(13);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var patterns = input.split("\n\n");

        var notesSum = Arrays.stream(patterns).mapToLong(pattern -> {
            long hMirror = findMirror(pattern.split("\n"));
            long vMirror = findMirror(transpose(pattern).split("\n"));

            return hMirror * 100 + vMirror;
        }).sum();

        return "%,d".formatted(notesSum);
    }

    private String runSolutionB(String input) {
        var patterns = input.split("\n\n");

        var notesSum = Arrays.stream(patterns).mapToLong(pattern -> {
            long hMirror = findMirrorWithSmudge(pattern.split("\n"));
            long vMirror = findMirrorWithSmudge(transpose(pattern).split("\n"));

            return hMirror * 100 + vMirror;
        }).sum();

        return "%,d".formatted(notesSum);
    }

    private String transpose(String s) {
        var array = Arrays.stream(s.split("\n"))
                .map(row -> Arrays.stream(row.split("")).mapToDouble(r -> r.charAt(0)).toArray())
                .toArray(double[][]::new);
        var matrix = MatrixUtils.createRealMatrix(array).transpose();
        var data = matrix.getData();
        StringBuilder transposedInputSb = new StringBuilder();
        for (double[] row : data) {
            for (double cell : row) {
                transposedInputSb.append((char) cell);
            }
            transposedInputSb.append("\n");
        }
        return transposedInputSb.toString();
    }

    private long findMirror(String[] lines) {
        long mirror = 0;
        line: for (int i = 1; i < lines.length; ++i) {
            for (int d = 0; d < i && i + d < lines.length; ++d) {
                if (!lines[i+d].equals(lines[i-d-1])) {
                    continue line;
                }
            }
            mirror = i;
        }
        return mirror;
    }

    private long findMirrorWithSmudge(String[] lines) {
        long mirror = 0;
        line: for (int i = 1; i < lines.length; ++i) {
            int differenceCount = 0;
            for (int d = 0; d < i && i + d < lines.length; ++d) {
                var difference = StringUtils.indexOfDifference(lines[i+d], lines[i-d-1]);
                if (difference != -1 && differenceCount <= 1 && StringUtils.difference(lines[i+d].substring(difference+1), lines[i-d-1].substring(difference+1)).isEmpty()) {
                    // there is exactly 1 difference
                    differenceCount++;
                } else if (difference != -1) {
                    // more than 1 difference
                    continue line;
                }
            }
            if (differenceCount == 1) {
                mirror = i;
            }
        }
        return mirror;
    }
}
