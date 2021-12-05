import numpy as np

with open('input', 'r') as file:
    data = file.readlines()
    numbers = [int(n) for n in data[0].strip().split(',')]

    bingo_cards = []
    i = 2
    while i < len(data):
        card = [dd.strip().split() for dd in data[i:i+5]]
        bingo_cards.append(np.array(card).astype(int))
        i += 6


def check_card(bingo_card, sequence):
    checks = np.zeros((5,5))
    for idx, num in enumerate(sequence):
        x, y = np.where(bingo_card == num)
        if x.size == 0 or y.size == 0:
            pass
        else:
            checks[x[0]][y[0]] = True
        if np.any(np.all(checks, axis=0)) or np.any(np.all(checks, axis=1)):
            return idx, num, checks

rankings = []
for card in bingo_cards:
    rank, num, checks = check_card(card, numbers)
    rankings.append((rank, checks, card, num))

winner = max(rankings, key=lambda x: x[0])
sum = 0
for i in range(5):
    for j in range(5):
        if not int(winner[1][i][j]):
            sum += winner[2][i][j]
print(sum * winner[3])
