package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(year = 2023, day = 21)
@Slf4j
public class Day21 extends AdventOfCodeSolution {
    public Day21() {
        super(21, 2023);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    public String runSolutionA(String input) {
        var grid = new Grid(input);
        for (int i = 0; i < 64; ++i) {
            grid.step();
        }
        grid.visualize(grid.reachable);
        return "%,d".formatted(grid.reachable.size());
    }

    public String runSolutionB(String input) {
        var grid = new InfiniteGrid(input);

        var target = 26501365L;
        var xs = new ArrayList<Long>();
        for (int i = 0; ; ++i) {
            if (xs.size() >= 3) {
                break;
            }

            if (i % grid.width == target % grid.width) {
                log.info("f({}) = {}", i, grid.reachable.size());
                xs.add((long) grid.reachable.size());
            }
            grid.step();
        }

        return "%,d".formatted(f((target - 65) / 131, xs));
    }

    public long f(long n, List<Long> factors) {
        log.info("Interpolating [0, 1, 2] = {} at x = {}", factors, n);
        long b0 = factors.get(0);
        long b1 = factors.get(1) - factors.get(0);
        long b2 = factors.get(2) - factors.get(1);
        return b0 + b1 * n + (n * (n - 1) / 2) * (b2 - b1);
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class IntVector2D {
        @EqualsAndHashCode.Include
        private int x;

        @EqualsAndHashCode.Include
        private int y;

        public int getIndex(int width) {
            return x + y * width;
        }

        public IntVector2D add(IntVector2D other) {
            return new IntVector2D(x + other.x, y + other.y);
        }

        public List<IntVector2D> neighbors() {
            return List.of(left(), right(), above(), below());
        }

        public IntVector2D left() {
            return new IntVector2D(x - 1, y);
        }

        public IntVector2D right() {
            return new IntVector2D(x + 1, y);
        }

        public IntVector2D above() {
            return new IntVector2D(x, y - 1);
        }

        public IntVector2D below() {
            return new IntVector2D(x, y + 1);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }

    @Slf4j
    private static class Grid {
        private final List<Character> grid = new ArrayList<>();

        private final int width;

        private final int height;

        private Set<IntVector2D> reachable = new HashSet<>();

        public Grid(String grid) {
            this.width = grid.split("\n")[0].length();
            var rows = grid.split("\n");
            this.height = rows.length;
            for (String row : rows) {
                var chars = Arrays.stream(row.split("")).map(ch -> ch.charAt(0)).toList();
                this.grid.addAll(chars);
            }
            var start = fromIndex(this.grid.indexOf('S'));
            reachable.add(start);
            log.info("Width: {}, Height: {}, Start: {}", width, height, start);
        }

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

        public void step() {
            var newReachable = new HashSet<IntVector2D>();
            reachable.forEach(r -> {
                r.neighbors().forEach(n -> {
//                    log.info("Checking {}", n);
                    if (inBounds(n) && sampleGrid(n) != '#') {
                        newReachable.add(n);
                    }
                });
            });
            reachable = newReachable;
        }

        public boolean inBounds(IntVector2D coordinate) {
            return coordinate.x >= 0 && coordinate.x < width && coordinate.y >= 0 && coordinate.y < height;
        }

        public Character sampleGrid(IntVector2D coordinate) {
            return grid.get(coordinate.getIndex(width));
        }

        public void visualize(Set<IntVector2D> positions) {
            List<List<Character>> partition = ListUtils.partition(this.grid, this.width);
            for (int i = 0; i < partition.size(); i++) {
                var row = partition.get(i);
                for (int j = 0; j < row.size(); j++) {
                    var pos = new IntVector2D(j, i);
                    if (positions.contains(pos)) {
                        row.set(j, 'O');
                    }
                }
            }
            var logGrid = new Grid(partition.stream()
                    .map(row -> row.stream().map(Object::toString).collect(Collectors.joining()))
                    .collect(Collectors.joining("\n")));
            logGrid.log();
        }

        private void log() {
            ListUtils.partition(this.grid, this.width).forEach(row -> {
                log.debug(String.valueOf(row.stream().map(Object::toString).collect(Collectors.joining(" "))));
            });
        }
    }

    @Slf4j
    private static class InfiniteGrid {
        private final List<Character> grid = new ArrayList<>();

        private final int width;

        private final int height;

        private Set<Pair<IntVector2D, IntVector2D>> reachable = new HashSet<>();

        public InfiniteGrid(String grid) {
            this.width = grid.split("\n")[0].length();
            var rows = grid.split("\n");
            this.height = rows.length;
            for (String row : rows) {
                var chars = Arrays.stream(row.split("")).map(ch -> ch.charAt(0)).toList();
                this.grid.addAll(chars);
            }
            var start = fromIndex(this.grid.indexOf('S'));
            reachable.add(Pair.of(start, new IntVector2D(0, 0)));
            log.info("Width: {}, Height: {}, Start: {}", width, height, start);
        }

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

        public void step() {
            var newReachable = new HashSet<Pair<IntVector2D, IntVector2D>>();
            reachable.forEach(r -> {
                r.getLeft().neighbors().forEach(n -> {
                    var norm = this.norm(Pair.of(n, r.getRight()));
                    if (sampleGrid(norm.getLeft()) != '#') {
                        newReachable.add(norm);
                    }
                });
            });
            reachable = newReachable;
        }

        public Pair<IntVector2D, IntVector2D> norm(Pair<IntVector2D, IntVector2D> pair) {
            if (!inBounds(pair.getLeft())) {
                if (pair.getLeft().x >= width) {
                    return Pair.of(pair.getLeft().add(new IntVector2D(-width, 0)),
                            pair.getRight().add(new IntVector2D(1, 0))
                    );
                } else if (pair.getLeft().x < 0) {
                    return Pair.of(pair.getLeft().add(new IntVector2D(width, 0)),
                            pair.getRight().add(new IntVector2D(-1, 0))
                    );
                } else if (pair.getLeft().y >= height) {
                    return Pair.of(pair.getLeft().add(new IntVector2D(0, -height)),
                            pair.getRight().add(new IntVector2D(0, 1))
                    );
                } else if (pair.getLeft().y < 0) {
                    return Pair.of(pair.getLeft().add(new IntVector2D(0, height)),
                            pair.getRight().add(new IntVector2D(0, -1))
                    );
                }
            } else {
                return pair;
            }
            throw new RuntimeException("Should not happen");
        }

        public Character sampleGrid(IntVector2D coordinate) {
            return grid.get(coordinate.getIndex(width));
        }

        public boolean inBounds(IntVector2D coordinate) {
            return coordinate.x >= 0 && coordinate.x < width && coordinate.y >= 0 && coordinate.y < height;
        }
    }
}
