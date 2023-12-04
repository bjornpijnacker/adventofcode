from collections import deque
import numpy as np

with open('input.txt', 'r') as file:
    lines = file.readlines()

opening = ['(', '[', '{', '<']
closing = [')', ']', '}', '>']

match = {'(': ')', '[': ']', '{': '}', '<': '>',
         ')': '(', ']': '[', '}': '{', '>': '<'}

points = {')': 3, ']': 57, '}': 1197, '>': 25137}

points2 = {'(': 1, '[': 2, '{': 3, '<': 4}

sum = 0
linesum = []
for line in lines:
    _sum = 0
    stack = deque()
    for char in line:
        if char in opening:
            stack.append(char)
        elif char in closing:
            matchchar = stack.pop()
            if not matchchar == match[char]:
                sum += points[char]
                continue
        elif char == '\n':
            print("found newline", stack)
            while not len(stack) == 0:
                _sum *= 5
                _sum += points2[stack.pop()]
            linesum.append(_sum)
            continue

print(sum)
linesum.sort()
print(linesum[int(len(linesum)/2)])
