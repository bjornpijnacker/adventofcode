package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SolutionDay(year = 2024, day = 11)
@Slf4j
public class Day11 extends AdventOfCodeSolution {
    Map<Pair<Long, Integer>, Long> cache = new HashMap<>();

    public Day11(int day, int year) {
        super(day, year);

        register("a", input -> runSolution(input, 25));
        register("b", input -> runSolution(input, 75));
    }

    protected String runSolution(String input, int n) {
        var stones = Arrays.stream(input.split(" ")).mapToLong(Long::parseLong).toArray();

        long sum = 0;
        for (var stone : stones) {
            sum += blink(stone, n);
        }

        return "%,d".formatted(sum);
    }

    private long blink(long stone, int n) {
        var cacheKey = Pair.of(stone, n);
        if (cache.containsKey(cacheKey)) return cache.get(cacheKey);

        if (n == 0) {
            cache.put(cacheKey, 1L);
            return 1;
        }

        if (stone == 0) {
            var res = blink(1, n - 1);
            cache.put(cacheKey, res);
            return res;
        }

        var nDigits = (long) Math.floor(Math.log10(stone) + 1);
        if (nDigits > 0 && nDigits % 2 == 0) {
            long l = stone / (long) Math.pow(10, nDigits / 2);
            long r = stone % (long) Math.pow(10, nDigits / 2);
            var res = blink(l, n - 1) + blink(r, n - 1);
            cache.put(cacheKey, res);
            return res;
        }

        var res = blink(stone * 2024, n - 1);
        cache.put(cacheKey, res);
        return res;
    }
}
