package com.bjornp.aoc.solutions.implementations.y2015;

import com.bjornp.aoc.annotation.SolutionDay;
import com.bjornp.aoc.solutions.AdventOfCodeSolution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SolutionDay(year = 2015, day = 18)
@Slf4j
public class Day18 extends AdventOfCodeSolution {
    public Day18() {
        super(18, 2015);

        register("a", this::runSolutionA);
        register("b", this::runSolutionB);
    }


    public String runSolutionA(String input) {
        var grid = new Grid(input);

        for (int i = 0; i < 100; ++i) {
            grid.step();
        }

        return "%,d".formatted(grid.count());
    }

    public String runSolutionB(String input) {
        var grid = new Grid(input, true);

        for (int i = 0; i < 100; ++i) {
            grid.step();
        }

        return "%,d".formatted(grid.count());
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

        public IntVector2D w() {
            return new IntVector2D(x - 1, y);
        }

        public IntVector2D e() {
            return new IntVector2D(x + 1, y);
        }

        public IntVector2D n() {
            return new IntVector2D(x, y - 1);
        }

        public IntVector2D s() {
            return new IntVector2D(x, y + 1);
        }

        public IntVector2D nw() {
            return new IntVector2D(x - 1, y - 1);
        }

        public IntVector2D ne() {
            return new IntVector2D(x + 1, y - 1);
        }

        public IntVector2D sw() {
            return new IntVector2D(x - 1, y + 1);
        }

        public IntVector2D se() {
            return new IntVector2D(x + 1, y + 1);
        }

        public List<IntVector2D> neighbors() {
            return List.of(w(), e(), n(), s(), nw(), ne(), sw(), se());
        }

        @Override
        public String toString() {
            return "<%,d, %,d>".formatted(x, y);
        }
    }


    @Slf4j
    private static class Grid {
        private List<Boolean> grid = new ArrayList<>();

        private boolean stuckCorners;
        private int width;
        private int height;

        private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

        public Grid(String grid) {
            this.width = grid.split("\n")[0].length();
            this.height = grid.split("\n").length;
            for (String row : grid.split("\n")) {
                var values = Arrays.stream(row.split("")).map(ch -> ch.charAt(0) == '#').toList();
                this.grid.addAll(values);
            }
        }

        public Grid(String grid, boolean stuckCorners) {
            this(grid);
            this.stuckCorners = stuckCorners;
            if (this.stuckCorners) {
                this.grid.set(new IntVector2D(0, 0).getIndex(width), true);
                this.grid.set(new IntVector2D(width - 1, 0).getIndex(width), true);
                this.grid.set(new IntVector2D(0, height - 1).getIndex(width), true);
                this.grid.set(new IntVector2D(width - 1, height - 1).getIndex(width), true);
            }
        }

        public boolean sampleGrid(IntVector2D coordinate) {
            return inBounds(coordinate) ? grid.get(coordinate.getIndex(width)) : false;
        }

        public void step() {
            var nextGrid = new ArrayList<Boolean>();
            for (int i = 0; i < grid.size(); ++i) {
                var coord = fromIndex(i);
                var neighbors = coord.neighbors().stream().map(this::sampleGrid).filter(b -> b).count();
                    nextGrid.add(sampleGrid(coord) && neighbors == 2 || neighbors == 3);
            }
            if (this.stuckCorners) {
                nextGrid.set(new IntVector2D(0, 0).getIndex(width), true);
                nextGrid.set(new IntVector2D(width - 1, 0).getIndex(width), true);
                nextGrid.set(new IntVector2D(0, height - 1).getIndex(width), true);
                nextGrid.set(new IntVector2D(width - 1, height - 1).getIndex(width), true);
            }
            grid = nextGrid;
        }

        public long count() {
            return grid.stream().filter(b -> b).count();
        }

        public boolean inBounds(IntVector2D coordinate) {
            return coordinate.x >= 0 && coordinate.x < width && coordinate.y >= 0 && coordinate.y < height;
        }

        public void visualize() {
            List<List<Boolean>> partition = ListUtils.partition(this.grid, this.width);
            for (int i = 0; i < partition.size(); i++) {
                for (int j = 0; j < partition.get(i).size(); j++) {
                    System.out.print(partition.get(i).get(j) ? "#" : ".");
                }
                System.out.println();
            }
        }
    }
}
