package com.bjornp.aoc.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;

@AllArgsConstructor
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class IntVector2D {
    @EqualsAndHashCode.Include private final int x;

    @EqualsAndHashCode.Include private final int y;

    public int getIndex(int width) {
        return x + y * width;
    }

    public IntVector2D move(Direction2D direction) {
        return this.move(direction, 1);
    }

    public IntVector2D move(Direction2D direction, int s) {
        return switch (direction) {
            case NORTH -> this.n(s);
            case EAST -> this.e(s);
            case SOUTH -> this.s(s);
            case WEST -> this.w(s);
        };
    }

    public IntVector2D n() {
        return n(1);
    }

    public IntVector2D n(int s) {
        return new IntVector2D(x, y - s);
    }

    public IntVector2D e() {
        return e(1);
    }

    public IntVector2D e(int s) {
        return new IntVector2D(x + s, y);
    }

    public IntVector2D s() {
        return s(1);
    }

    public IntVector2D s(int s) {
        return new IntVector2D(x, y + s);
    }

    public IntVector2D w() {
        return w(1);
    }

    public IntVector2D w(int s) {
        return new IntVector2D(x - s, y);
    }

    public IntVector2D ne() {
        return ne(1);
    }

    public IntVector2D ne(int s) {
        return new IntVector2D(x + s, y - s);
    }

    public IntVector2D se() {
        return se(1);
    }

    public IntVector2D se(int s) {
        return new IntVector2D(x + s, y + s);
    }

    public IntVector2D nw() {
        return nw(1);
    }

    public IntVector2D nw(int s) {
        return new IntVector2D(x - s, y - s);
    }

    public IntVector2D sw() {
        return sw(1);
    }

    public IntVector2D sw(int s) {
        return new IntVector2D(x - s, y + s);
    }

    @Override
    public String toString() {
        return "[%,d, %,d]".formatted(x, y);
    }
}
