import numpy as np

with open('input', 'r') as file:
    data = np.array(file.readlines()[0].split(',')).astype(int)

# print(np.mean(data))  # the mean rounded is off-by-one w.r.t. the optimal location for the given input...
mean_dist = []
for mean in range(0, max(data)):  # so time to simply try all of them
    mean_dist.append((mean, sum([(d * (d + 1))/2 for d in abs(data - mean)])))
print(min(mean_dist, key=lambda k: k[1]))
