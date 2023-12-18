package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(day = 17)
@Slf4j
public class Day17 extends AdventOfCodeSolution {
    private static boolean isPart2 = false;

    public Day17() {
        super(17);

        register("a", this::runSolutionA);
        register("b", input -> {
            isPart2 = true;
            return runSolutionA(input);
        });
    }

    public String runSolutionA(String input) {
        var grid = new Grid(input);
        var dists = new HashMap<SearchUnit, Integer>();
        var prev = new HashMap<SearchUnit, SearchUnit>();
        var q = new PriorityQueue<PrioritySearchUnit>();

        dists.put(new SearchUnit(new IntVector2D(0, 0), new IntVector2D(0, 1), 0), 0);
        q.add(new PrioritySearchUnit(new SearchUnit(new IntVector2D(0, 0), new IntVector2D(0, 1), 0), 0));
        dists.put(new SearchUnit(new IntVector2D(0, 0), new IntVector2D(1, 0), 0), 0);
        q.add(new PrioritySearchUnit(new SearchUnit(new IntVector2D(0, 0), new IntVector2D(1, 0), 0), 0));

        while (!q.isEmpty()) {
            var u = q.poll();

            if (Objects.equals(u.searchUnit.position, new IntVector2D(grid.width - 1, grid.height - 1))) {
                break;
            }

            for (var v : u.searchUnit.getNeighbors()) {
                if (grid.inBounds(v.position)) {
                    int alt = dists.get(u.searchUnit) + grid.sampleGrid(u.searchUnit.getPosition(), v.position);
                    if (alt < dists.getOrDefault(v, Integer.MAX_VALUE)) {
                        dists.put(v, alt);
                        prev.put(v, u.searchUnit);
                        var newN = new PrioritySearchUnit(new SearchUnit(v.position, v.direction, v.length), alt);
                        q.add(newN);
                    }
                }
            }
        }

        var target = new IntVector2D(grid.width - 1, grid.height - 1);
        var end = dists.entrySet()
                .stream()
                .filter(entry -> entry.getKey().position.equals(target))
                .min(Comparator.comparingInt(Map.Entry::getValue));
//        grid.visualize(end.get().getKey(), prev);

        return "%s".formatted(end.get().getValue());
    }

    public String runSolutionB(String input) {
        return "";
    }

    @AllArgsConstructor
    @Data
    @EqualsAndHashCode
    private static class SearchUnit {
        private final IntVector2D position;

        private final IntVector2D direction;

        private final int length;

        public List<SearchUnit> getNeighbors() {
            var neighbors = new ArrayList<SearchUnit>();

            if (isPart2) {
                if (length < 3) {
                    neighbors.add(this.forward(4 - length));
                }
                if (length >= 3) {
                    if (length < 10) {
                        neighbors.add(this.forward(1));
                    }
                    neighbors.add(this.right());
                    neighbors.add(this.left());
                }
            } else {
                if (length < 3) {
                    neighbors.add(this.forward(1));
                }
                neighbors.add(this.right());
                neighbors.add(this.left());
            }
            return neighbors;

        }

        public SearchUnit forward(int factor) {
            return new SearchUnit(position.add(direction.mult(factor)), direction, length + factor);
        }

        public SearchUnit right() {
            return new SearchUnit(
                    position.add(new IntVector2D(direction.y, direction.x)),
                    new IntVector2D(direction.y, direction.x),
                    1
            );
        }

        public SearchUnit left() {
            return new SearchUnit(
                    position.add(new IntVector2D(-direction.y, -direction.x)),
                    new IntVector2D(-direction.y, -direction.x),
                    1
            );
        }

        public String toDirectionString() {
            if (direction.x == 0) {
                return direction.y == 1 ? "v" : "^";
            } else {
                return direction.x == 1 ? ">" : "<";
            }
        }

    }

    @AllArgsConstructor
    public static class PrioritySearchUnit implements Comparable<PrioritySearchUnit> {
        private final SearchUnit searchUnit;

        private final int priority;

        @Override
        public int compareTo(PrioritySearchUnit o) {
            return Integer.compare(priority, o.priority);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class IntVector2D {
        @EqualsAndHashCode.Include
        private int x;

        @EqualsAndHashCode.Include
        private int y;

        public IntVector2D mult(int factor) {
            return new IntVector2D(x * factor, y * factor);
        }

        public int getIndex(int width) {
            return x + y * width;
        }

        public IntVector2D add(IntVector2D other) {
            return new IntVector2D(x + other.x, y + other.y);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }

    @Slf4j
    private static class Grid {
        private final List<Integer> grid = new ArrayList<>();

        private final int width;

        private final int height;

        public Grid(String grid) {
            this.width = grid.split("\n")[0].length();
            this.height = grid.split("\n").length;
            for (String row : grid.split("\n")) {
                for (String cell : row.split("")) {
                    this.grid.add(Integer.parseInt(cell));
                }
            }
        }

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

        public void visualize(SearchUnit end, HashMap<SearchUnit, SearchUnit> prev) {
            var stringGrid = new ArrayList<String>();
            grid.forEach(integer -> stringGrid.add("."));

            var current = end;
            while (prev.containsKey(current)) {
                stringGrid.set(current.position.getIndex(width), current.toDirectionString());
                current = prev.get(current);
            }
            for (List<String> row : ListUtils.partition(stringGrid, width)) {
                log.debug(String.join("", row));
            }
        }

        public Integer sampleGrid(IntVector2D start, IntVector2D end) {
            if (start.x == end.x) {
                if (start.y < end.y) {
                    var sum = 0;
                    for (int i = start.y + 1; i <= end.y; ++i) {
                        sum += sampleGrid(new IntVector2D(start.x, i));
                    }
                    return sum;
                } else if (start.y > end.y) {
                    var sum = 0;
                    for (int i = start.y - 1; i >= end.y; --i) {
                        sum += sampleGrid(new IntVector2D(start.x, i));
                    }
                    return sum;
                } else {
                    return 0;
                }
            } else if (start.y == end.y) {
                if (start.x < end.x) {
                    var sum = 0;
                    for (int i = start.x + 1; i <= end.x; ++i) {
                        sum += sampleGrid(new IntVector2D(i, start.y));
                    }
                    return sum;
                } else {
                    var sum = 0;
                    for (int i = start.x - 1; i >= end.x; --i) {
                        sum += sampleGrid(new IntVector2D(i, start.y));
                    }
                    return sum;
                }
            } else {
                throw new IllegalArgumentException("Not a straight line");
            }
        }

        public Integer sampleGrid(IntVector2D coordinate) {
            return grid.get(coordinate.getIndex(width));
        }

        public boolean inBounds(IntVector2D coordinate) {
            return coordinate.x >= 0 && coordinate.x < width && coordinate.y >= 0 && coordinate.y < height;
        }


        private void log() {
            ListUtils.partition(this.grid, this.width).forEach(row -> {
                log.debug(String.valueOf(row.stream().map(Object::toString).collect(Collectors.joining(" "))));
            });
        }
    }
}
