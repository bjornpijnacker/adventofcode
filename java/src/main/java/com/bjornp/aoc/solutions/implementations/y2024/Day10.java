package com.bjornp.aoc.solutions.implementations.y2024;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.bjornp.aoc.util.Grid2D;
import com.bjornp.aoc.util.IntVector2D;
import com.sun.jdi.IntegerValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.jgrapht.alg.util.Pair;

import java.util.*;

@SolutionDay(year = 2024, day = 10)
@Slf4j
public class Day10 extends AdventOfCodeSolution {

    public Day10(int day, int year) {
        super(day, year);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    protected String runSolutionA(String input) {
        var grid = new Grid2D<Integer>(input
                .lines()
                .map(line -> Arrays.stream(line.split("")).map(Integer::valueOf).toArray(Integer[]::new))
                .toArray(Integer[][]::new));
        var nines = new HashMap<IntVector2D, Set<IntVector2D>>();

        for (var trailhead : grid.findAll(0)) {
            var toSearch = new ArrayDeque<Triple<IntVector2D, IntVector2D, Integer>>();

            if (grid.inBounds(trailhead.n()) && grid.get(trailhead.n()) == 1) {
                toSearch.add(Triple.of(trailhead, trailhead.n(), 1));
            }

            if (grid.inBounds(trailhead.e()) && grid.get(trailhead.e()) == 1) {
                toSearch.add(Triple.of(trailhead, trailhead.e(), 1));
            }

            if (grid.inBounds(trailhead.s()) && grid.get(trailhead.s()) == 1) {
                toSearch.add(Triple.of(trailhead, trailhead.s(), 1));
            }

            if (grid.inBounds(trailhead.w()) && grid.get(trailhead.w()) == 1) {
                toSearch.add(Triple.of(trailhead, trailhead.w(), 1));
            }

            while (!toSearch.isEmpty()) {
                var head = toSearch.pop();

                var source = head.getLeft();
                var coord = head.getMiddle();
                var num = head.getRight();

                if (!grid.inBounds(coord)) continue;

                if (grid.get(coord) == 9) {
                    if (nines.containsKey(source)) {
                        nines.get(source).add(coord);
                    } else {
                        nines.put(source, new HashSet<>(List.of(coord)));
                    }
                    continue;
                }

                if (grid.inBounds(coord.n()) && grid.get(coord.n()) == num + 1) {
                    toSearch.add(Triple.of(source, coord.n(), num + 1));
                }

                if (grid.inBounds(coord.w()) && grid.get(coord.w()) == num + 1) {
                    toSearch.add(Triple.of(source, coord.w(), num + 1));
                }

                if (grid.inBounds(coord.s()) && grid.get(coord.s()) == num + 1) {
                    toSearch.add(Triple.of(source, coord.s(), num + 1));
                }

                if (grid.inBounds(coord.e()) && grid.get(coord.e()) == num + 1) {
                    toSearch.add(Triple.of(source, coord.e(), num + 1));
                }
            }
        }

        var score = nines.values().stream().mapToLong(Set::size).sum();
        return "%,d".formatted(score);
    }

    protected String runSolutionB(String input) {
        var grid = new Grid2D<Integer>(input
                .lines()
                .map(line -> Arrays.stream(line.split("")).map(Integer::valueOf).toArray(Integer[]::new))
                .toArray(Integer[][]::new));
        var nines = new HashMap<IntVector2D, Set<List<IntVector2D>>>();

        for (var trailhead : grid.findAll(0)) {
            var toSearch = new ArrayDeque<Triple<List<IntVector2D>, IntVector2D, Integer>>();

            if (grid.inBounds(trailhead.n()) && grid.get(trailhead.n()) == 1) {
                toSearch.add(Triple.of(List.of(trailhead, trailhead.n()), trailhead.n(), 1));
            }

            if (grid.inBounds(trailhead.e()) && grid.get(trailhead.e()) == 1) {
                toSearch.add(Triple.of(List.of(trailhead, trailhead.e()), trailhead.e(), 1));
            }

            if (grid.inBounds(trailhead.s()) && grid.get(trailhead.s()) == 1) {
                toSearch.add(Triple.of(List.of(trailhead, trailhead.s()), trailhead.s(), 1));
            }

            if (grid.inBounds(trailhead.w()) && grid.get(trailhead.w()) == 1) {
                toSearch.add(Triple.of(List.of(trailhead, trailhead.w()), trailhead.w(), 1));
            }

            while (!toSearch.isEmpty()) {
                var head = toSearch.pop();

                var path = head.getLeft();
                var coord = head.getMiddle();
                var num = head.getRight();

                if (!grid.inBounds(coord)) continue;

                if (grid.get(coord) == 9) {
                    if (nines.containsKey(path.getFirst())) {
                        nines.get(path.getFirst()).add(path);
                    } else {
                        Set<List<IntVector2D>> set = new HashSet<>();
                        set.add(path);
                        nines.put(path.getFirst(), set);
                    }
                    continue;
                }

                if (grid.inBounds(coord.n()) && grid.get(coord.n()) == num + 1) {
                    var newPath = new ArrayList<>(path);
                    newPath.add(coord.n());
                    toSearch.add(Triple.of(newPath, coord.n(), num + 1));
                }

                if (grid.inBounds(coord.w()) && grid.get(coord.w()) == num + 1) {
                    var newPath = new ArrayList<>(path);
                    newPath.add(coord.w());
                    toSearch.add(Triple.of(newPath, coord.w(), num + 1));
                }

                if (grid.inBounds(coord.s()) && grid.get(coord.s()) == num + 1) {
                    var newPath = new ArrayList<>(path);
                    newPath.add(coord.s());
                    toSearch.add(Triple.of(newPath, coord.s(), num + 1));
                }

                if (grid.inBounds(coord.e()) && grid.get(coord.e()) == num + 1) {
                    var newPath = new ArrayList<>(path);
                    newPath.add(coord.e());
                    toSearch.add(Triple.of(newPath, coord.e(), num + 1));
                }
            }
        }

        var score = nines.values().stream().mapToLong(Set::size).sum();
        return "%,d".formatted(score);
    }
}
