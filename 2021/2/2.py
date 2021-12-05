with open('input', 'r') as file:
    data = file.readlines()[:-1]
    data = [(line.strip().split(' ')[0], int(line.strip().split(' ')[1])) for line in data]

pos = 0
depth = 0
aim = 0

for (direction, amount) in data:
    if direction == "forward":
        pos += amount
        depth += aim * amount
    elif direction == "down":
        aim += amount
    elif direction == "up":
        aim -= amount

print(pos * depth)

