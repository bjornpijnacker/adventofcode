package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 10)
@Slf4j
public class Day10 extends AdventOfCodeSolution {
    public Day10() {
        super(10, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var line = input;

        for (int i = 0; i < 40; ++i) {
            var newLine = new StringBuilder();
            for (int k = 0; k < line.length();) {
                var cur = line.charAt(k);
                int j = 0;
                for (; j < line.length() - k; ++j) {
                    if (line.charAt(k + j) != cur) {
                        break;
                    }
                }
                newLine.append(j).append(cur);
                k += j;
            }
            line = newLine.toString();
        }

        return "%,d".formatted(line.length());
    }

    public String runSolutionB(String input) {
        var line = input;

        for (int i = 0; i < 50; ++i) {
            var newLine = new StringBuilder();
            for (int k = 0; k < line.length();) {
                var cur = line.charAt(k);
                int j = 0;
                for (; j < line.length() - k; ++j) {
                    if (line.charAt(k + j) != cur) {
                        break;
                    }
                }
                newLine.append(j).append(cur);
                k += j;
            }
            line = newLine.toString();
        }

        return "%,d".formatted(line.length());
    }

}
