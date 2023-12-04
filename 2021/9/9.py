import numpy as np

with open('input', 'r') as file:
    data = file.readlines()
    data = np.array([list(line.strip()) for line in data]).astype(int)

data = np.pad(data, pad_width=1, mode='constant', constant_values=10)

min_pts = []
for i in range(1, data.shape[0] - 1):
    for j in range(1, data.shape[1] - 1):
        if data[i-1, j-1] < data[i,j]: continue
        if data[i  , j-1] < data[i,j]: continue
        if data[i+1, j-1] < data[i,j]: continue
        if data[i+1, j  ] < data[i,j]: continue
        if data[i+1, j+1] < data[i,j]: continue
        if data[i  , j+1] < data[i,j]: continue
        if data[i-1, j+1] < data[i,j]: continue
        if data[i-1, j  ] < data[i,j]: continue
        min_pts.append((i, j, 1))

for idx in range(len(min_pts)):


def grow(data, min_pt, seen):

