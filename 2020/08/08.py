def main():
    with open("input", "r") as input_file:
        instructions = input_file.readlines()
    acc = 0
    i = 0
    while i < len(instructions):
        ins = instructions[i]
        (opcode, arg) = ins.split(' ')
        print('opcode: ' + opcode, '| arg sign: ' + arg[0], '| arg val: ' + arg[1:])
        if opcode == 'nop':
            i += 1
        elif opcode == 'acc':
            acc += int(arg[1:]) * (-1 if arg[0] == '-' else 1)
            i += 1
        elif opcode == 'jmp':
            if arg[0] == '-':
                break
            else:
                i += int(arg[1:])
    print(acc)


if __name__ == "__main__":
    main()
