import functools
import operator

import regex

pattern = "Sensor at x=([^,]+), y=([^:]+): closest beacon is at x=([^,]+), y=([^$]+)"
# target_y = 2000000

input = functools.reduce(operator.iconcat,
                         [[(int(r[0]), int(r[1]), int(r[2]), int(r[3])) for r in regex.findall(pattern, line)]
                          for line in open('input', 'r').readlines()],
                         [])

for target_y in range(4000000):
    if target_y % 100000 == 0:
        print(target_y)
    scanned_ranges = []

    for sx, sy, bx, by in input:
        sensor_range = abs(sx - bx) + abs(sy - by)
        dist_target = abs(target_y - sy)
        scan_width_at_target = sensor_range - dist_target
        if scan_width_at_target > 0:
            target_x = (sx - scan_width_at_target, sx + scan_width_at_target)
            scanned_ranges.append(target_x)

    scanned_ranges.sort()
    found = False
    running_max = scanned_ranges[0][1]
    for sr in scanned_ranges:
        if sr[0] > running_max:
            print(target_y + 4000000 * (sr[0] - 1))
            found = True
            break
        else:
            running_max = sr[1] if sr[1] > running_max else running_max
    if found:
        break

    # available_locations = set(range(4000000))
    # for srs, sre in scanned_ranges:
    #     # print(srs, sre)
    #     available_locations = available_locations.difference(set(range(srs, sre + 1)))
    # if available_locations:
    #     print(target_y, available_locations)
    #     break
    # else:
    #     print(target_y)
