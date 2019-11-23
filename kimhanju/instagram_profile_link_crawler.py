from selenium import webdriver
import urllib.request
import selenium, time
from time import sleep
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class InstagramCrawler(object):
    def __init__(self):
        self.driver = webdriver.Chrome(r'chromedriver')
        self.baseURL = "https://www.instagram.com"
        self.authURL = self.baseURL + '/accounts/login/'

    def login(self):
        driver = self.driver

        my_username = ''
        my_password = ''

        driver.get(self.authURL)
        sleep(5)

        inp_username = driver.find_element(By.NAME, "username")
        inp_password = driver.find_element(By.NAME, "password")

        inp_username.send_keys(my_username)
        inp_password.send_keys(my_password)

        driver.find_element_by_css_selector(".L3NKy").click()
        sleep(5)
    
    def crawl(self, hashtag):
        self.login()
        driver = self.driver
        driver.get(self.baseURL + '/explore/tags/' + str(hashtag))
        driver.implicitly_wait(3)

        while(1):
            image_url_list = driver.find_elements(By.CLASS_NAME, "v1Nh3")
            print(len(image_url_list))
            profile_link_list = []
            for i in range(len(image_url_list)):
                profile_link_list.append(image_url_list[i].find_element(By.TAG_NAME, 'a').get_attribute("href"))

            f = open('profile_link.txt', 'a')
            for link in profile_link_list:
                f.write(link)
                f.write('\n')
            f.close()

            driver.find_element_by_tag_name('body').send_keys(Keys.END)
            sleep(3)


if __name__ == '__main__':
    crawl = InstagramCrawler()
    crawl.crawl('여행에미치다')
    sleep(3)