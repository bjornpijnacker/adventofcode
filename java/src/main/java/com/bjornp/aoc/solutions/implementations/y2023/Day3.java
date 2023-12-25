package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SolutionDay(year = 2023, day = 3)
@Slf4j
public class Day3 extends AdventOfCodeSolution {
    private int height;

    private int width;

    public Day3() {
        super(3, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var splitgrid = input.split("\n");
        this.width = splitgrid[0].length();
        this.height = splitgrid.length;

        var grid = String.join("", splitgrid);

        var symbolPattern = Pattern.compile("([^.0-9])");
        var matcher = symbolPattern.matcher(grid);

        var numberIndices = new ArrayList<Integer>();

        while (matcher.find()) {
            var index = this.reverseIndex(matcher.start());

            neighbors(index(index.getLeft(), index.getRight())).forEach(neightbor -> {
                if (stringAt(grid, neightbor).matches("[0-9]")) {
                    numberIndices.add(neightbor);
                }
            });
        }

//        log.info(String.valueOf(numberIndices.stream().map(this::reverseIndex).toList()));

        var numbers = new ArrayList<Integer>();

        var numberPattern = Pattern.compile("[0-9]+");
        var numberMatcher = numberPattern.matcher(grid);
        while (numberMatcher.find()) {
            var start = numberMatcher.start();
            var end = numberMatcher.end();
            if (numberIndices.stream().anyMatch(index -> index >= start && index < end)) {
                numbers.add(Integer.valueOf(numberMatcher.group()));
            }
        }

//        log.info(String.valueOf(numbers));

        return String.valueOf(numbers.stream().mapToInt(num -> num).sum());
    }

    private String stringAt(String str, int index) {
        return str.substring(index, index + 1);
    }

    private int index(int x, int y) {
        return x + y * this.width;
    }

    private List<Integer> neighbors(int num) {
        var index = reverseIndex(num);
        var output = new ArrayList<Pair<Integer, Integer>>();
        output.add(new ImmutablePair<>(index.getLeft() - 1, index.getRight()));
        output.add(new ImmutablePair<>(index.getLeft() - 1, index.getRight() - 1));
        output.add(new ImmutablePair<>(index.getLeft(), index.getRight() - 1));
        output.add(new ImmutablePair<>(index.getLeft() + 1, index.getRight() - 1));
        output.add(new ImmutablePair<>(index.getLeft() + 1, index.getRight()));
        output.add(new ImmutablePair<>(index.getLeft() + 1, index.getRight() + 1));
        output.add(new ImmutablePair<>(index.getLeft(), index.getRight() + 1));
        output.add(new ImmutablePair<>(index.getLeft() - 1, index.getRight() + 1));
        return output.stream()
                .filter(pair -> pair.getLeft() >= 0 && pair.getRight() >= 0 && pair.getLeft() < width && pair.getRight() < height)
                .map(pair -> this.index(pair.getLeft(), pair.getRight()))
                .collect(Collectors.toList());
    }

    private Pair<Integer, Integer> reverseIndex(int num) {
        int x = num % width;
        int y = num / width;
        return new ImmutablePair<>(x, y);
    }

    protected String runSolutionB(String input) {
        var splitgrid = input.split("\n");
        this.width = splitgrid[0].length();
        this.height = splitgrid.length;

        var grid = String.join("", splitgrid);

        var symbolPattern = Pattern.compile("([*])");
        var matcher = symbolPattern.matcher(grid);

        var numberIndices = new ArrayList<ArrayList<Integer>>();

        while (matcher.find()) {
            var index = this.reverseIndex(matcher.start());

            var localNumberIndices = new ArrayList<Integer>();
            neighbors(index(index.getLeft(), index.getRight())).forEach(neightbor -> {
                if (stringAt(grid, neightbor).matches("[0-9]")) {
                    localNumberIndices.add(neightbor);
                }
            });

            if (localNumberIndices.size() >= 2) {
                numberIndices.add(localNumberIndices);
            }
        }

        var numbers = new ArrayList<Integer>();

        numberIndices.forEach(numberSet -> {
            var result = new ArrayList<Integer>();

            var numberPattern = Pattern.compile("[0-9]+");
            var numberMatcher = numberPattern.matcher(grid);

            while (numberMatcher.find()) {
                var start = numberMatcher.start();
                var end = numberMatcher.end();

                var match = numberSet.stream().filter(num -> start <= num && end > num).findFirst();
                if (match.isPresent()) {
                    result.add(Integer.valueOf(numberMatcher.group()));
                }
            }

            if (result.stream().distinct().toList().size() == 2) {
                numbers.add(result.stream().reduce(1, (x, y) -> x * y));
            }
        });

//        log.info(String.valueOf(numbers));

        return String.valueOf(numbers.stream().mapToInt(num -> num).sum());
    }
}
