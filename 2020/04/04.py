def main():
    with open("input", "r") as file:
        data = file.read()
        data_list = data.split('\n\n')
        required = ['byr', 'iyr', 'eyr', 'hgt', 'hcl', 'ecl', 'pid']
        required.sort()
        for i in range(len(data_list)):
            data_list[i] = data_list[i].replace('\n', ' ')
        counter = 0
        for data in data_list:
            entries = data.split(' ')
            passport = []
            for i in range(len(entries)):
                split_idx = entries[i].index(':')
                passport.append((entries[i][0:split_idx], entries[i][split_idx+1:]))
            counter += int(all_entries(passport, required) and entries_correct(passport))
        print(counter)


def all_entries(passport: [(str, str)], required: [str]):
    keys = [x[0] for x in passport]
    keys.sort()
    if keys.__contains__('cid'):
        keys.remove('cid')
    return required == keys


def entries_correct(passport: [(str, str)]):
    hex = ['a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7','8', '9']
    ecl = ['amb', 'blu', 'brn', 'gry', 'grn', 'hzl', 'oth']
    for x in passport:
        if x[0] == 'byr':
            if not 1920 <= int(x[1]) <= 2002:
                print("invalid", x)
                return False
        elif x[0] == 'iyr':
            if not 2010 <= int(x[1]) <= 2020:
                print("invalid", x)
                return False
        elif x[0] == 'eyr':
            if not 2020 <= int(x[1]) <= 2030:
                print("invalid", x)
                return False
        elif x[0] == 'hgt':
            end = x[1][-2:]
            if end == 'cm' and 150 <= int(x[1][:-2]) <= 193:
                continue
            elif end == 'in' and 59 <= int(x[1][:-2]) <= 76:
                continue
            else:
                print("invalid", x)
                return False
        elif x[0] == 'hcl':
            if not x[1][0] == '#' or not len(x[1]) == 7:
                print("invalid", x)
                return False
            else:
                for char in x[1][1:7]:
                    if char not in hex:
                        print("invalid", x)
                        return False
        elif x[0] == 'ecl':
            if not x[1] in ecl:
                print("invalid", x)
                return False
        elif x[0] == 'pid':
            if not len(x[1]) == 9 or not(x[1].isnumeric()):
                print("invalid", x)
                return False
    print("  valid", x)
    return True


if __name__ == "__main__":
    main()