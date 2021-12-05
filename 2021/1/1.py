def main():
    with open('input', 'r') as inputFile:
        data = inputFile.readlines()
        data = [int(d) for d in data]
        counter = 0
        for count, _ in enumerate(data):
            if count < 3:
                continue

            prev = (data[count - 1] + data[count - 2] + data[count - 3]) / 3
            curr = (data[count] + data[count - 1] + data[count - 2]) / 3

            if prev < curr:
                counter += 1
        print(counter)

if __name__ == "__main__":
    main()

