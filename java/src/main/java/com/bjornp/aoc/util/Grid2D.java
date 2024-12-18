package com.bjornp.aoc.util;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.*;

public class Grid2D<T> {
    @Getter
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

    public Grid2D(int width, int height, T init) {
        this.width = width;
        this.height = height;
        this.grid.addAll(Collections.nCopies(width * height, init));
    }
    
    private IntVector2D fromIndex(int index) {
            int x = index % width;
            int y = index / width;
            return new IntVector2D(x, y);
        }

    public T get(IntVector2D coordinate) {
        return grid.get(coordinate.getIndex(width));
    }

    /**
     * Returns the first index of value row-wise.
     * @param value Value to find
     * @return Optional<IntVector2D>
     */
    public Optional<IntVector2D> indexOf(T value) {
        var index = grid.indexOf(value);
        if (index == -1) return Optional.empty();
        return Optional.of(fromIndex(index));
    }

    public Collection<IntVector2D> findAll(T value) {
        var coordinates = new HashSet<IntVector2D>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                var c = new IntVector2D(i, j);
                if (this.get(c).equals(value)) {
                    coordinates.add(c);
                }
            }
        }
        return coordinates;
    }

    /**
     * Warning: very slow operation!
     */
    public void replaceAll(T value, T newValue) {
        Optional<IntVector2D> idx;
        while ((idx = indexOf(value)).isPresent()) {
            grid.set(idx.get().getIndex(width), newValue);
        }
    }

    public void set(IntVector2D coordinate, T value) {
        grid.set(coordinate.getIndex(width), value);
    }

    public boolean inBounds(IntVector2D coordinate) {
        return coordinate.getX() < width && coordinate.getY() < height && coordinate.getX() >= 0 && coordinate.getY() >= 0;
    }

    public String toString(Collection<IntVector2D> overlay, T overlayValue) {
        var gridCopy = new Grid2D<>(this);
        overlay.forEach(ol -> gridCopy.grid.set(ol.getIndex(gridCopy.width), overlayValue));
        return gridCopy.toString();
    }

    public String toString() {
        var gridLs = Lists.partition(grid, width);
        var sb = new StringBuilder();
        sb.append("\n");
        gridLs.forEach(grid -> {
            grid.forEach(sb::append);
            sb.append("\n");
        });
        return sb.toString();
    }

    public void swap(IntVector2D a, IntVector2D b) {
        var c = grid.get(a.getIndex(width));
        grid.set(a.getIndex(width), grid.get(b.getIndex(width)));
        grid.set(b.getIndex(width), c);
    }
}
