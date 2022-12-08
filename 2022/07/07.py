import copy
import os

dirs = []  # (name, parent, size)
files = []  # (name, size, parent)

lines = [[l.strip() for l in line.split(' ')] for line in open('input', 'r').readlines()]
currentpath = "/"
# parse input
for line in lines:
    match line:
        case ['$', 'ls']:
            pass
        case ['$', 'cd', '/']:
            currentpath = "/"
        case ['$', 'cd', name]:
            currentpath = os.path.normpath(os.path.join(currentpath, line[2]))
        case ['dir', dirname]:
            if list(filter(lambda d: d["name"] == os.path.join(currentpath, line[1]), dirs)):
                continue
            dirs.append({"name": os.path.join(currentpath, line[1]), "parent": currentpath, "size": -1})
        case [size, filename]:
            if list(filter(lambda f: f["name"] == os.path.join(currentpath, line[1]), files)):
                continue
            files.append({"name": os.path.join(currentpath, line[1]), "parent": currentpath, "size": int(line[0])})

# iteratively calculate sizes until calculation has stabilized
# we start from the "leaf" directories as they don't have undefined children and go step by step until we
# know the size of everything, or no new sizes can be calculated
olddirs = []
dirs.append({"name": "/", "parent": "", "size": -1})
while olddirs != dirs:
    olddirs = copy.deepcopy(dirs)
    for d in dirs:
        children = list(filter(lambda _d: _d["parent"] == d["name"], dirs)) + list(filter(lambda _f: _f["parent"] == d["name"], files))
        if list(filter(lambda c: c["size"] < 0, children)):
            continue
        d["size"] = sum(map(lambda c: c["size"], children))

# q1
print(sum(map(lambda d: d["size"] if d["size"] <= 100000 else 0, dirs)))

# q2
needed_space = 30000000 - (70000000 - list(filter(lambda d: d["name"] == "/", dirs))[0]["size"])
print(sorted(list(filter(lambda d: d["size"] >= needed_space, dirs)), key=lambda d: d["size"])[0]["size"])
