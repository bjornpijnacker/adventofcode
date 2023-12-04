def main():
    with open('input.txt', 'r') as file:
        adapters = file.readlines()
    adapters = [int(ad) for ad in adapters]
    adapters.sort()
    print(adapters)
    adapters = adapters + [adapters[len(adapters) - 1] + 3]

    result = dfs(0, adapters)
    print(result)


def dfs(start, adapters):
    print(start, adapters)
    if len(adapters) == 1:
        return int(1 <= adapters[0] - start <= 3)

    intermediary = 0
    for adap in adapters:
        if 1 <= adap - start <= 3:
            intermediary += dfs(adap, adapters[1:])
        else:
            break
    return intermediary



if __name__ == '__main__':
    main()
