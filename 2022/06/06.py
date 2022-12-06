# could be inlined for fewer lines of code but that would be very inefficient
text = open('input', 'r').read().strip()

# q1
print([len(set(list(group))) for group in [text[i-4:i] for i in range(14, len(text))]].index(4) + 4)

# q2 (same but with 14 instead of 4
print([len(set(list(group))) for group in [text[i-14:i] for i in range(14, len(text))]].index(14) + 14)
