import re

p = re.compile(r"(.*);.*#(.*)E\d+\.\d+ (.*)")

with open('./src/jvmMain/resources/15-emoji-test.txt', 'r') as f:
    lines = (line.strip() for line in f.readlines())
    lines = filter(lambda l: ';' in l and 'unqualified' not in l, lines)
    r = map(lambda l: p.search(l), lines)
    r = filter(lambda s: s, r)
    r = map(lambda s: s.groups(), r)

with open('./src/jvmMain/resources/emojis.csv', 'w') as f:
    f.writelines(("{};{};{}\n".format(e[0].strip(), e[1].strip(), e[2].strip()) for e in r))
