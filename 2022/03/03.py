# q1
print(sum([ord(char[0]) - 96 if char[0].islower() else ord(char[0]) - 38 for char in [list(set(backpack[0:len(backpack)//2]).intersection(set(backpack[len(backpack)//2:]))) for backpack in [list(line.strip()) for line in open('input', 'r').readlines()]]]))

# q2
print(sum([ord(char[0]) - 96 if char[0].islower() else ord(char[0]) - 38 for char in [list(set(group[0].strip()).intersection(set(group[1].strip())).intersection(set(group[2].strip()))) for group in zip([lines for lines in open('input', 'r').readlines()[0::3]], [lines for lines in open('input', 'r').readlines()[1::3]], [lines for lines in open('input', 'r').readlines()[2::3]])]]))