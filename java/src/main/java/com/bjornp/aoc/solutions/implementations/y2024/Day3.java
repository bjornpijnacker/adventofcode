package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@SolutionDay(year = 2024, day = 3)
@Slf4j
public class Day3 extends AdventOfCodeSolution {

    public Day3() {
        super(3, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var pattern = Pattern.compile("mul\\((?<l>\\d{1,3}),(?<r>\\d{1,3})\\)");
        var matcher = pattern.matcher(input);
        long sum = 0;
        while (matcher.find()) {
            var l = Long.valueOf(matcher.group("l"));
            var r = Long.valueOf(matcher.group("r"));

            sum += l * r;
        }
        return "%,d".formatted(sum);
    }

    protected String runSolutionB(String input) {
        // find mul(...,...) or do() or don't(); capture groups for L and R operants of mul(.,.)
        var pattern = Pattern.compile("mul\\((?<l>\\d{1,3}),(?<r>\\d{1,3})\\)|do\\(\\)|don't\\(\\)");
        var matcher = pattern.matcher(input);

        long sum = 0;
        var enabled = true;

        while (matcher.find()) {  // loop over every match in order
            if (matcher.group().equals("do()")) {  // if we have a do() in the anonymous group (whole match)
                enabled = true;
            } else if (matcher.group().equals("don't()")) {  // if we have a don't() in the anonymous group (whole match)
                enabled = false;
            } else {  // we have mul()
                if (!enabled) continue;
                var l = Long.valueOf(matcher.group("l"));  // parse the L and R operant groups
                var r = Long.valueOf(matcher.group("r"));

                sum += l * r;  // count the result
            }
        }
        return "%,d".formatted(sum);
    }
}
