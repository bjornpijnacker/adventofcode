package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;

@SolutionDay(year = 2023, day = 15)
@Slf4j
public class Day15 extends AdventOfCodeSolution {
    public Day15() {
        super(15, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var result = Arrays.stream(input.split(",")).mapToLong(step -> {
            long hash = 0;
            for (char c : step.toCharArray()) {
                hash = ((hash + c) * 17) % 256;
            }
            return hash;
        }).sum();

        return "%,d".formatted(result);
    }

    private String runSolutionB(String input) {
        LinkedList<String>[] boxes = new LinkedList[256];
        for (int i = 0; i < 256; ++i) {
            boxes[i] = new LinkedList<>();
        }
        for (String step : input.split(",")) {
            var label = step.split("[-=]\\d?")[0];
            var operation = step.substring(label.length());

            long hash = 0;
            for (char c : label.toCharArray()) {
                hash = ((hash + c) * 17) % 256;
            }

            if (operation.startsWith("-")) {
                boxes[(int) hash].removeIf(s -> s.startsWith(label + " "));
            } else if (operation.startsWith("=")) {
                var focalLength = Integer.parseInt(operation.substring(1));
                if (boxes[(int) hash].stream().noneMatch(s -> s.startsWith(label + " "))) {
                    boxes[(int) hash].add(label + " " + focalLength);
                } else {
                    boxes[(int) hash].replaceAll(s -> s.startsWith(label + " ") ? label + " " + focalLength : s);
                }
            }
        }

        long sum = 0;
        for (int i = 0; i < 256; ++i) {
            for (int l = 0; l < boxes[i].size(); ++l) {
                var box = boxes[i].get(l);
                long focalLength = Integer.parseInt(box.split(" ")[1]);
                sum += (i + 1) * (l + 1) * (focalLength);
            }
        }

        return "%,d".formatted(sum);
    }
}
