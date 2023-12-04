def main():
    input_list = []
    with open("input", "r") as file:
        temp = file.read()
        input_list = temp.split('\n\n')

    input_list = [group.split('\n') for group in input_list]
    print(input_list)
    acc_sum = 0
    for group in input_list:
        acc_sum += group_score_and(group)
    print(acc_sum)


def group_score(group: [str]):
    alph_dict = {}
    for ch in char_range('a', 'z'):
        alph_dict[ch] = 0

    for person in group:
        for question in person:
            alph_dict[question] = 1

    acc_sum = 0
    for ch in char_range('a', 'z'):
        acc_sum += alph_dict[ch]
    return acc_sum


def group_score_and(group: [str]):
    acc_sum = 0
    for question in char_range('a', 'z'):
        acc_sum += int(all([(question in person) for person in group]))
    return acc_sum


def char_range(c1, c2):
    for c in range(ord(c1), ord(c2)+1):
        yield chr(c)


if __name__ == "__main__":
    main()