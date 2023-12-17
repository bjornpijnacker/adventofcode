package com.bjornp.aoc2023.solutions.implementations;

import com.bjornp.aoc2023.annotation.SolutionDay;
import com.bjornp.aoc2023.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

@SolutionDay(day = 10)
@Slf4j
public class Day10 extends AdventOfCodeSolution {


    public Day10() {
        super(10);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }

    private String runSolutionA(String input) {
        var grid = new Grid(Arrays.stream(input.split("\n")).map(i -> i.split("")).toArray(String[][]::new));
        grid.calculateLoop();
        return "%,d".formatted(grid.getLoopLength() / 2);
    }

    private String runSolutionB(String input) {
        var grid = new Grid(Arrays.stream(input.split("\n")).map(i -> i.split("")).toArray(String[][]::new));
        grid.growGrid();
        grid.calculateLoop();
        grid.flood();
        grid.shrinkGrid();
        return "%,d".formatted(grid.countInside());
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class Coordinate {
        @EqualsAndHashCode.Include
        private final int x;
        @EqualsAndHashCode.Include
        private final int y;

        public int getIndex(int width) {
            return x + y * width;
        }

        public Coordinate left() {
            return new Coordinate(x - 1, y);
        }

        public Coordinate right() {
            return new Coordinate(x + 1, y);
        }

        public Coordinate above() {
            return new Coordinate(x, y - 1);
        }

        public Coordinate below() {
            return new Coordinate(x, y + 1);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }

    private static class Grid {
        private final char pathIndicator = 'X';
        private final char insideIncidator = 'i';
        private final char outsideIndicator = 'o';

        private boolean loopCalculated = false;
        private boolean regionsCalculated = false;

        private final Map<Character, List<List<Character>>> doubleReplacements = new HashMap<>() {{
            put('.', List.of(List.of('.', '.', '.'), List.of('.', '.', '.'), List.of('.', '.', '.')));
            put('|', List.of(List.of('.', '|', '.'), List.of('.', '|', '.'), List.of('.', '|', '.')));
            put('-', List.of(List.of('.', '.', '.'), List.of('-', '-', '-'), List.of('.', '.', '.')));
            put('L', List.of(List.of('.', '|', '.'), List.of('.', 'L', '-'), List.of('.', '.', '.')));
            put('J', List.of(List.of('.', '|', '.'), List.of('-', 'J', '.'), List.of('.', '.', '.')));
            put('7', List.of(List.of('.', '.', '.'), List.of('-', '7', '.'), List.of('.', '|', '.')));
            put('F', List.of(List.of('.', '.', '.'), List.of('.', 'F', '-'), List.of('.', '|', '.')));
            put('S', List.of(List.of('.', '|', '.'), List.of('-', 'S', '-'), List.of('.', '|', '.')));
        }};

        private List<Character> grid = new ArrayList<>();
        private int width;
        private int height;

        private Coordinate fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new Coordinate(x, y);
        }

        public Grid(String[][] grid) {
            this.width = grid[0].length;
            this.height = grid.length;
            for (String[] row : grid) {
                var chars = Arrays.stream(row).map(ch -> ch.charAt(0)).toList();
                this.grid.addAll(chars);
            }
        }

        public void growGrid() {
            List<Character> bigGrid = new ArrayList<>(width * height * 9);
            for (int y = 0; y < height * 3; ++y) {
                for (int x = 0; x < width * 3; ++x) {
                    bigGrid.add(' ');
                }
            }

            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    var replacement = this.doubleReplacements.get(sampleGrid(new Coordinate(x, y)));
                    for (int dx = 0; dx < 3; dx++) {
                        for (int dy = 0; dy < 3; dy ++) {
                            bigGrid.set((y * 3 + dy) * width * 3 + (x * 3 + dx), replacement.get(dy).get(dx));
                        }
                    }
                }
            }

            this.grid = bigGrid;
            this.width *= 3;
            this.height *= 3;

            // handle specific S case
            var start = this.getStart();
            if (!List.of('L', 'F', '-').contains(sampleGrid(start.left().left()))) {
                bigGrid.set(start.left().getIndex(this.width), '.');
            }
            if (!List.of('7', 'J', '-').contains(sampleGrid(start.right().right()))) {
                bigGrid.set(start.right().getIndex(this.width), '.');
            }
            if (!List.of('L', 'J', '|').contains(sampleGrid(start.below().below()))) {
                bigGrid.set(start.below().getIndex(this.width), '.');
            }
            if (!List.of('7', 'F', '|').contains(sampleGrid(start.above().above()))) {
                bigGrid.set(start.above().getIndex(this.width), '.');
            }
        }

        public void shrinkGrid() {
            List<Character> smallGrid = new ArrayList<>((width / 3) * (height * 3));

            for (int y = 1; y < height; y+=3) {
                for (int x = 1; x < width; x+=3) {
                    smallGrid.add(sampleGrid(new Coordinate(x, y)));
                }
            }

            this.width /= 3;
            this.height /= 3;
            this.grid = smallGrid;
        }

        public long countInside() {
            if (!this.regionsCalculated) {
                throw new RuntimeException("Cannot get inside num without first calculating flood");
            }
            return grid.stream().filter(character -> character == this.insideIncidator).count();
        }

        public Character sampleGrid(Coordinate coordinate) {
            return grid.get(coordinate.getIndex(width));
        }

        public Coordinate getStart() {
            var index = grid.indexOf('S');
            if (index == -1) {
                throw new RuntimeException("No start found in grid!");
            }
            return this.fromIndex(index);
        }

        public void calculateLoop() {
            var loopStart = this.getStart();
            var path = new HashSet<Coordinate>();

            Coordinate previous = null;
            Coordinate current = loopStart;

            // get initial step
            path.add(loopStart);
            if (current.y > 0 && List.of('|', '7', 'F').contains(sampleGrid(current.above()))) {
                current = current.above();
            } else if (current.y < width && List.of('|', 'L', 'J').contains(sampleGrid(current.below()))) {
                current = current.below();
            } else if (current.x > 0 && List.of('-', 'L', 'F').contains(sampleGrid(current.left()))) {
                current = current.left();
            } else if (current.x < height && List.of('-', '7', 'J').contains(sampleGrid(current.right()))) {
                current = current.right();
            }
            path.add(current);

            previous = loopStart;

            while (sampleGrid(current) != 'S') {
                var next = getStep(current, previous);
                path.add(next);
                previous = current;
                current = next;
            }

            // replace by 'X' in grid
            path.forEach(p -> grid.set(p.getIndex(width), this.pathIndicator));

            this.loopCalculated = true;
        }

        public void flood() {
            if (this.regionsCalculated) {
                throw new RuntimeException("Cannot calculate regions twice");
            }
            var seen = new HashSet<Coordinate>();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    boolean outside = false;
                    var thisFlood = new HashSet<Coordinate>();
                    if (seen.contains(new Coordinate(x, y))) continue;

                    var queue = new LinkedList<Coordinate>();
                    queue.push(new Coordinate(x, y));

                    while (!queue.isEmpty()) {
                        var current = queue.pop();
                        seen.add(current);
                        if (sampleGrid(current) == this.pathIndicator) continue;
                        thisFlood.add(current);

                        if (current.x == 0 || current.x == width - 1 || current.y == 0 || current.y == height - 1) {
                            outside = true;
                        }

                        if (current.x > 0 && !seen.contains(current.left())) queue.push(current.left());
                        if (current.x < width - 1 && !seen.contains(current.right())) queue.push(current.right());
                        if (current.y > 0 && !seen.contains(current.above())) queue.push(current.above());
                        if (current.y < height - 1 && !seen.contains(current.below())) queue.push(current.below());
                    }

                    // complete flood is done
                    boolean finalOutside = outside;
                    thisFlood.forEach(coordinate -> {
                        grid.set(coordinate.getIndex(width), finalOutside ? this.outsideIndicator : this.insideIncidator);
                    });
                }
            }

            this.regionsCalculated = true;
        }

        private long getLoopLength() {
            if (!this.loopCalculated) {
                throw new RuntimeException("Cannot get loop length before its calculation");
            }
            return grid.stream().filter(character -> character == this.pathIndicator).count();
        }

        private Coordinate getStep(Coordinate current, Coordinate previous) {
            return switch (sampleGrid(current)) {
                case '-' -> previous.x < current.x ? current.right() : current.left();
                case '|' -> previous.y < current.y ? current.below() : current.above();
                case 'L' -> current.x == previous.x ? current.right() : current.above();
                case 'J' -> current.x == previous.x ? current.left() : current.above();
                case '7' -> current.x == previous.x ? current.left() : current.below();
                case 'F' -> current.x == previous.x ? current.right() : current.below();
                default -> throw new IllegalArgumentException("Unknown character: " + sampleGrid(current));
            };
        }

        private void log() {
            ListUtils.partition(this.grid, this.width).forEach(row -> {
                log.debug(String.valueOf(row.stream().map(Object::toString).collect(Collectors.joining(" "))));
            });
        }
    }
}
