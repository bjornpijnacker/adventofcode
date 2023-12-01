import regex


class Valve:
    def __init__(self, name, flow_rate, leads_to):
        self.name = name
        self.flow_rate = int(flow_rate)
        self.leads_to = leads_to
        self.is_open = False

    def __str__(self):
        return f"{self.name} {self.flow_rate} {self.leads_to} {self.is_open}"


input_pattern = "Valve ([A-Z]{2}) has flow rate=(\d+); tunnels? leads? to valves? ((?>(?>[A-Z]{2})(?>, )?)+)"


def calc_next(minutes_left, valves, current_valve):
    valves_set = set([v.name for v in valves.values()])

    children_steps_total_flow = {valves[v].name: (1, int(not valves[v].is_open) * valves[v].flow_rate * minutes_left - 1) for v in
                                 valves[current_valve].leads_to}

    steps = 2
    while True:
        if steps > minutes_left or len(
                valves_set.difference(set(children_steps_total_flow.keys()))) == 0:  # all checked
            break
        children_steps_total_flow_next = [{valves[v].name: (steps, int(not valves[v].is_open) * valves[v].flow_rate * (minutes_left - steps)) for v in
                                           valves[valve].leads_to} for valve in children_steps_total_flow.keys()]
        new_dict = dict()
        for c in children_steps_total_flow_next:
            new_dict.update(c)
        new_dict.update(children_steps_total_flow)
        children_steps_total_flow = new_dict
        steps += 1

    best = max(children_steps_total_flow, key=lambda x: children_steps_total_flow[x][1] // children_steps_total_flow[x][0])
    return best, children_steps_total_flow[best][0], children_steps_total_flow[best][1]


valves = dict()
for line in open('input', 'r').readlines():
    name, flow_rate, leads_to = regex.findall(input_pattern, line)[0]
    leads_to = leads_to.split(', ')
    valve = Valve(name, flow_rate, leads_to)
    valves[name] = valve

current_valve = 'AA'
release = 0
minutes_left = 30
while minutes_left >= 0:
    best, minutes, released = calc_next(minutes_left, valves, current_valve)
    if released <= 0:
        break
    print(best, minutes, released)
    minutes_left -= minutes if valves[best].is_open else minutes + 1
    release += released if not valves[best].is_open else 0
    current_valve = best
    valves[best].is_open = True
print(release)
