lines = open('input', 'r').readlines()
strength = 0
strength_counter = 20
program_counter = 0
x = 1

for line in lines:
    match line.strip().split(' '):
        case ['noop']:
            if program_counter == strength_counter - 1:
                strength += x * strength_counter
                strength_counter += 40
            program_counter += 1
        case ['addx', num]:
            if program_counter == strength_counter - 1 or program_counter == strength_counter - 2:
                strength += x * strength_counter
                strength_counter += 40
            program_counter += 2
            x += int(num)
print(strength)

x = 1
crt = [['.'] * 40, ['.'] * 40, ['.'] * 40, ['.'] * 40, ['.'] * 40, ['.'] * 40]
crt_counter = 0

for line in lines:
    match line.strip().split(' '):
        case ['noop']:
            if crt_counter % 40 in [x - 1, x, x + 1]:
                crt[crt_counter // 40][crt_counter % 40] = '#'
            crt_counter += 1
        case ['addx', num]:
            if crt_counter % 40 in [x - 1, x, x + 1]:
                crt[crt_counter // 40][crt_counter % 40] = '#'
            crt_counter += 1
            if crt_counter % 40 in [x - 1, x, x + 1]:
                crt[crt_counter // 40][crt_counter % 40] = '#'
            crt_counter += 1
            x += int(num)
print('\n'.join([''.join(c) for c in crt]))
