import copy

import numpy as np


class Monkey:
    def __init__(self):
        self.items = []
        self.operation_value = 1
        self.operator = ''
        self.test = 0
        self.test_true = 0
        self.test_false = 0
        self.inspect_cnt = 0

    def __str__(self):
        return f"items={self.items}, " \
               f"operator={self.operator}, " \
               f"operation_value={self.operation_value}, " \
               f"test={self.test},{self.test_true},{self.test_false}, " \
               f"inspect_cnt={self.inspect_cnt}"

    def turn1(self, monkey_ls: list):
        while self.items:
            item = self.items.pop()
            self.inspect_cnt += 1
            if self.operator == '+':
                item += item if self.operation_value == 'old' else int(self.operation_value)
            else:
                item *= item if self.operation_value == 'old' else int(self.operation_value)
            monkey_ls[self.test_true if item % self.test == 0 else self.test_false].items.append(item % total_mod)

    def turn2(self, monkey_ls: list):
        while self.items:
            item = self.items.pop()
            self.inspect_cnt += 1
            item //= 3
            if self.operator == '+':
                item += item if self.operation_value == 'old' else int(self.operation_value)
            else:
                item *= item if self.operation_value == 'old' else int(self.operation_value)
            monkey_ls[self.test_true if item % self.test == 0 else self.test_false].items.append(item)


monkeys = []
for monkey in [monkey.split('\n') for monkey in open('input', 'r').read().split('\n\n')]:
    monk = Monkey()
    for line in monkey:
        match (line.strip().split(' ')[0]):
            case 'Monkey':
                pass
            case 'Starting':
                monk.items = [int(items.replace(',', '')) for items in line.strip().split(' ')[2:]]
            case 'Operation:':
                monk.operator = line.strip().split(' ')[4]
                monk.operation_value = line.strip().split(' ')[5]
            case 'Test:':
                monk.test = int(line.strip().split(' ')[3])
            case 'If':
                if line.strip().split(' ')[1] == 'true:':
                    monk.test_true = int(line.strip().split(' ')[5])
                else:
                    monk.test_false = int(line.strip().split(' ')[5])
    monkeys.append(monk)

total_mod = np.prod([m.test for m in monkeys])

monkeys1 = copy.deepcopy(monkeys)
monkeys2 = copy.deepcopy(monkeys)

# q1
for i in range(20):
    for monkey in monkeys1:
        monkey.turn1(monkeys1)
print(np.prod([m.inspect_cnt for m in sorted(monkeys1, key=lambda m: -m.inspect_cnt)[:2]]))

# q2
for i in range(10000):
    for monkey in monkeys2:
        monkey.turn1(monkeys2)
print(np.prod([m.inspect_cnt for m in sorted(monkeys2, key=lambda m: -m.inspect_cnt)[:2]]))
