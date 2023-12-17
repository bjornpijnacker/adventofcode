package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.LogUtils;
import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.linear.MatrixUtils;

import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(day = 14)
@Slf4j
public class Day14 extends AdventOfCodeSolution {
    public Day14() {
        super(14);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var cols = transpose(input).split("\n");

        var rolled = rollNorth(cols);

        var output = Arrays.stream(rolled).mapToLong(line -> {
            // count the sum
            long sum = 0;
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == 'O') {
                    sum += line.length() - i;
                }
            }
            return sum;
        }).sum();

        return "%,d".formatted(output);
    }

    private String runSolutionB(String input) {
        var cols = transpose(input).split("\n");
        var oldCols = new HashMap<String, Integer>();

        for (int i = 0; i < 1_000_000_000; ++i) {
            cols = rotate(rollNorth(rotate(rollNorth(rotate(rollNorth(rotate(rollNorth(cols))))))));

            if (oldCols.containsKey(String.join("\n", cols))) {
                var cycleStart = oldCols.get(String.join("\n", cols));
                var cycleLength = i - cycleStart;

                var cycleIndex = (1_000_000_000 - cycleStart) % cycleLength;

                cols = oldCols.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() == cycleStart + cycleIndex - 1)
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow()
                        .split("\n");
                break;
            }
            oldCols.put(String.join("\n", cols), i);
        }

        var output = Arrays.stream(cols).mapToLong(line -> {
            // count the sum
            long sum = 0;
            for (int i = 0; i < line.length(); ++i) {
                if (line.charAt(i) == 'O') {
                    sum += line.length() - i;
                }
            }
            return sum;
        }).sum();

        return "%,d".formatted(output);
    }

    private int ordinal(String c) {
        return switch (c) {
            case "#" -> 0;
            case "O" -> 1;
            case "." -> 2;
            default -> throw new IllegalArgumentException("Unexpected value: " + c);
        };
    }

    private String[] rollNorth(String[] s) {
        return Arrays.stream(s).map(col -> {
            var cubeSections = new ArrayList<String>();

            // calculate the sections
            var tempSingleSection = new StringBuilder();
            for (char c : col.toCharArray()) {
                if (c == '#' && !tempSingleSection.isEmpty()) {
                    cubeSections.add(tempSingleSection.toString());
                    tempSingleSection = new StringBuilder();
                }
                tempSingleSection.append(c);
            }
            if (!tempSingleSection.isEmpty()) {
                cubeSections.add(tempSingleSection.toString());
            }

            // sort each section and join then together
            List<String> sortedCubesLs = cubeSections.stream().map(section -> {
                var chars = section.split("");
                Arrays.sort(chars, Comparator.comparingInt(this::ordinal));
                return String.join("", chars);
            }).toList();

            return String.join("", sortedCubesLs);
        }).toArray(String[]::new);
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

    private String[] rotate(String[] s) {  // FIXME Can be optimized by rotating in-place?
        var regArray = Arrays.stream(s).map(row -> row.split("")).toArray(String[][]::new);
        var newArray = new String[regArray[0].length][regArray.length];
        for (int i = 0; i < regArray.length; ++i) {
            for (int j = 0; j < regArray[i].length; ++j) {
                newArray[regArray.length - i - 1][j] = regArray[j][i];
            }
        }
        return String.join("\n", Arrays.stream(newArray).map(row -> String.join("", row)).toArray(String[]::new))
                .split("\n");
    }
}
