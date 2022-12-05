import functools
import operator
import re

# q1
stack_input, instructions = tuple(open('input', 'r').read().split('\n\n'))
parsed_input = functools.reduce(operator.iconcat, [list(enumerate(parsed_line)) for parsed_line in reversed([line.replace('    ', ' ').split(' ') for line in stack_input.split('\n')][:-1])], [])
stacks = [[] for _ in range(max([p for p, v in parsed_input]) + 1)]
[stacks[i].append(v) if v else None for (i, v) in parsed_input]

for instruction in instructions.split('\n'):
    amount, target, source = re.match('move ([0-9]+) from ([0-9]+) to ([0-9]+)', instruction).groups()
    [stacks[int(source) - 1].append(stacks[int(target) - 1].pop()) for _ in range(int(amount))]
print([stack[-1] for stack in stacks])

# q2
stack_input, instructions = tuple(open('input', 'r').read().split('\n\n'))
parsed_input = functools.reduce(operator.iconcat, [list(enumerate(parsed_line)) for parsed_line in reversed([line.replace('    ', ' ').split(' ') for line in stack_input.split('\n')][:-1])], [])
stacks = [[] for _ in range(max([p for p, v in parsed_input]) + 1)]
[stacks[i].append(v) if v else None for (i, v) in parsed_input]

for instruction in instructions.split('\n'):
    amount, target, source = re.match('move ([0-9]+) from ([0-9]+) to ([0-9]+)', instruction).groups()
    stacks[int(source) - 1].extend(stacks[int(target) - 1][-int(amount):])
    del stacks[int(target) - 1][-int(amount):]
print([stack[-1] for stack in stacks])
