# q1
print(sorted([sum(cals) for cals in [map(int, i) for i in [reindeer.strip().split('\n') for reindeer in open('input', 'r').read().split('\n\n')]]])[-1])

# q2
print(sum(sorted([sum(cals) for cals in [map(int, i) for i in [reindeer.strip().split('\n') for reindeer in open('input', 'r').read().split('\n\n')]]])[-3:]))
