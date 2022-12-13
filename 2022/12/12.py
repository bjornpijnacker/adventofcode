import functools
import operator

import numpy as np


def get_value(grid, coordinate):
    if grid[coordinate[0], coordinate[1]] == 'S':
        return ord('a')
    elif grid[coordinate[0], coordinate[1]] == 'E':
        return ord('z')
    else:
        return ord(grid[coordinate[0], coordinate[1]])


def get_neighbors(grid, coordinate):
    neighbors = []
    value = get_value(grid, coordinate)
    if coordinate[0] > 0 and get_value(grid, (coordinate[0] - 1, coordinate[1])) <= value + 1:
        neighbors.append((coordinate[0] - 1, coordinate[1]))
    if coordinate[0] < grid.shape[0] - 1 and get_value(grid, (coordinate[0] + 1, coordinate[1])) <= value + 1:
        neighbors.append((coordinate[0] + 1, coordinate[1]))
    if coordinate[1] > 0 and get_value(grid, (coordinate[0], coordinate[1] - 1)) <= value + 1:
        neighbors.append((coordinate[0], coordinate[1] - 1))
    if coordinate[1] < grid.shape[1] - 1 and get_value(grid, (coordinate[0], coordinate[1] + 1)) <= value + 1:
        neighbors.append((coordinate[0], coordinate[1] + 1))
    return neighbors


grid = np.array([list(line.strip()) for line in open('input', 'r').readlines()])

start, end = (np.where(grid == 'S'), np.where(grid == 'E'))
extra_starts = np.where(grid == 'a')
starts = list(zip(extra_starts[0], extra_starts[1]))
starts.append((start[0][0], start[1][0]))

lengths = []

for s in starts:
    frontier = set()
    visited = set()

    frontier.add(s)
    goal = (end[0][0], end[1][0])
    counter = 0

    while goal not in frontier:
        neighbors = set(functools.reduce(operator.iconcat, [get_neighbors(grid, f) for f in frontier], []))
        neighbors = set(filter(lambda n: n not in visited, neighbors))
        if len(neighbors) == 0:
            counter = np.inf  # no path to finish
            break
        [visited.add(n) for n in neighbors]
        frontier = neighbors
        counter += 1
    lengths.append(counter)

print(np.min(lengths))
