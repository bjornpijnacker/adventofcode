def main():
    f = open("input", "r")
    input_data = f.read()
    input_list = []
    for line in input_data.splitlines():
        input_list.append(tolist(line))
    print(calc(input_list, 3, 1))
    print(calc(input_list, 1, 1) * calc(input_list, 3, 1) * calc(input_list, 5, 1) * calc(input_list, 7, 1) *
          calc(input_list, 1, 2))


def calc(input_list, right: int, down: int):
    coordx = 0
    maxidx = len(input_list[0])
    # print(maxidx)
    counter = 0
    for i in range(0, len(input_list), down):
        counter += input_list[i][coordx]
        # print(input_list[i][coordx], i, coordx)
        coordx = (coordx + right) % maxidx
    return counter


def tolist(line: str):
    line_list = []
    for char in line:
        line_list.append(0 if char == '.' else 1)
    return line_list


if __name__ == "__main__":
    main()
