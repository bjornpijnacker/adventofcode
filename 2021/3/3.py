import numpy as np
import copy

with open('input', 'r') as file:
    data = file.readlines()
    data = [[int(char) for char in line.strip()] for line in data]
    matrix = np.array(data)

oxygen = copy.deepcopy(matrix)
for col in range(oxygen.shape[1]):
    bincount = np.bincount(oxygen[:,col])
    most_occurring = np.argmax(bincount)
    if (bincount[0] == bincount[1]):
        most_occurring = 1
    index_list = []
    for row in range(oxygen.shape[0]):
        if oxygen[row][col] != most_occurring:
            index_list.append(row)
    index_list.reverse()
    for index in index_list:
        oxygen = np.delete(oxygen, index, 0)
    if oxygen.shape[0] == 1:
        break
print(oxygen)

co2 = copy.deepcopy(matrix)
for col in range(co2.shape[1]):
    bincount = np.bincount(co2[:,col])
    least_occurring = np.argmin(bincount)
    if (bincount[0] == bincount[1]):
        least_occurring = 0
    index_list = []
    for row in range(co2.shape[0]):
        if co2[row][col] != least_occurring:
            index_list.append(row)
    index_list.reverse()
    for index in index_list:
        co2 = np.delete(co2, index, 0)
    if co2.shape[0] == 1:
        break
print(co2)

oxygen_val = 0
co2_val = 0
for bit in oxygen[0]:
    oxygen_val = (oxygen_val << 1) | bit

for bit in co2[0]:
    co2_val = (co2_val << 1) | bit

print(oxygen_val * co2_val)

# part 1:

#gamma = []
#epsilon = []
#for col in range(matrix.shape[1]):
#    most_ocurring = np.argmax(np.bincount(matrix[:,col]))
#    least_ocurring = np.argmin(np.bincount(matrix[:,col]))
#    gamma.append(most_ocurring)
#    epsilon.append(least_ocurring)

#print(gamma, epsilon)

#gamma_val = 0
#epsilon_val = 0
#for bit in gamma:
#    gamma_val = (gamma_val << 1) | bit

#for bit in epsilon:
#    epsilon_val = (epsilon_val << 1) | bit

#print(gamma_val * epsilon_val)
