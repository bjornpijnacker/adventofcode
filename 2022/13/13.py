import regex
import functools

pattern = "\[([^\[\]]*+(?:(?R)[^\[\]]*)*+)\]"
pattern_digit = "^(\d+)(?:,)?(.*)"


def compare(first, second):
    # print('comparing\n\t', first, '\n\t', second)
    if first == '[]' and second == '[]' or first == '' and second == '':
        return None
    elif first.strip() == '' or first == '[]':
        return True
    elif second.strip() == '' or second == '[]':
        return False

    first_digit = regex.findall(pattern_digit, first)
    second_digit = regex.findall(pattern_digit, second)

    if first_digit != [] and second_digit != []:
        if int(first_digit[0][0]) != int(second_digit[0][0]):
            return int(first_digit[0][0]) < int(second_digit[0][0])
        else:
            return compare(first_digit[0][1], second_digit[0][1])  # the rest of the list

    if first_digit == [] and second_digit == []:
        first_sub = next(regex.finditer(pattern, first))
        second_sub = next(regex.finditer(pattern, second))

        if first_sub.group() and second_sub.group():
            sublist = compare(first_sub.group()[1:-1], second_sub.group()[1:-1])
            if sublist is not None:
                return sublist
            return compare(first[first_sub.span()[1] + 1:],
                           second[second_sub.span()[1] + 1:])  # else first_sub.match and not second_sub.match:
    elif first_digit != []:
        res = compare(f'[{first_digit[0][0]}],{first[len(first_digit[0][0]) + 1:]}', second)
        if res is not None:
            return res
        return None
    elif second_digit != []:
        res = compare(first, f'[{second_digit[0][0]}],{second[len(second_digit[0][0]) + 1:]}')
        if res is not None:
            return res
        return None

def comparator(x, y):
    return -1 if compare(x, y) else 1

# q1
counter = 0
for index, (first, second) in enumerate([tuple(l.splitlines()) for l in open('input_', 'r').read().split('\n\n')]):
    counter += (index + 1) if compare(first, second) else 0
print(counter)

# q2
data = open('input', 'r').read().replace('\n\n', '\n').split('\n')
data.extend(["[[2]]", "[[6]]"])
data.sort(key=functools.cmp_to_key(comparator))
print((data.index("[[2]]") + 1) * (data.index("[[6]]") + 1))
