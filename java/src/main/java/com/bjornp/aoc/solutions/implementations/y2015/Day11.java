package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 11)
@Slf4j
public class Day11 extends AdventOfCodeSolution {
    public Day11() {
        super(11, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var password = input;
        do {
            password = nextPassword(password);
        } while (!isValid(password));

        return password;
    }

    public String runSolutionB(String input) {
        var password = input;
        do {
            password = nextPassword(password);
        } while (!isValid(password));

        do {
            password = nextPassword(password);
        } while (!isValid(password));

        return password;
    }

    private String nextPassword(String password) {
        var chars = password.toCharArray();
        for (int i = chars.length - 1; i >= 0; --i) {
            if (chars[i] == 'z') {
                chars[i] = 'a';
            } else {
                chars[i]++;
                break;
            }
        }
        return new String(chars);
    }

    private boolean isValid(String password) {
        var hasIncreasingStraight = false;
        var hasForbiddenLetters = false;
        var hasTwoPairs = false;

        var pairPattern = Pattern.compile("(?<pair>[a-z])\\1");

        var chars = password.toCharArray();

        for (int i = 0; i < chars.length - 3; ++i) {
            if (chars[i] + 1 == chars[i + 1] && chars[i + 1] + 1 == chars[i + 2]) {
                hasIncreasingStraight = true;
                break;
            }
        }

        if (password.contains("i") || password.contains("o") || password.contains("l")) {
            hasForbiddenLetters = true;
        }

        var matcher = pairPattern.matcher(password);
        var res = matcher.results().toList();
        if (res.size() >= 2) {
            var valid = false;
            for (int i = 0; i < res.size(); ++i) {
                for (int j = i + 1; j < res.size(); ++j) {
                    if (res.get(i).group(0).charAt(0) != res.get(j).group(0).charAt(0)) {
                        valid = true;
                    }
                }
            }
            hasTwoPairs = valid;
        }

        return hasIncreasingStraight && !hasForbiddenLetters && hasTwoPairs;
    }

}
