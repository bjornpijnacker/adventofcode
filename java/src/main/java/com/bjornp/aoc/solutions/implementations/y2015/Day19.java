package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SolutionDay(year = 2015, day = 19)
@Slf4j
public class Day19 extends AdventOfCodeSolution {
    private final HashMap<String, List<String>> replacements = new HashMap<>();
    private final HashMap<String, String> reverseReplacements = new HashMap<>();

    public Day19() {
        super(19, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        input.split("\n\n")[0].lines().forEach(line -> {
            var split = line.split(" => ");
            var key = split[0];
            var value = split[1];
            if (replacements.containsKey(key)) {
                replacements.get(key).add(value);
            } else {
                replacements.put(key, new ArrayList<>(List.of(value)));
            }
        });

        return "%,d".formatted(countReplacements(input.split("\n\n")[1]));
    }

    public String runSolutionB(String input) {
        input.split("\n\n")[0].lines().forEach(line -> {
            var split = line.split(" => ");
            var key = split[0];
            var value = split[1];
            reverseReplacements.put(value, key);
        });

        var i = 0;
        while (true) {
            var target = input.split("\n\n")[1].trim();
            var entries = new ArrayList<>(reverseReplacements.entrySet());
            Collections.shuffle(entries);
            i = 0;
            outer: while (true) {
                for (var replacement : entries) {
                    if (target.contains(replacement.getKey())) {
                        target = target.replaceFirst(replacement.getKey(), replacement.getValue());
                        i++;
                        continue outer;
                    }
                }
                break;
            }

            if (target.equals("e")) {
                break;
            }
        }

        return "%,d".formatted(i);
    }

    private long countReplacements(String s) {
        var actualReplacements = calculateReplacements(s);
        return actualReplacements.stream().map(replacement -> {
            var afterReplace = s.substring(0, replacement.getRight())
                    + replacement.getMiddle()
                    + s.substring(replacement.getRight() + replacement.getLeft().length());
            return afterReplace;
        }).distinct().count();
    }

    private Set<Triple<String, String, Integer>> calculateReplacements(String s) {
        var replacementTuples = new HashSet<Triple<String, String, Integer>>();
        for (var prefix : replacements.keySet()) {
            var pattern = Pattern.compile(prefix);
            var matcher = pattern.matcher(s);
            while (matcher.find()) {
                for (var replacement : replacements.get(prefix)) {
                    replacementTuples.add(Triple.of(prefix, replacement, matcher.start()));
                }
            }
        }
        return replacementTuples;
    }

    private List<String> performReplacements(String s) {
        var replacementTuples = calculateReplacements(s);

        return replacementTuples.stream().map(replacement -> {
            var afterReplace = s.substring(0, replacement.getRight())
                    + replacement.getMiddle()
                    + s.substring(replacement.getRight() + replacement.getLeft().length());
            return afterReplace;
        }).toList();
    }

    private List<String> prune(List<String> ls, String target) {
        return ls.stream().filter(s -> {
            return s.charAt(0) == target.charAt(0) || replacements.keySet().stream().anyMatch(s::startsWith);
        }).toList();
    }
}
