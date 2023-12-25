package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;

import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

@SolutionDay(year = 2023, day = 11)
@Slf4j
public class Day11 extends AdventOfCodeSolution {
    public Day11() {
        super(11, 2023);

        register("a", input -> runSolution(input, 1));
        register("b", input -> runSolution(input, 999999));
    }

    public String runSolution(String input, int multiplier) {
        var width = input.split("\n")[0].length();
        var height = input.split("\n").length;

        var pounds = Pattern.compile("#")
                .matcher(input.replace("\n", ""))
                .results()
                .map(MatchResult::start)
                .map(index -> Coordinate.fromIndex(width, index))
                .toList();

        var horizEmpty = Pattern.compile("[.]{" + width + "}\n")
                .matcher(input)
                .results()
                .map(MatchResult::start)
                .map(index -> index / height)
                .toList();

        // create transposed input string
        var array = Arrays.stream(input.split("\n"))
                .map(row -> Arrays.stream(row.split("")).mapToDouble(s -> s.charAt(0)).toArray())
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
        var transposedInput = transposedInputSb.toString();

        var vertEmpty = Pattern.compile("[.]{" + width + "}\n")
                .matcher(transposedInput)
                .results()
                .map(MatchResult::start)
                .map(index -> index / height)
                .toList();

        pounds.forEach(pound -> {
            var horizOffset = horizEmpty.stream().filter(i -> i < pound.y).count();
            var vertOffset = vertEmpty.stream().filter(i -> i < pound.x).count();
            pound.x += vertOffset * multiplier;
            pound.y += horizOffset * multiplier;
        });

        long distanceSum = 0;
        for (int i = 0; i < pounds.size() - 1; ++i) {
            for (int j = i + 1; j < pounds.size(); ++j) {
                var distance = Math.abs(pounds.get(i).x - pounds.get(j).x) + Math.abs(pounds.get(i).y - pounds.get(j).y);
                distanceSum += distance;
            }
        }
        return "%,d".formatted(distanceSum);
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class Coordinate {
        @EqualsAndHashCode.Include
        private long x;

        @EqualsAndHashCode.Include
        private long y;

        public static Coordinate fromIndex(int width, int index) {
            return new Coordinate(index % width, index / width);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }
}
