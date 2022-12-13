import numpy as np

def moveT(H, T, T_visited):
    if abs(H[0] - T[0]) <= 1 and abs(H[1] - T[1]) <= 1:
        return H, T, T_visited

    newT = (0, 0)

    if abs(H[0] - T[0]) >= 1 and abs(H[1] - T[1]) >= 1:  # move diagonal
        if abs(H[0] - T[0]) == abs(H[1] - T[1]):
            newT = (H[0] - 1 if H[0] > T[0] else H[0] + 1, H[1] - 1 if H[1] > T[1] else H[1] + 1)
        elif abs(H[0] - T[0]) > abs(H[1] - T[1]):  # move horizontal
            newT = (H[0] - 1 if H[0] > T[0] else H[0] + 1, H[1])
        else:
            newT = (H[0], H[1] - 1 if H[1] > T[1] else H[1] + 1)
    elif abs(H[0] - T[0]) > 1:  # horizontal
        newT = (H[0] - 1 if H[0] > T[0] else H[0] + 1, T[1])
    elif abs(H[1] - T[1]) > 1:  # vertical
        newT = (T[0], H[1] - 1 if H[1] > T[1] else H[1] + 1)
    T_visited.add(newT)
    return H, newT, T_visited


def show():
    height = ((max(T + [H], key=lambda cell: cell[0])[0] + 1) - (min(T + [H], key=lambda cell: cell[0])[0]))
    width = ((max(T + [H], key=lambda cell: cell[1])[1] + 1) - (min(T + [H], key=lambda cell: cell[1])[1]))
    normalize_v = min(T + [H], key=lambda cell: cell[0])[0]
    normalize_h = min(T + [H], key=lambda cell: cell[1])[1]
    grid = np.zeros(shape=(height, width), dtype=int)
    grid[H[0] - normalize_v, H[1] - normalize_h] = -1
    for index, t in enumerate(T):
        if grid[t[0] - normalize_v, t[1] - normalize_h] == 0:
            grid[t[0] - normalize_v, t[1] - normalize_h] = index + 1
    print(direction, count)
    print(np.flip(grid.transpose(), axis=0))


steps = [line.strip().split(' ') for line in open('input', 'r').readlines()]
H = (0, 0)
T = [(0, 0), (0, 0), (0, 0), (0, 0), (0, 0), (0, 0), (0, 0), (0, 0), (0, 0)]
T_visited = set()

for direction, count in steps:
    print("  =======  ")
    match direction:
        case "L":
            for c in range(int(count)):
                H = (H[0] - 1, H[1])
                H, T[0], _ = moveT(H, T[0], set())
                for t in range(1, len(T) - 1):
                    T[t - 1], T[t], _ = moveT(T[t - 1], T[t], set())
                T[7], T[8], T_visited = moveT(T[7], T[8], T_visited)
                show()
        case "R":
            for c in range(int(count)):
                H = (H[0] + 1, H[1])
                H, T[0], _ = moveT(H, T[0], set())
                for t in range(1, len(T) - 1):
                    T[t - 1], T[t], _ = moveT(T[t - 1], T[t], set())
                T[7], T[8], T_visited = moveT(T[7], T[8], T_visited)
                show()
        case "U":
            for c in range(int(count)):
                H = (H[0], H[1] + 1)
                H, T[0], _ = moveT(H, T[0], set())
                for t in range(1, len(T) - 1):
                    T[t - 1], T[t], _ = moveT(T[t - 1], T[t], set())
                T[7], T[8], T_visited = moveT(T[7], T[8], T_visited)
                show()
        case "D":
            for c in range(int(count)):
                H = (H[0], H[1] - 1)
                H, T[0], _ = moveT(H, T[0], set())
                for t in range(1, len(T) - 1):
                    T[t - 1], T[t], _ = moveT(T[t - 1], T[t], set())
                T[7], T[8], T_visited = moveT(T[7], T[8], T_visited)
                show()

    # visualization
    show()
print(len(T_visited) + 1)
