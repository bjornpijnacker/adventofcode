# q1
print(sum([((ord(game[1]) - ord(game[0]) - 1) % 3) * 3 + ord(game[1]) - 87 for game in [line.strip().split(" ") for line in open('input', 'r').readlines()]]))

# q2
print(sum([(ord(game[0]) + ord(game[1]) + 2) % 3 + 1 + (ord(game[1]) - 88) * 3 for game in [line.strip().split(" ") for line in open('input', 'r').readlines()]]))
