import numpy as np

with open('input', 'r') as file:
    data = np.array(file.readlines()[0].split(',')).astype(int)

median = np.median(data)
print(sum(abs(data - median)))
