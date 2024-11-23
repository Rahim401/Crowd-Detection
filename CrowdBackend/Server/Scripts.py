import os
import random
# from cv2 import imread
from datetime import datetime, timedelta
from shutil import copy2

import cv2

from CrowdDetector import CrowdDetector
from CrowdBackend import TestImageDir
from CrowdBackend.Server.DataManager import DataManager
from CrowdBackend.Utils import AreasInBangalore, time2Str, stampImage

# 7 mock places (locations) from Bangalore
places = [(area, "Some Place") for area in AreasInBangalore]

# 5 mock email addresses
emails = [
    "user1@example.com",
    "user2@example.com",
    "user3@example.com",
    "user4@example.com",
    "user5@example.com"
]

def insertMockLocation(database):
    # Insert mock places into the Location table
    for place in places:
        database.insertLocation(place[0], place[1])

testImages = [f"{TestImageDir}/{images}" for images in os.listdir(TestImageDir)]
testImageCrowd = [20, 18, 19, 14]
def insertMockData(database, recordCount=5000):
    # Generate and insert 50 mock records
    for i in range(recordCount):
        location = random.choice(places)[0]
        fromEmail = random.choice(emails)
        message = f"Mock message at {location}"
        testImagesLen = len(testImages)
        # imagePath = random.choice(testImages)
        testImageChosen = random.randint(0, testImagesLen * 2)
        crowdCount = random.randint(0, 30)  # Random crowd count between 10 and 100

        # Random time within the past week
        randomDays = random.randint(0, 10)
        randomMinutes = random.randint(0, 1440)  # random minute in a day
        randomTime = datetime.now() - timedelta(days=randomDays, minutes=randomMinutes)
        atTimeStr = time2Str(randomTime)

        # Insert the record into the Record table
        print(f"Inserting #{i}: {location}")
        try:
            locPath = f"{DataManager.databaseDir}/{location}"
            if not os.path.exists(locPath): os.makedirs(locPath)
            photoPath = f"{DataManager.databaseDir}/{location}/{atTimeStr}.jpg"
            # copy2(imagePath, photoPath)
            if testImageChosen < testImagesLen:
                imgType = testImages[testImageChosen]
                crowdCount = testImageCrowd[testImageChosen]
            else: imgType = "blue" if i%2==0 else "red"
            image = stampImage(
                imgType, AtLocation=location, AtTime=atTimeStr,
                CrowdCount=crowdCount
            )
            cv2.imwrite(photoPath, image)
            database.insertRecord(location, atTimeStr, fromEmail, message, photoPath, crowdCount)
        except Exception as e: print(f"ErrorOn #{i}: {e}")

if __name__ == "__main__":
    dataManager = DataManager()
    # # Insert mock data
    insertMockLocation(dataManager)
    insertMockData(dataManager, 3000)

    dataManager.closeConnection()
    # print([len(CrowdDetector().detectFromPath(path)) for path in testImages])