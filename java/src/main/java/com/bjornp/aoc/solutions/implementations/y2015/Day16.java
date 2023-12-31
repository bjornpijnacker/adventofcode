package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 16)
@Slf4j
public class Day16 extends AdventOfCodeSolution {
    private List<Map<String, Long>> aunts = new ArrayList<>();
    private final Map<Map<String, Long>, Long> auntNumbers = new HashMap<>();

    public Day16() {
        super(16, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        input.split("\n\n")[0].lines().forEach(line -> {
            var props = line.split(":", 2)[1].trim().split(", ");
            var map = new HashMap<String, Long>();
            for (String prop : props) {
                var split = prop.split(": ");
                map.put(split[0], Long.parseLong(split[1]));
                auntNumbers.put(map, Long.parseLong(line.split(":")[0].trim().substring(4)));
            }
            aunts.add(map);
        });

        input.split("\n\n")[1].lines().forEach(line -> {
            var name = line.split(":")[0].trim();
            var value = Long.parseLong(line.split(":")[1].trim());
            aunts = aunts.stream().filter(aunt -> {
                if (aunt.containsKey(name)) {
                    return aunt.get(name).equals(value);
                }
                return true;
            }).toList();
        });

        return "%,d".formatted(auntNumbers.get(aunts.get(0)));
    }

    public String runSolutionB(String input) {
        input.split("\n\n")[0].lines().forEach(line -> {
            var props = line.split(":", 2)[1].trim().split(", ");
            var map = new HashMap<String, Long>();
            for (String prop : props) {
                var split = prop.split(": ");
                map.put(split[0], Long.parseLong(split[1]));
                auntNumbers.put(map, Long.parseLong(line.split(":")[0].trim().substring(4)));
            }
            aunts.add(map);
        });

        input.split("\n\n")[1].lines().forEach(line -> {
            var name = line.split(":")[0].trim();
            var value = Long.parseLong(line.split(":")[1].trim());
            aunts = aunts.stream().filter(aunt -> {
                if (aunt.containsKey(name)) {
                    if (name.equals("cats") || name.equals("trees")) {
                        return aunt.get(name) > value;
                    }
                    if (name.equals("pomeranians") || name.equals("goldfish")) {
                        return aunt.get(name) < value;
                    }
                    return aunt.get(name).equals(value);
                }
                return true;
            }).toList();
        });

        return "%,d".formatted(auntNumbers.get(aunts.get(0)));
    }
}
