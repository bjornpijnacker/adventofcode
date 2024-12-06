package com.bjornp.aoc.util;

public enum Direction2D {
    NORTH,
    EAST,
    SOUTH,
    WEST;

    public Direction2D right() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }
}
