
f = open("profile_link.txt", 'r')
profile_link = []
while True:
    line = f.readline()
    if not line:
        break
    profile_link.append(line)
f.close()

profile_link = list(set(profile_link))

print(len(profile_link))
f = open("set_profile_link.txt", 'w')
for link in profile_link:
    f.write(link)
f.close()