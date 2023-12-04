import numpy as np

with open('input', 'r') as file:
    data = file.readlines()
    for idx in range(len(data)):
        data[idx] = [l.split() for l in data[idx].split('|')]

sum = 0
for line in data:
    _sum = ""
    digit_1 = set(list(filter(lambda l: len(l) == 2, line[0]))[0])
    digit_4 = set(list(filter(lambda l: len(l) == 4, line[0]))[0])

    # find difference of 1 and 4:
    # we use this to identify 2 vs 3 vs 5 and 0 vs 6 vs 9
    L = digit_4 - digit_1

    for output_val in line[1]:
        if len(output_val) == 2: _sum += str(1)
        elif len(output_val) == 3: _sum += str(7)
        elif len(output_val) == 4: _sum += str(4)
        elif len(output_val) == 7: _sum += str(8)
        elif len(output_val) == 5:  # 2, 3, or 5
            if L.issubset(set(output_val)):
                _sum += str(5)
            elif digit_1.issubset(set(output_val)):
                _sum += str(3)
            else:
                _sum += str(2)
        elif len(output_val) == 6:  # 0, 6, or 9
            if digit_4.issubset(set(output_val)):
                _sum += str(9)
            elif L.issubset(set(output_val)):
                _sum += str(6)
            else:
                _sum += str(0)
    sum += int(_sum)

print(sum)