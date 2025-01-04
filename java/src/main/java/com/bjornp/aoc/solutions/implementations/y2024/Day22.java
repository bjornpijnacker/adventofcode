package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.common.collect.MinMaxPriorityQueue;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SolutionDay(year = 2024, day = 22)
@Slf4j
public class Day22 extends AdventOfCodeSolution {

    public Day22(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        log.info(String.valueOf(input.length()));
        var result = input.lines().mapToLong(line -> {
            AtomicLong secret = new AtomicLong(Long.parseLong(line));
            IntStream.range(0, 2000).forEach(iter -> secret.set(next(secret.get())));
//            log.info(String.valueOf(secret));
            return secret.get();
        }).sum();

        return "%d".formatted(result);
    }

    protected String runSolutionB(String input) {
        List<Map<List<Long>, Long>> monkeySequences = input.lines().map(line -> {
            Map<List<Long>, Long> sequence = new HashMap<>();

            long secret = Long.parseLong(line);
            ArrayList<Long> previous = new ArrayList<>();
            for (int i = 0; i < 2000; ++i) {
                var next = next(secret);

                if (previous.size() == 5) {
                    // store the sequence
                    var seq = previous.stream().toList();
                    var dif = new ArrayList<Long>();

                    for (int j = 1; j < seq.size(); ++j) {
                        dif.add((seq.get(j) % 10) - (seq.get(j - 1) % 10));
                    }

                    if (!sequence.containsKey(dif)) {
                        sequence.put(dif, seq.getLast() % 10);
                    }
                }

                previous.add(next);
                if (previous.size() > 5) previous.removeFirst();
                secret = next;
            }
            return sequence;
        }).toList();

        var bestSequence = monkeySequences
                .parallelStream()
                .flatMap(monkeySequence -> monkeySequence.keySet().stream())
                .distinct()
                .max(Comparator.comparingLong(sequence -> monkeySequences.stream()
                        .mapToLong(monkey -> monkey.getOrDefault(sequence, 0L))
                        .sum())
                ).orElseThrow();

        var totalPrice = monkeySequences.stream()
                .mapToLong(monkey -> monkey.getOrDefault(bestSequence, 0L))
                .sum();

        log.info(String.valueOf(bestSequence));

        return "%d".formatted(totalPrice);
    }

    private long next(long num) {
        var secret = num;

        var mul = secret * 64;
        secret = mix(secret, mul);
        secret = prune(secret);

        var div = secret / 32;
        secret = mix(secret, div);
        secret = prune(secret);

        var twth = secret * 2048;
        secret = mix(secret, twth);
        secret = prune(secret);

        return secret;
    }

    private long prune(long secret) {
        return secret % 16777216;
    }

    private long mix(long secret, long num) {
        return secret ^ num;
    }
}
