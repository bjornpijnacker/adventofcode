package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

@SolutionDay(year = 2015, day = 3)
@Slf4j
public class Day3 extends AdventOfCodeSolution {
    public Day3() {
        super(3, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var seen = new HashSet<Vector2D>();
        final Vector2D[] position = {new Vector2D(0, 0)};
        seen.add(position[0]);

        input.chars().forEach(c -> {
            switch (c) {
                case '^' -> position[0] = position[0].add(new Vector2D(0, 1));
                case 'v' -> position[0] = position[0].add(new Vector2D(0, -1));
                case '>' -> position[0] = position[0].add(new Vector2D(1, 0));
                case '<' -> position[0] = position[0].add(new Vector2D(-1, 0));
            }
            seen.add(position[0]);
        });

        return "%,d".formatted(seen.size());
    }

    public String runSolutionB(String input) {
        var seen = new HashSet<Vector2D>();
        final Vector2D[] position = {new Vector2D(0, 0), new Vector2D(0, 0)};
        AtomicInteger i = new AtomicInteger();
        seen.add(position[0]);

        input.chars().forEach(c -> {
            i.set(i.incrementAndGet() % 2);
            switch (c) {
                case '^' -> position[i.get()] = position[i.get()].add(new Vector2D(0, 1));
                case 'v' -> position[i.get()] = position[i.get()].add(new Vector2D(0, -1));
                case '>' -> position[i.get()] = position[i.get()].add(new Vector2D(1, 0));
                case '<' -> position[i.get()] = position[i.get()].add(new Vector2D(-1, 0));
            }
            seen.add(position[i.get()]);
        });

        return "%,d".formatted(seen.size());
    }
}
