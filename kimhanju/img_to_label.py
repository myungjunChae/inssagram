from PIL import Image
import glob
import json


json_list = []
f = open('info/info.json', 'r')
while True:
    line = f.readline()
    if not line: 
        break
    json_acceptable_string = line.replace("'", "\"")
    d = json.loads(json_acceptable_string)
    json_list.append(d)
f.close()

new_filename = ""
old_filename = ""
for filename in glob.glob('images/*.jpg'):
    old_filename = filename.split('.')[0][7:]
    image = Image.open(filename)

    for json in json_list:
        filename = filename.replace('\\', "/")
        if json['image'] == filename:
            new_filename = old_filename + '_' + str(json['like']) + '_' + str(json['comment']) + '_' + str(json['follower']) + '.jpg'
    image.save("images_label/" + new_filename)