import functools
import operator

import numpy as np

traces = [[tuple(trace.split(',')) for trace in line.strip().split(' -> ')] for line in open('input', 'r').readlines()]
all_coords = functools.reduce(operator.iconcat, traces, [])
max_h = int(max(all_coords, key=lambda t: int(t[0]))[0])
max_v = int(max(all_coords, key=lambda t: int(t[1]))[1])
floor_v = max_v + 2

# the cave size will be very large but this is easier than calculating the exact needed area (:
cave = np.zeros(shape=(floor_v + 1, max_h + floor_v + 1))
cave[floor_v:] = 1  # set the floor to rock

# set up cave
for trace in traces:
    for trace_idx in range(1, len(trace)):
        trace_from, trace_to = trace[trace_idx - 1], trace[trace_idx]
        trace_from, trace_to = (int(trace_from[0]), int(trace_from[1])), \
                               (int(trace_to[0]), int(trace_to[1]))
        if trace_from[0] == trace_to[0]:  # vertical
            if trace_from[1] > trace_to[1]:
                trace_from, trace_to = trace_to, trace_from
            for rock in range(trace_from[1], trace_to[1] + 1):
                cave[rock, trace_from[0]] = 1
        else:  # horizontal
            if trace_from[0] > trace_to[0]:
                trace_from, trace_to = trace_to, trace_from
            for rock in range(trace_from[0], trace_to[0] + 1):
                cave[trace_from[1], rock] = 1

# simulate sand
overflow = False
while True:
    sand = [0, 500]
    if cave[0, 500]:  # the pile is complete (puzzle 2)
        break
    while True:
        if not overflow and sand[0] >= max_v:  # overflow (puzzle 1)
            overflow = True
            print("q1", np.sum(cave == 2))
        if cave[sand[0] + 1, sand[1]]:  # down is blocked
            if cave[sand[0] + 1, sand[1] - 1]:  # left is blocked
                if cave[sand[0] + 1, sand[1] + 1]:  # right is blocked
                    cave[sand[0], sand[1]] = 2
                    break
                else:  # right is not blocked
                    sand[0] += 1
                    sand[1] += 1
            else:  # left is not blocked
                sand[0] += 1
                sand[1] -= 1
        else:  # down is not blocked
            sand[0] += 1

print("q2", np.sum(cave == 2))
print(np.array2string(cave, separator='', formatter={'float': lambda x: '.' if x == 0 else '#' if x == 1 else 'o'}))
