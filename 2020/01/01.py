def main():
    input_list = open("input", "r").read().splitlines()
    for i in input_list:
        for j in input_list:
            if i == j:
                continue
            for k in input_list:
                if i == k or j == k:
                    continue
                if int(i) + int(j) + int(k) == 2020:
                    print(int(i) * int(j) * int(k))
                    exit(0)
    exit(1)


if __name__ == "__main__":
    main()
