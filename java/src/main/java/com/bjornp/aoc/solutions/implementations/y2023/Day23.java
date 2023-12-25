package com.bjornp.aoc.solutions.implementations.y2023;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import com.google.common.collect.HashBasedTable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(year = 2023, day = 23)
@Slf4j
public class Day23 extends AdventOfCodeSolution {
    public Day23() {
        super(23, 2023);

        register("a", this::runSolutionA);
        register("a-alt", this::runSolutionAAlt);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var grid = new Grid(input);

        return "%,d".formatted(longestPath(grid.fromIndex(grid.grid.indexOf('.')),
                grid.fromIndex(grid.grid.lastIndexOf('.')),
                grid,
                new HashSet<>()
        ));
    }

    private String runSolutionAAlt(String input) {
        var grid = new Grid(input);

        grid.populateAdjacencyMatrix(true);

        log.info("{} nodes to explore", grid.adjacencyMatrix.rowKeySet().size());
        log.info("Target: {}", grid.fromIndex(grid.grid.lastIndexOf('.')));

        return "%,d".formatted(longestPathAdjMatrix(grid.fromIndex(grid.grid.indexOf('.')),
                grid.fromIndex(grid.grid.lastIndexOf('.')),
                grid
        ));
    }

    private String runSolutionB(String input) {
        var grid = new Grid(input);

        grid.populateAdjacencyMatrix(false);

        log.info("{} nodes to explore", grid.adjacencyMatrix.rowKeySet().size());
        log.info("Target: {}", grid.fromIndex(grid.grid.lastIndexOf('.')));

        return "%,d".formatted(longestPathAdjMatrix(grid.fromIndex(grid.grid.indexOf('.')),
                grid.fromIndex(grid.grid.lastIndexOf('.')),
                grid
        ));
    }

    private long longestPath(IntVector2D pos, IntVector2D target, Grid grid, HashSet<IntVector2D> visited) {
        if (visited.contains(pos)) {
            return Long.MIN_VALUE;
        }
        if (pos.equals(target)) {
            return 0;
        }
        HashSet<IntVector2D> newVis = SerializationUtils.clone(visited);
        newVis.add(pos);

        if (grid.sampleGrid(pos) == '.') {
            var neighbors = pos.neighbors()
                    .stream()
                    .filter(grid::inBounds)
                    .filter(neighbor -> grid.sampleGrid(neighbor) != '#')
                    .filter(neighbor -> !visited.contains(neighbor))
                    .toList();
            if (neighbors.isEmpty()) {
                return Long.MIN_VALUE;
            }
            return 1 + neighbors.stream()
                    .mapToLong(neighbor -> longestPath(neighbor, target, grid, newVis))
                    .max()
                    .orElseThrow();
        } else if (grid.sampleGrid(pos) == '>') {
            var neighbor = pos.right();
            if (grid.inBounds(neighbor) && grid.sampleGrid(neighbor) != '#') {
                return 1 + longestPath(neighbor, target, grid, newVis);
            }
        } else if (grid.sampleGrid(pos) == '<') {
            var neighbor = pos.left();
            if (grid.inBounds(neighbor) && grid.sampleGrid(neighbor) != '#') {
                return 1 + longestPath(neighbor, target, grid, newVis);
            }
        } else if (grid.sampleGrid(pos) == '^') {
            var neighbor = pos.above();
            if (grid.inBounds(neighbor) && grid.sampleGrid(neighbor) != '#') {
                return 1 + longestPath(neighbor, target, grid, newVis);
            }
        } else if (grid.sampleGrid(pos) == 'v') {
            var neighbor = pos.below();
            if (grid.inBounds(neighbor) && grid.sampleGrid(neighbor) != '#') {
                return 1 + longestPath(neighbor, target, grid, newVis);
            }
        }
        throw new RuntimeException("Invalid position: " + pos);
    }

    private long longestPathAdjMatrix(IntVector2D start, IntVector2D target, Grid grid) {
        var stack = new Stack<Triple<IntVector2D, Long, HashSet<IntVector2D>>>();

        var seen = new HashSet<IntVector2D>();
        seen.add(start);

        var max = 0L;
        var count = 0L;

        stack.push(Triple.of(start, 0L, seen));

        while (!stack.isEmpty()) {
            count++;
            if (count % 1e5 == 0) {
                log.info("Count: %,d, Max: {}".formatted(count), max);
            }
            var current = stack.pop();
            var pos = current.getLeft();
            var dist = current.getMiddle();
            var visited = current.getRight();

            var newVisited = new HashSet<>(visited);
            newVisited.add(pos);

            if (pos.equals(target)) {
                if (max < dist) {
                    max = dist;
                }
            } else {
                for (var n : grid.adjacencyMatrix.row(pos).keySet()) {
                    if (!visited.contains(n)) {
                        stack.push(Triple.of(n, dist + grid.adjacencyMatrix.get(pos, n), newVisited));
                    }
                }
            }
        }

        return max;
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class IntVector2D implements Serializable, Comparable {
        @EqualsAndHashCode.Include
        private final int x;

        @EqualsAndHashCode.Include
        private final int y;

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

        @Override
        public int compareTo(Object o) {
            return Comparator.comparingInt(IntVector2D::getY)
                    .thenComparingInt(IntVector2D::getX)
                    .compare(this, (IntVector2D) o);
        }
    }

    @Slf4j
    private static class Grid {
        private final List<Character> grid = new ArrayList<>();

        private final int width;

        private final int height;

        private final HashBasedTable<IntVector2D, IntVector2D, Long> adjacencyMatrix = HashBasedTable.create();

        public Grid(String grid) {
            this.width = grid.split("\n")[0].length();
            var rows = grid.split("\n");
            this.height = rows.length;
            for (String row : rows) {
                var chars = Arrays.stream(row.split("")).map(ch -> ch.charAt(0)).toList();
                this.grid.addAll(chars);
            }
        }

        public void populateAdjacencyMatrix(boolean respectSlopes) {
            var starts = new HashSet<IntVector2D>();
            var q = new LinkedList<Pair<IntVector2D, IntVector2D>>();

            var start = fromIndex(this.grid.indexOf('.'));
            q.add(Pair.of(start, start.below()));

            while (!q.isEmpty()) {
                var current = q.poll();
                if (starts.contains(current.getRight())) {
                    continue;
                }
                starts.add(current.getRight());
                var next = findNextChoice(current.getRight(), current.getLeft(), respectSlopes);

                if (next != null) {
                    adjacencyMatrix.put(current.getLeft(), next.getLeft(), next.getRight());

                    var nextNeighbors = cleanedNeighbors(next.getLeft(), Set.of());
                    for (IntVector2D nextNeighbor : nextNeighbors) {
                        q.add(Pair.of(next.getLeft(), nextNeighbor));
                    }
                }
            }

            log.info(adjacencyMatrix.toString());
        }

        public List<IntVector2D> cleanedNeighbors(IntVector2D of, Set<IntVector2D> visited) {
            return of.neighbors()
                    .stream()
                    .filter(this::inBounds)
                    .filter(neighbor -> this.sampleGrid(neighbor) != '#')
                    .filter(neighbor -> !visited.contains(neighbor))
                    .toList();
        }

        private Pair<IntVector2D, Long> findNextChoice(IntVector2D start, IntVector2D previous, boolean respectSlopes) {
            var current = start;
            var visited = new HashSet<IntVector2D>();
            visited.add(previous);
            while (cleanedNeighbors(current, visited).size() == 1) {
                visited.add(current);
                var neighbors = cleanedNeighbors(current, visited);
                if (respectSlopes) {
                    if (sampleGrid(current) == '>' && !current.right().equals(neighbors.get(0))) {
                        return null;
                    } else if (sampleGrid(current) == '<' && !current.left().equals(neighbors.get(0))) {
                        return null;
                    } else if (sampleGrid(current) == '^' && !current.above().equals(neighbors.get(0))) {
                        return null;
                    } else if (sampleGrid(current) == 'v' && !current.below().equals(neighbors.get(0))) {
                        return null;
                    }
                }

                current = neighbors.get(0);
            }
            return Pair.of(current, (long) visited.size());
        }

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
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
}
