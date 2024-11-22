import random
from PIL import Image
# from cv2 import imread
from datetime import datetime, timedelta
from CrowdServer import DataManager

# 7 mock places (locations) from Bangalore
places = [
    ("MG Road", "MG Road, Bangalore"),
    ("Koramangala", "Koramangala, Bangalore"),
    ("Indiranagar", "Indiranagar, Bangalore"),
    ("Whitefield", "Whitefield, Bangalore"),
    ("Jayanagar", "Jayanagar, Bangalore"),
    ("Electronic City", "Electronic City, Bangalore"),
    ("Yelahanka", "Yelahanka, Bangalore")
]

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

def insertMockData(database, recordCount=5000):
    # Generate and insert 50 mock records
    for _ in range(recordCount):
        location = random.choice(places)[0]
        fromEmail = random.choice(emails)
        message = f"Mock message at {location}"
        crowdCount = random.randint(3, 30)  # Random crowd count between 10 and 100

        # Random time within the past week
        randomDays = random.randint(0, 7)
        randomMinutes = random.randint(0, 1440)  # random minute in a day
        randomTime = datetime.now() - timedelta(days=randomDays, minutes=randomMinutes)

        # Insert the record into the Record table
        database.insertRecord(location, randomTime.strftime("%Y-%m-%d %H:%M:%S"), fromEmail, message, "", crowdCount)

if __name__ == "__main__":
    dataManager = DataManager()

    # dataManager.insertRecord(
    #     "Jayanagar4", "2014-10-20 10:10:17", "hrahim401",
    #     "Hi Da", "fuck", 21
    # )
    # # Insert mock data
    # insertMockLocation(dataManager)
    # insertMockData(dataManager, 100)

    dataManager.insertRecordFromPhoto(
        "Jayanagar", "2014-10-25 10:10:20", "hrahim401",
        "Hi Da", Image.open("/media/rahim401/DevStuffs/Some Projects/Crowd Detection Exp/CrowdServer/WhatsApp Image 2024-10-21 at 22.10.37.jpeg")
    )

    # for (place, _) in places:
    #     print(dataManager.getAvgCrowdOf(place))
        # print(place, dataManager.getCrowdAt(place, datetime.now()))
        # records = dataManager.getRecordsByLocation(place)
        # for record in records:
        #     print(*record, sep=", ")



    dataManager.closeConnection()