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
import java.util.function.Function;
import java.util.stream.Collectors;

@SolutionDay(day = 16)
@Slf4j
public class Day16 extends AdventOfCodeSolution {
    public Day16() {
        super(16);
        
        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }
    
    public String runSolutionA(String input) {
        var grid = new Grid(Arrays.stream(input.split("\n")).map(row -> row.split("")).toArray(String[][]::new));
        var simulation = new BeanSimulation(grid, new Beam(new IntVector2D(-1, 0), new IntVector2D(1, 0)));
        simulation.run();

        return "%,d".formatted(simulation.getNumEnergized());
    }

    public String runSolutionB(String input) {
        var grid = new Grid(Arrays.stream(input.split("\n")).map(row -> row.split("")).toArray(String[][]::new));

        long result = 0;

        // horiz edge
        for (int i = 0; i < grid.width; i++) {
            var sim = new BeanSimulation(grid, new Beam(new IntVector2D(i, -1), new IntVector2D(0, 1)));
            sim.run();
            var res = sim.getNumEnergized();
            if (res > result) {
                result = res;
            }

            var sim2 = new BeanSimulation(grid, new Beam(new IntVector2D(i, grid.height), new IntVector2D(0, -1)));
            sim2.run();
            res = sim2.getNumEnergized();
            if (res > result) {
                result = res;
            }
        }

        // vert edge
        for (int i = 0; i < grid.height; i++) {
            var sim = new BeanSimulation(grid, new Beam(new IntVector2D(-1, i), new IntVector2D(1, 0)));
            sim.run();
            var res = sim.getNumEnergized();
            if (res > result) {
                result = res;
            }

            var sim2 = new BeanSimulation(grid, new Beam(new IntVector2D(grid.width, i), new IntVector2D(-1, 0)));
            sim2.run();
            res = sim2.getNumEnergized();
            if (res > result) {
                result = res;
            }
        }

        return "%,d".formatted(result);
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

        public IntVector2D add(IntVector2D other) {
            return new IntVector2D(x + other.x, y + other.y);
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Data
    private static class Beam {
        private final IntVector2D position;
        private final IntVector2D direction;

        public Beam step() {
            return new Beam(position.add(direction), direction);
        }
    }

    @Slf4j
    private static class Grid {
        private List<Character> grid = new ArrayList<>();
        private int width;
        private int height;

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

        public Grid(String[][] grid) {
            this.width = grid[0].length;
            this.height = grid.length;
            for (String[] row : grid) {
                var chars = Arrays.stream(row).map(ch -> ch.charAt(0)).toList();
                this.grid.addAll(chars);
            }
        }

        public Character sampleGrid(IntVector2D coordinate) {
            return grid.get(coordinate.getIndex(width));
        }

        public boolean inBounds(IntVector2D coordinate) {
            return coordinate.x >= 0 && coordinate.x < width && coordinate.y >= 0 && coordinate.y < height;
        }

        public void visualize(Set<IntVector2D> positions) {
            List<List<Character>> partition = ListUtils.partition(this.grid, this.width);
            for (int i = 0; i < partition.size(); i++) {
                var row = partition.get(i);
                for (int j = 0; j < row.size(); j++) {
                    var pos = new IntVector2D(j, i);
                    if (positions.contains(pos)) {
                        row.set(j, '#');
                    }
                }
            }
            var logGrid = new Grid(partition.stream().map(row -> row.stream().map(Object::toString).toArray(String[]::new)).toArray(String[][]::new));
            logGrid.log();
        }


        private void log() {
            ListUtils.partition(this.grid, this.width).forEach(row -> {
                log.debug(String.valueOf(row.stream().map(Object::toString).collect(Collectors.joining(" "))));
            });
        }
    }

    public static class BeanSimulation {
        private Grid grid;
        private LinkedList<Beam> currentBeams;
        private HashSet<Beam> knownBeams;

        public BeanSimulation(Grid grid, Beam start) {
            this.grid = grid;
            this.currentBeams = new LinkedList<>();
            this.knownBeams = new HashSet<>();

            this.currentBeams.push(start);
        }

        public long getNumEnergized() {
            return this.knownBeams.stream().map(beam -> beam.position).filter(pos -> grid.inBounds(pos)).distinct().count();
        }

        public void log() {
            var energized = this.knownBeams.stream().map(beam -> beam.position).filter(pos -> grid.inBounds(pos)).collect(Collectors.toSet());
            grid.visualize(energized);
        }

        private void addBeam(Beam beam) {
            if (!knownBeams.contains(beam)) {
                currentBeams.push(beam);
                knownBeams.add(beam);
            }
        }

        public void run() {
            while (!currentBeams.isEmpty()) {
                var beam = currentBeams.pop();
                beam = beam.step();

                if (!grid.inBounds(beam.position)) {
                    continue;
                }

                var sample = grid.sampleGrid(beam.position);
                switch (sample) {
                    case '.' -> {
                        addBeam(beam);
                    }
                    case '/' -> {
                        if (beam.direction.x > 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(0, -1)));
                        } else if (beam.direction.x < 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(0, 1)));
                        } else if (beam.direction.y > 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(-1, 0)));
                        } else if (beam.direction.y < 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(1, 0)));
                        }
                    }
                    case '\\' -> {
                        if (beam.direction.x > 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(0, 1)));
                        } else if (beam.direction.x < 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(0, -1)));
                        } else if (beam.direction.y > 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(1, 0)));
                        } else if (beam.direction.y < 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(-1, 0)));
                        }
                    }
                    case '-' -> {
                        if (beam.direction.y != 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(1, 0)));
                            addBeam(new Beam(beam.position, new IntVector2D(-1, 0)));
                        } else {
                            addBeam(beam);
                        }
                    }
                    case '|' -> {
                        if (beam.direction.x != 0) {
                            addBeam(new Beam(beam.position, new IntVector2D(0, 1)));
                            addBeam(new Beam(beam.position, new IntVector2D(0, -1)));
                        } else {
                            addBeam(beam);
                        }
                    }
                }
            }
        }
    }
}
