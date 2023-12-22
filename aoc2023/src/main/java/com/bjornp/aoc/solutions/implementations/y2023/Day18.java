package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SolutionDay(year = 2023, day = 18)
@Slf4j
public class Day18 extends AdventOfCodeSolution {
    private final Pattern linePattern = Pattern.compile("([RDLU]) (\\d+) \\((#[0-9a-fA-F]{6})\\)");

    public Day18() {
        super(18, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var borders = new ArrayList<LongVector2D>();
        var current = new LongVector2D(0, 0);

        long length = 0;
        borders.add(current);

        for (String line : input.split("\n")) {
            var matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                var direction = matcher.group(1);
                var num = Integer.parseInt(matcher.group(2));
                switch (direction) {
                    case "R" -> current = current.right(num);
                    case "L" -> current = current.left(num);
                    case "U" -> current = current.above(num);
                    case "D" -> current = current.below(num);
                }
                length += num;
                borders.add(current);
            }
        }

        return "%,d".formatted(shoelacePick(borders, length));
    }

    public String runSolutionB(String input) {
        var borders = new ArrayList<LongVector2D>();
        var current = new LongVector2D(0, 0);
        long length = 0;
        borders.add(current);

        for (String line : input.split("\n")) {
            var matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                var direction = matcher.group(3).substring(6);
                var num = Integer.parseInt(matcher.group(3).substring(1, 6), 16);
                switch (direction) {
                    case "0" -> current = current.right(num);
                    case "2" -> current = current.left(num);
                    case "3" -> current = current.above(num);
                    case "1" -> current = current.below(num);
                }
                length += num;
                borders.add(current);
            }
        }

        return "%,d".formatted(shoelacePick(borders, length));
    }

    private long shoelacePick(List<LongVector2D> borders, long length) {
        long sum = 0;
        for (int i = 1; i < borders.size() - 1; ++i) {
            sum += (borders.get(i + 1).x + borders.get(i).x) * (borders.get(i + 1).y - borders.get(i).y);
        }
        return (sum / 2) + (length / 2) + 1;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class LongVector2D {
        @EqualsAndHashCode.Include
        private long x;

        @EqualsAndHashCode.Include
        private long y;

        public LongVector2D left() {
            return new LongVector2D(x - 1, y);
        }

        public LongVector2D right() {
            return new LongVector2D(x + 1, y);
        }

        public LongVector2D above() {
            return new LongVector2D(x, y - 1);
        }

        public LongVector2D below() {
            return new LongVector2D(x, y + 1);
        }

        public LongVector2D left(long factor) {
            return new LongVector2D(x - factor, y);
        }

        public LongVector2D right(long factor) {
            return new LongVector2D(x + factor, y);
        }

        public LongVector2D above(long factor) {
            return new LongVector2D(x, y - factor);
        }

        public LongVector2D below(long factor) {
            return new LongVector2D(x, y + factor);
        }

        public LongVector2D add(LongVector2D other) {
            return new LongVector2D(x + other.x, y + other.y);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }
}
