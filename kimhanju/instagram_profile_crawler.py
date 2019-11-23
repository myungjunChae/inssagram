from selenium import webdriver
import urllib.request
import selenium
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import json

class InstagramCrawler(object):
    def __init__(self):
        self.driver = webdriver.Chrome(r'chromedriver')
        self.profile_link_list = []
        f = open('profile_link.txt', 'r')
        while True:
            line = f.readline()
            if not line:
                break
            self.profile_link_list.append(line)
        f.close()
    
    def crawl(self):
        driver = self.driver
        start = 10963
        end = 20000
        for i in range(start, end):
            driver.get(self.profile_link_list[i])
            WebDriverWait(driver, 5).until(EC.presence_of_element_located((By.TAG_NAME, "body")))
            if driver.find_element(By.TAG_NAME, "body").text.find('죄송합니다. 페이지를 사용할 수 없습니다.') != -1:
                print(str(i) + " page deleted")
                continue

            try:
                WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CLASS_NAME, "FFVAD")))
            except TimeoutException:
                print(str(i) + " image fault")
                continue
            image = driver.find_element(By.CLASS_NAME, "FFVAD").get_attribute("srcset")
            image = image.split(' ')[0]
            image_file = 'images/image'+str(i)+'.jpg'

            try:
                if len(driver.find_elements(By.CLASS_NAME, "Nm9Fw")) != 0:
                    like = driver.find_elements(By.CLASS_NAME, "Nm9Fw")[0].find_element(By.TAG_NAME, 'span').text
                    like = like.replace(',', '')
                else:
                    like = 0
            except NoSuchElementException:
                like = 1

            owner = driver.find_element(By.CLASS_NAME, "BrX75").text
            comment_list = driver.find_elements(By.CLASS_NAME, "Mr508")
            comment_count = 0
            for comment in comment_list:
                if comment.find_element(By.CLASS_NAME, '_6lAjh').text == owner:
                    continue
                comment_count += 1

            driver.get('https://www.instagram.com/' + owner)
            WebDriverWait(driver, 5).until(EC.presence_of_element_located((By.CLASS_NAME, "Y8-fY")))
            follower = driver.find_elements(By.CLASS_NAME, "Y8-fY")[1].find_element(By.CLASS_NAME, 'g47SY').get_attribute("title")
            follower = follower.replace(',', '')

            if image == '':
                print(str(i) + " empty image")
                continue
            urllib.request.urlretrieve(image, image_file)
            image_info = {'image': image_file, 'like': like, 'comment': str(comment_count), 'follower': follower}
            json_info = json.dumps(image_info)
            f = open('info/info.json', 'a')
            f.write(json_info)
            f.write('\n')
            f.close()

            print(str(i) + " complete")


if __name__ == '__main__':
    crawl = InstagramCrawler()
    crawl.crawl()