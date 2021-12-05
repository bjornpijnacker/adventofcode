import numpy as np

with open('input', 'r') as file:
    data = file.readlines()
    lines = [[d.split(',') for d in dd.split()[0:3:2]] for dd in data]

lines = np.array(lines).astype(int)

# ┌─────────────────────────────────────────────────────────────┐
# │ indexing works as follows: shape is points * 2 * 2          │
# │ lines[x,:,:] returns input line x                           │
# │ lines[:,x,:] returns the first or second point in each line │
# │ linex[:,:,x] returns the x or y component for a given line  │
# └─────────────────────────────────────────────────────────────┘

# find largest x and y
x_max = np.max(lines[:,:,0])
y_max = np.max(lines[:,:,1])
counts = np.zeros((x_max + 1, y_max + 1))

for line in lines:
    if line[0][0] == line[1][0]:  # if x components are equal
        if line[0][1] > line[1][1]:  # range in python can only go up, not down, so we swap if needed
            loop_range = range(line[1][1], line[0][1] + 1)
        else:
            loop_range = range(line[0][1], line[1][1] + 1)
        for i in loop_range:
            counts[line[0][0], i] += 1
    elif line[0][1] == line[1][1]:  # if y components are equal
       if line[0][0] > line[1][0]:
           loop_range = range(line[1][0], line[0][0] + 1)
       else:
           loop_range = range(line[0][0], line[1][0] + 1)
       for i in loop_range:
            counts[i, line[0][1]] += 1
    else:  # 45° angle
        x = line[0][0]
        y = line[0][1]
        counts[x, y] += 1
        while x != line[1][0] and y != line[1][1]:
            x += 1 if line[0][0] < line[1][0] else -1
            y += 1 if line[0][1] < line[1][1] else -1
            counts[x, y] += 1

# print(np.where(counts == 0, '.', counts.astype(int).astype(str)).transpose())
print(len(np.where(counts >= 2)[0]))
