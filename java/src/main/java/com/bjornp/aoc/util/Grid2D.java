package com.bjornp.aoc.util;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.*;

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

    public Grid2D(Grid2D<T> grid) {
        this.width = grid.width;
        this.height = grid.height;
        this.grid.addAll(grid.grid);
    }
    
    private Coordinate2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new Coordinate2D(x, y);
        }

    public T get(Coordinate2D coordinate) {
        return grid.get(coordinate.getIndex(width));
    }

    /**
     * Returns the first index of value row-wise.
     * @param value Value to find
     * @return Optional<Coordinate2D>
     */
    public Optional<Coordinate2D> indexOf(T value) {
        var index = grid.indexOf(value);
        if (index == -1) return Optional.empty();
        return Optional.of(fromIndex(index));
    }

    /**
     * Warning: very slow operation!
     */
    public void replaceAll(T value, T newValue) {
        Optional<Coordinate2D> idx;
        while ((idx = indexOf(value)).isPresent()) {
            grid.set(idx.get().getIndex(width), newValue);
        }
    }

    public void set(Coordinate2D coordinate, T value) {
        grid.set(coordinate.getIndex(width), value);
    }

    public boolean inBounds(Coordinate2D coordinate) {
        return coordinate.getX() < width && coordinate.getY() < height && coordinate.getX() >= 0 && coordinate.getY() >= 0;
    }

    public String toString(Collection<Coordinate2D> overlay, T overlayValue) {
        var gridCopy = new Grid2D<>(this);
        overlay.forEach(ol -> gridCopy.grid.set(ol.getIndex(gridCopy.width), overlayValue));
        return gridCopy.toString();
    }

    public String toString() {
        var gridLs = Lists.partition(grid, width);
        var sb = new StringBuilder();
        gridLs.forEach(grid -> {
            grid.forEach(sb::append);
            sb.append("\n");
        });
        return sb.toString();
    }
}
