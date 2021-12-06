with open('input', 'r') as file:
    init = file.readlines()[0].split(',')
    init = [int(d) for d in init]

lanterfish = [0 for _ in range(9)]
for fish in init:
    lanterfish[fish] += 1

days = 256
lanterfish_new = [0 for _ in range(9)]
for day in range(days):
    for index in range(1, 9):
        lanterfish_new[index - 1] = lanterfish[index]
    lanterfish_new[8] = lanterfish[0]
    lanterfish_new[6] += lanterfish[0]
    lanterfish_new, lanterfish = lanterfish, lanterfish_new

print(sum(lanterfish))
