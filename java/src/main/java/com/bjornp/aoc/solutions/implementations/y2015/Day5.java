package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 5)
@Slf4j
public class Day5 extends AdventOfCodeSolution {
    public Day5() {
        super(5, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var nNiceStrings = input.lines().mapToLong(line -> {
            var vowels = line.chars().filter(c -> "aeiou".contains(String.valueOf((char) c))).count() >= 3;
            var doubleLetter = line.chars().anyMatch(c -> {
                var s = String.valueOf((char) c);
                return line.contains(s + s);
            });
            var hasIllegalStrings = line.contains("ab") || line.contains("cd") || line.contains("pq") || line.contains("xy");
            return vowels && doubleLetter && !hasIllegalStrings ? 1 : 0;
        }).sum();

        return "%,d".formatted(nNiceStrings);
    }

    public String runSolutionB(String input) {
        var nNiceStrings = input.lines().mapToLong(line -> {
            var pairPattern = Pattern.compile("([a-z]{2}).*\\1");
            var repeatPattern = Pattern.compile("([a-z]).\\1");
            return pairPattern.matcher(line).find() && repeatPattern.matcher(line).find() ? 1 : 0;
        }).sum();

        return "%,d".formatted(nNiceStrings);
    }
}
