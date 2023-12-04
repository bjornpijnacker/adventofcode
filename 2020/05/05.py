from math import floor, ceil

def main():
    seats = []
    seat_ids = []
    with open("input", "r") as file:
        seats = file.readlines()
    for seat in seats:
        (row, col) = get_seat(seat)
        print(seat, (row, col), get_id(row, col))
        seat_ids.append((seat, get_id(row, col)))
    seat_ids.sort()
    print(seat_ids[0])


def get_seat(seat):
    # print(seat)
    row_lower = 0
    row_size = 127
    col_lower = 0
    col_size = 7
    for char in seat:
        if char == 'F':
            row_size = floor(row_size / 2)
            pass
        elif char == 'B':
            row_lower += ceil(row_size / 2)
            row_size = floor(row_size / 2)
            pass
        elif char == 'L':
            col_size = floor(col_size / 2)
            pass
        elif char == 'R':
            col_lower += ceil(col_size / 2)
            col_size = floor(col_size / 2)
            pass
    return floor(row_lower), floor(col_lower)


def get_id(row: int, col: int):
    return (row * 8) + col


if __name__ == "__main__":
    main()
