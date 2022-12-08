import numpy as np


def is_visible(row, col, j, i):
    return any([max(row[0:i]) < row[i] if i > 0 else True, max(row[i + 1:]) < row[i] if i + 1 < len(row) else True,
                max(col[0:j]) < col[j] if j > 0 else True, max(col[j + 1:]) < col[j] if j + 1 < len(col) else True])


def score(row, col, j, i):
    return np.prod([
        next((list(reversed(row[0:i])).index(t) + 1 for t in reversed(row[0:i]) if t >= row[i]), i),
        next((list(row[i + 1:]).index(t) + 1 for t in row[i + 1:] if t >= row[i]), len(row) - (i + 1)),
        next((list(reversed(col[0:j])).index(t) + 1 for t in reversed(col[0:j]) if t >= col[j]), j),
        next((list(col[j + 1:]).index(t) + 1 for t in col[j + 1:] if t >= col[j]), len(col) - (j + 1))
    ])


data = np.array([list(line.strip()) for line in open('input', 'r').readlines()]).astype(int)

# q1
print(sum([is_visible(data[i, :], data[:, j], i, j) for i in range(data.shape[0]) for j in range(data.shape[1])]))

# q2
print(max([score(data[i, :], data[:, j], i, j) for i in range(data.shape[0]) for j in range(data.shape[1])]))
