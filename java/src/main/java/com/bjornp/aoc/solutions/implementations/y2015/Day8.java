package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 8)
@Slf4j
public class Day8 extends AdventOfCodeSolution {
    public Day8() {
        super(8, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var noWhitespace = input.replaceAll("\\n", "");
        var numQuotes = input.split("\n").length * 2;

        var simpleCount = noWhitespace.chars().count();

        var index = 0;
        while (index >= 0) {
            index = noWhitespace.indexOf("\\x", index + 1);
            if (index == -1) {
                break;
            }
            var num = 0;
            for (int i = index;; i--) {
                if (noWhitespace.charAt(i) == '\\') {
                    num++;
                } else {
                    break;
                }
            }
            if (num % 2 == 1) {
                noWhitespace = noWhitespace.substring(0, index) + "\\u00" + noWhitespace.substring(index + 2);
            }
        }

        var escapedCount = StringEscapeUtils.unescapeEcmaScript(noWhitespace).chars().count();

        return "%,d".formatted(simpleCount - (escapedCount - numQuotes));
    }

    public String runSolutionB(String input) {
        var noWhitespace = input.replaceAll("\\n", "");
        var numQuotes = input.split("\n").length * 2;

        var escaped = StringEscapeUtils.escapeJava(noWhitespace);
        return "%,d".formatted((numQuotes + escaped.length()) - noWhitespace.length());
    }
}
