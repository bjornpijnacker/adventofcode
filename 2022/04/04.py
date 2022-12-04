import re

# q1
print(sum([(int(sections[0]) <= int(sections[2]) and int(sections[1]) >= int(sections[3])) or (int(sections[0]) >= int(sections[2]) and int(sections[1]) <= int(sections[3])) for sections in [re.split('-|,', line.strip()) for line in open('input', 'r').readlines()]]))

# q2
# Not the most efficient solution, as q1 is O(1) for each pair of sections and this is O(n) based on the size of the
# interval, however the input size is very small. A comparison as with q1 would have been faster.
print(sum([len(set(range(int(sections[0]), int(sections[1]) + 1)) & set(range(int(sections[2]), int(sections[3]) + 1))) != 0 for sections in [re.split('-|,', line.strip()) for line in open('input', 'r').readlines()]]))