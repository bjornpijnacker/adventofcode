package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(year = 2024, day = 5)
@Slf4j
public class Day5 extends AdventOfCodeSolution {
    // rules are indexed by L value for easy retrieval
    private final Map<Integer, List<Pair<Integer, Integer>>> rules = new HashMap<>();
    private final Map<Integer, List<Pair<Integer, Integer>>> rulesR = new HashMap<>();

    public Day5() {
        super(5, 2024);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private void parseRules(String[] rulesStr) {
        for (String rule : rulesStr) {
            var split = rule.split("[|]");
            var l = Integer.parseInt(split[0]);
            var r = Integer.parseInt(split[1]);

            {
                var a = rules.getOrDefault(l, new ArrayList<>());
                a.add(Pair.of(l, r));
                rules.put(l, a);
            }

            {
                var a = rulesR.getOrDefault(r, new ArrayList<>());
                a.add(Pair.of(l, r));
                rulesR.put(r, a);
            }
        }
    }

    protected String runSolutionA(String input) {
        var split = input.split("\n\n");
        parseRules(split[0].split("\n"));

        var updates = split[1].split("\n");
        int sum = 0;

        for (String updateStr : updates) {
            var update = Arrays.stream(updateStr.split(",")).map(Integer::parseInt).toList();

            var numbersToSee = new HashSet<Integer>();

            for (Integer page : update) {
                numbersToSee.remove(page);  // remove the current number

                // add any new numbers based on the rules
                var applicableRules = rules.getOrDefault(page, new ArrayList<>());
                applicableRules.forEach(rule -> numbersToSee.add(rule.getRight()));
            }

            // check if any missed numbers are contained in the update
            var valid = numbersToSee.stream().noneMatch(update::contains);
            if (valid) {
                var middle = update.get(update.size() / 2);
                sum += middle;
            }
        }

        return "%,d".formatted(sum);
    }

    protected String runSolutionB(String input) {
        var split = input.split("\n\n");
        parseRules(split[0].split("\n"));

        var updates = split[1].split("\n");
        int sum = 0;

        for (String s : updates) {
            var iterations = 0;
            var update = Arrays.stream(s.split(",")).map(Integer::parseInt).collect(Collectors.toList());

            wh:
            while (true) {
                iterations++;
                var alreadySeen = new HashSet<Integer>();

                for (Integer page : update) {
                    alreadySeen.add(page);
                    var rulesInViolation = rulesR
                            .getOrDefault(page, new ArrayList<>())
                            .stream()
                            .filter(rule -> update.contains(rule.getLeft()) && !alreadySeen.contains(rule.getLeft()))
                            .toList();

                    if (!rulesInViolation.isEmpty()) {
                        rulesInViolation.forEach(rule -> Collections.swap(update, update.indexOf(rule.getLeft()), update.indexOf(rule.getRight())));
                        continue wh;
                    }
                }
                break;
            }

            if (iterations > 1) {
                var middle = update.get(update.size() / 2);
                sum += middle;
            }
        }



        return "%,d".formatted(sum);
    }
}
