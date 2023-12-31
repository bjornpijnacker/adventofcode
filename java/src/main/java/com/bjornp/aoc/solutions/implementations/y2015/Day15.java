package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

@SolutionDay(year = 2015, day = 15)
@Slf4j
public class Day15 extends AdventOfCodeSolution {
    private final static Pattern INGREDIENT = Pattern.compile(
            "^[A-Z][a-z]+: capacity (?<cap>-?\\d+), durability (?<dur>-?\\d+), flavor (?<flav>-?\\d+), texture (?<tex>-?\\d+), calories (?<cal>-?\\d+)$");

    public Day15() {
        super(15, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
        register("test", (input) -> {
            log.info("{}", nkPartition(100, 4));
            return "";
        });
    }


    public String runSolutionA(String input) {
        var ingredients = input.lines().map(ing -> {
            var m = INGREDIENT.matcher(ing);
            if (m.matches()) {
                return new Ingredient(
                        Long.parseLong(m.group("cap")),
                        Long.parseLong(m.group("dur")),
                        Long.parseLong(m.group("flav")),
                        Long.parseLong(m.group("tex")),
                        Long.parseLong(m.group("cal"))
                );
            }
            throw new IllegalArgumentException("Invalid: " + ing);
        }).toList();

        long max = nkPartition(100, ingredients.size()).stream().mapToLong(list -> {
            long capacity = 0;
            long durability = 0;
            long flavor = 0;
            long texture = 0;
            for (int i = 0; i < list.size(); ++i) {
                capacity += ingredients.get(i).getCapacity() * list.get(i);
                durability += ingredients.get(i).getDurability() * list.get(i);
                flavor += ingredients.get(i).getFlavor() * list.get(i);
                texture += ingredients.get(i).getTexture() * list.get(i);
            }
            return Long.max(0, capacity) * Long.max(0, durability) * Long.max(0, flavor) * Long.max(0, texture);
        }).max().getAsLong();

        return "%,d".formatted(max);
    }

    public String runSolutionB(String input) {
        var ingredients = input.lines().map(ing -> {
            var m = INGREDIENT.matcher(ing);
            if (m.matches()) {
                return new Ingredient(
                        Long.parseLong(m.group("cap")),
                        Long.parseLong(m.group("dur")),
                        Long.parseLong(m.group("flav")),
                        Long.parseLong(m.group("tex")),
                        Long.parseLong(m.group("cal"))
                );
            }
            throw new IllegalArgumentException("Invalid: " + ing);
        }).toList();

        long max = nkPartition(100, ingredients.size()).stream().mapToLong(list -> {
            long capacity = 0;
            long durability = 0;
            long flavor = 0;
            long texture = 0;
            long calories = 0;
            for (int i = 0; i < list.size(); ++i) {
                capacity += ingredients.get(i).getCapacity() * list.get(i);
                durability += ingredients.get(i).getDurability() * list.get(i);
                flavor += ingredients.get(i).getFlavor() * list.get(i);
                texture += ingredients.get(i).getTexture() * list.get(i);
                calories += ingredients.get(i).getCalories() * list.get(i);
            }
            return calories == 500 ?
                   Long.max(0, capacity) * Long.max(0, durability) * Long.max(0, flavor) * Long.max(0, texture)
                   : 0;
        }).max().getAsLong();

        return "%,d".formatted(max);
    }

    public HashSet<List<Integer>> nkPartition(int n, int k) {
        if (k == 0) {
            return new HashSet<>();
        }

        if (k == 1) {
            var res = new HashSet<List<Integer>>();
            res.add(new ArrayList<>(List.of(n)));
            return res;
        }

        var options = new HashSet<List<Integer>>();
        for (int i = 0; i <= n; ++i) {
            var set = nkPartition(n - i, k - 1);
            int finalI = i;
            set.forEach(list -> list.add(finalI));
            options.addAll(set);
        }
        return options;
    }

    @Data
    @RequiredArgsConstructor
    private static class Ingredient {
        private final long capacity;

        private final long durability;

        private final long flavor;

        private final long texture;

        private final long calories;
    }

}
