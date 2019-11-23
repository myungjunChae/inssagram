from selenium import webdriver
import urllib.request
import selenium
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException
import json
import argparse

class InstagramCrawler(object):
    def __init__(self, username):
        self.driver = webdriver.Chrome(r'chromedriver')
        self.username = username

    def crawl(self):
        driver = self.driver

        driver.get('https://www.instagram.com/' + self.username)

        WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CLASS_NAME, "_6q-tv")))
        image = driver.find_element(By.CLASS_NAME, "_6q-tv").get_attribute("src")

        post = driver.find_elements(By.CLASS_NAME, "Y8-fY")[0].find_element(By.CLASS_NAME, 'g47SY').text
        post = post.replace(',', '')

        follower = driver.find_elements(By.CLASS_NAME, "Y8-fY")[1].find_element(By.CLASS_NAME, 'g47SY').get_attribute("title")
        follower = follower.replace(',', '')

        print(image)
        print(post, follower)
        image_info = {'image': image, 'post': post, 'follower': follower}
        json_info = json.dumps(image_info)
        return json_info


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--username', type=str, help='user name')
    args = parser.parse_args()
    
    crawl = InstagramCrawler(args.username)
    crawl.crawl()