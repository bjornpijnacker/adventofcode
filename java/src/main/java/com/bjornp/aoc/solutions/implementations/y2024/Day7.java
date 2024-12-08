package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

@SolutionDay(year = 2024, day = 7)
@Slf4j
public class Day7 extends AdventOfCodeSolution {

    public Day7(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var eqs = input.split("\n");

        var result = Arrays.stream(eqs).map(eq -> {
            var split = eq.split(":");
            var test = Long.parseLong(split[0]);
            var values = Arrays.stream(split[1].trim().split(" ")).mapToLong(Long::parseLong).toArray();
            return Pair.of(test, values);
        }).filter(pair -> hasSolution2(pair.getLeft(), pair.getRight())).mapToLong(Pair::getLeft).sum();

        return "%d".formatted(result);
    }

    protected String runSolutionB(String input) {
        var eqs = input.split("\n");

        var result = Arrays.stream(eqs).parallel().map(eq -> {
            var split = eq.split(":");
            var test = Long.parseLong(split[0]);
            var values = Arrays.stream(split[1].trim().split(" ")).mapToLong(Long::parseLong).toArray();
            return Pair.of(test, values);
        }).filter(pair -> hasSolution3(pair.getLeft(), pair.getRight())).mapToLong(Pair::getLeft).sum();

        return "%d".formatted(result);
    }

    private boolean hasSolution2(long testValue, long[] operants) {
        var operators = new boolean[operants.length - 1];

        for (int i = 0; i < Math.pow(2, operants.length - 1); ++i) {
            // evaluate the equation
            var value = operants[0];
            for (int j = 1; j < operants.length; ++j) {
                if (value > testValue) break;  // optimization
                var r = operants[j];
                if (operators[j - 1]) {
                    value *= r;
                } else {
                    value += r;
                }
            }

            if (value == testValue) {
                return true;
            }

            // inc. by 1
            for (int o = operators.length - 1; o >= 0; o--) {
                if (operators[o]) {
                    operators[o] = false;
                } else {
                    operators[o] = true;
                    break;
                }
            }
        }

        return false;
    }

    private boolean hasSolution3(long testValue, long[] operants) {
        var operators = new int[operants.length - 1];

        for (int i = 0; i < Math.pow(3, operants.length - 1); ++i) {
            // evaluate the equation
            var value = operants[0];
            for (int j = 1; j < operants.length; ++j) {
                if (value > testValue) break;  // optimization
                var r = operants[j];
                if (operators[j - 1] == 0) {
                    value *= r;
                } else if (operators[j - 1] == 1) {
                    value += r;
                } else if (operators[j - 1] == 2) {
//                    var lStr = String.valueOf(value);
//                    var rStr = String.valueOf(r);
//                    value = Long.parseLong(lStr + rStr);
                    value = value * (long) Math.pow(10, Math.ceil(Math.log10(r + 1))) + r;
                } else {
                    throw new RuntimeException("Unsupported operator: " + operators[j - 1]);
                }
            }

            if (value == testValue) {
                return true;
            }

            // inc. by 1, base2 number is used for operators
            for (int o = operators.length - 1; o >= 0; o--) {
                if (operators[o] == 2) {
                    operators[o] = 0;
                } else {
                    operators[o]++;
                    break;
                }
            }
        }

        return false;
    }
}
