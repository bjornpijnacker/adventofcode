package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SolutionDay(year = 2015, day = 23)
@Slf4j
public class Day23 extends AdventOfCodeSolution {
    private final ArrayList<Triple<String, String, Integer>> instructions = new ArrayList<>();
    private final Map<String, Integer> registers = new HashMap<>();

    public Day23() {
        super(23, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private void init(String input) {
        input.lines().forEach(line -> {
            var parts = line.split(",\\s|\\s");

            switch (parts[0]) {
                case "hlf" -> {
                    instructions.add(Triple.of("hlf", parts[1], null));
                    registers.put(parts[1], 0);
                }
                case "tpl" -> {
                    instructions.add(Triple.of("tpl", parts[1], null));
                    registers.put(parts[1], 0);
                }
                case "inc" -> {
                    instructions.add(Triple.of("inc", parts[1], null));
                    registers.put(parts[1], 0);
                }
                case "jmp" -> instructions.add(Triple.of("jmp", null, Integer.parseInt(parts[1])));
                case "jie" -> {
                    instructions.add(Triple.of("jie", parts[1], Integer.parseInt(parts[2])));
                    registers.put(parts[1], 0);
                }
                case "jio" -> {
                    instructions.add(Triple.of("jio", parts[1], Integer.parseInt(parts[2])));
                    registers.put(parts[1], 0);
                }
            }
        });
    }

    private String run() {
        int i = 0;
        while (i < instructions.size()) {
            var instruction = instructions.get(i);

            switch (instruction.getLeft()) {
                case "hlf" -> {
                    registers.put(instruction.getMiddle(), registers.get(instruction.getMiddle()) / 2);
                    i++;
                }
                case "tpl" -> {
                    registers.put(instruction.getMiddle(), registers.get(instruction.getMiddle()) * 3);
                    i++;
                }
                case "inc" -> {
                    registers.put(instruction.getMiddle(), registers.get(instruction.getMiddle()) + 1);
                    i++;
                }
                case "jmp" -> i += instruction.getRight();
                case "jie" -> {
                    if (registers.get(instruction.getMiddle()) % 2 == 0) {
                        i += instruction.getRight();
                    } else {
                        i++;
                    }
                }
                case "jio" -> {
                    if (registers.get(instruction.getMiddle()) == 1) {
                        i += instruction.getRight();
                    } else {
                        i++;
                    }
                }
            }
        }

        return "%,d".formatted(registers.get("b"));
    }

    public String runSolutionA(String input) {
        init(input);
        return run();
    }

    public String runSolutionB(String input) {
        init(input);

        registers.put("a", 1);

        return run();
    }

}
