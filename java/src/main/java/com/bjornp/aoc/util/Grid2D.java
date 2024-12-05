package com.bjornp.aoc.util;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid2D<T> {
    private final List<T> grid = new ArrayList<>();

    @Getter
    private final int width;

    @Getter
    private final int height;

    public Grid2D(T[][] grid) {
        this.width = grid[0].length;
        this.height = grid.length;
        for (T[] row : grid) {
            this.grid.addAll(Arrays.stream(row).toList());
        }
    }

    public T get(Coordinate2D coordinate) {
        return grid.get(coordinate.getIndex(width));
    }
}
