package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SolutionDay(year = 2015, day = 25)
@Slf4j
public class Day25 extends AdventOfCodeSolution {
    public Day25() {
        super(25, 2015);

        register("a", this::runSolutionA);
    }

    public String runSolutionA(String input) {
        var pattern = Pattern.compile("^To continue, please consult the code grid in the manual.  Enter the code at row (\\d+), column (\\d+).$");
        var matcher = pattern.matcher(input);
        if (matcher.matches()) {
            var row = Long.parseLong(matcher.group(1));
            var col = Long.parseLong(matcher.group(2));

            var code = 20151125L;
            for (long i = 1; i < exp(row, col); ++i) {
                code = (code * 252533) % 33554393;
            }

            return "%,d".formatted(code);
        } else {
            throw new RuntimeException("No match");
        }
    }

    private long exp(long row, long col) {
        return (row + col - 1) * (row + col - 2) / 2 + col;
    }
}
