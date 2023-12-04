def main():
    with open('input', 'r') as file:
        data = file.readlines()
        for i in range(len(data)):
            data[i] = data[i].replace('\n', '')
        counter = 0
        for dat in data:
            (policy, passwd) = dat.split(': ')
            (policy_range, policy_char) = policy.split(' ')
            (policy_lower, policy_upper) = policy_range.split('-')
            policy_lower = int(policy_lower)-1
            policy_upper = int(policy_upper)-1

            # print(passwd, policy_lower, policy_upper)
            if len(passwd) > policy_lower:
                if len(passwd) > policy_upper:
                    counter += int((passwd[policy_lower] == policy_char) ^ (passwd[policy_upper] == policy_char))
                else:
                    counter += int(passwd[policy_lower] == policy_char)
                # print(passwd[policy_lower + 1] == policy_char ^ passwd[policy_upper + 1] == policy_char)

        print(counter)


if __name__ == "__main__":
    main()

# # # pt 1 # # #
# def main():
#     with open('input', 'r') as file:
#         data = file.readlines()
#         for i in range(len(data)):
#             data[i] = data[i].replace('\n', '')
#         counter = 0
#         for dat in data:
#             (policy, passwd) = dat.split(':')
#             (policyRange, policyChar) = policy.split(' ')
#             (policyRangeLower, policyRangeUpper) = policyRange.split('-')
#             if int(policyRangeLower) <= passwd.count(policyChar) <= int(policyRangeUpper):
#                 counter += 1
#         print(counter)
