package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.common.collect.HashBasedTable;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 13)
@Slf4j
public class Day13 extends AdventOfCodeSolution {
    private final HashBasedTable<String, String, Long> happiness = HashBasedTable.create();

    public Day13() {
        super(13, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public void init(String input) {
        var pattern = Pattern.compile("^(?<name>[a-zA-Z]+) would (?<plusmin>gain|lose) (?<amount>\\d+) happiness units by sitting next to (?<name2>[a-zA-Z]+).$");
        input.lines().forEach(line -> {
            var m = pattern.matcher(line);
            if (m.matches()) {
                var name1 = m.group("name");
                var name2 = m.group("name2");
                boolean add = m.group("plusmin").equals("gain");
                var amount = Long.parseLong(m.group("amount")) * (add ? 1 : -1);
                happiness.put(name1, name2, amount);
            }
        });
    }

    public long calculateOptimal() {
        var ref = new Object() {
            long optimal = Long.MIN_VALUE;
        };

        CollectionUtils.permutations(happiness.rowKeySet()).forEach(permutation -> {
            long total = 0;
            for (int i = 0; i < permutation.size(); ++i) {
                total += happiness.get(permutation.get(i), permutation.get((i + 1) % permutation.size()));
                total += happiness.get(permutation.get((i + 1) % permutation.size()), permutation.get(i));
            }
            ref.optimal = Long.max(total, ref.optimal);
        });

        return ref.optimal;
    }

    public String runSolutionA(String input) {
        init(input);
        return "%,d".formatted(calculateOptimal());
    }

    public String runSolutionB(String input) {
        init(input);

        var people = happiness.rowKeySet().stream().toList();
        people.forEach(person -> {
            happiness.put(person, "I", 0L);
            happiness.put("I", person, 0L);
        });

        return "%,d".formatted(calculateOptimal());
    }

}
