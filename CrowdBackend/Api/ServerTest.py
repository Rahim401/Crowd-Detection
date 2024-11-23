from CrowdApi import CrowdApi
from random import choice, randint
from CrowdBackend import TestImageDir
from datetime import datetime as dt, timedelta
from CrowdBackend.Utils import AreasInBangalore, Colors, time2Str

# Base URL for the server
BASE_URL = "http://127.0.0.1:5000"

def getRandomAreaOnBangalore():
    return choice(AreasInBangalore)

class ServerTester(CrowdApi):
    def getLocationInServer(self):
        resCode, resData = self.getLocationRes(verbose=False)
        if resCode == 200: return resData["allLocations"]
        return list()
    def getRandomLocation(self):
        try:
            locations = self.getLocationRes()[1]["allLocations"]
            if locations: return choice(locations)
        except Exception as e:
            # e.with_traceback()
            pass
        return ""

    # Test /getLocations (GET)
    def testGetLocation(self):
        print("\nTesting /getLocations")
        self.getLocationRes(title="On Proper", expCodes=200)

    # Test /createLocation (POST)
    def testCreateLocation(self):
        print(f"\n{Colors.Blue}Testing /createLocation{Colors.White}")
        areasInServer = self.getLocationInServer()
        areasNotInServer = list(AreasInBangalore.difference(areasInServer))

        if areasNotInServer:
            place, address = choice(areasNotInServer), "Chuma"
            self.createLocationRes(place, address, title="On Proper Insert", expCodes=201)
        if areasInServer:
            place, address = choice(areasInServer), "Duplicate"
            self.createLocationRes(place, address, title="On Duplicate Insert", expCodes=482)
        self.createLocationRes("", "Ommbu", title="On No PlaceName", expCodes=400)
        self.createLocationRes("Oklahoma", "", title="On No Address", expCodes=400)

    # Test /postCrowdAt (POST)
    def testPostCrowdAt(self):
        print(f"\n{Colors.Blue}Testing /postCrowdAt{Colors.White}")
        areasInServer = self.getLocationInServer()
        areasNotInServer = list(AreasInBangalore.difference(areasInServer))
        fromMail, atLoc, atTime = "test@example.com", "", time2Str(dt.now())
        message, crowdAt = "Test Post of Image", randint(0, 25)
        pathOfImage = f"{TestImageDir}/WhatsApp Image 2024-10-21 at 22.10.36.jpeg"

        if areasInServer:
            locInServer = choice(areasInServer)
            self.postCrowdAtRes(
                locInServer, time2Str(dt.now()), fromMail, message, pathOfImage, crowdAt,
                title="On Proper Insert", expCodes=202
            )
            self.postCrowdAtRes(
                locInServer, time2Str(dt.now()), fromMail, message, pathOfImage, -1,
                title="On No Crowd Insert", expCodes=202
            )
            self.postCrowdAtRes(
                locInServer, time2Str(dt.now()), fromMail, message, None, crowdAt,
                title="On No Image Insert", expCodes=202
            )
            self.postCrowdAtRes(
                locInServer, atTime, fromMail, message, None, -1,
                title="On No CrowdAt and Photo", expCodes=400
            )
            self.postCrowdAtRes(
                locInServer, "", fromMail, message, pathOfImage, crowdAt,
                title="On No Time", expCodes=400
            )
            self.postCrowdAtRes(
                locInServer, time2Str(dt.now()), "", message, pathOfImage, crowdAt,
                title="On No Mail", expCodes=400
            )
            self.postCrowdAtRes(
                locInServer, time2Str(dt.now()), fromMail, "", pathOfImage, crowdAt,
                title="On No Message", expCodes=202
            )
        if areasNotInServer:
            self.postCrowdAtRes(
                choice(areasNotInServer), atTime, fromMail, message, pathOfImage, crowdAt,
                title="On Invalid Location", expCodes=404
            )
        self.postCrowdAtRes(
            "", time2Str(dt.now()), fromMail, message, pathOfImage, crowdAt,
            title="On No Location", expCodes=400
        )

    # Test /getEstimation (GET)
    def testGetEstimation(self):
        print(f"\n{Colors.Blue}Testing /getEstimation{Colors.White}")
        areasInServer = self.getLocationInServer()
        areasNotInServer = list(AreasInBangalore.difference(areasInServer))
        fromMail, atLoc, atTime = "test@example.com", "", time2Str(dt.now())

        if areasInServer:
            locInServer = choice(areasInServer)
            self.getEstimationRes(
                locInServer, time2Str(dt.now()), fromMail,
                title="On Proper Inputs", expCodes=(200, 206)
            )
            self.getEstimationRes(
                locInServer, time2Str(dt.now()-timedelta(hours=2)), fromMail,
                title="On Proper Inputs With old data", expCodes=(200, 206)
            )
            self.getEstimationRes(
                locInServer, "", fromMail,
                title="On No Time", expCodes=400
            )
            self.getEstimationRes(
                locInServer, time2Str(dt.now()), "",
                title="On No Mail", expCodes=400
            )
        if areasNotInServer:
            self.getEstimationRes(
                choice(areasNotInServer), atTime, fromMail,
                title="On Invalid Location", expCodes=404
            )
        self.getEstimationRes(
            "", time2Str(dt.now()), fromMail,
            title="On No Location", expCodes=400
        )

    # Test /getGetPhotoNear (GET)
    def testGetPhotoNear(self):
        print(f"\n{Colors.Blue}Testing /getGetPhotoNear{Colors.White}")
        areasInServer = self.getLocationInServer()
        areasNotInServer = list(AreasInBangalore.difference(areasInServer))
        fromMail, atLoc, atTime = "test@example.com", "", time2Str(dt.now())

        if areasInServer:
            locInServer = choice(areasInServer)
            self.getPhotoNearRes(
                locInServer, time2Str(dt.now()),
                title="On Proper Inputs", expCodes=(200, 206, 222)
            )
            self.getPhotoNearRes(
                locInServer, time2Str(dt.now()-timedelta(hours=2)),
                title="On Proper Inputs With old data", expCodes=(200, 206, 222)
            )
            self.getPhotoNearRes(
                locInServer, time2Str(dt.now()), recordWith="PhotoOnly",
                title="With Photo only", expCodes=(200, 222)
            )
            self.getPhotoNearRes(
                locInServer, time2Str(dt.now()), recordWith="NoPhotoOnly",
                title="With NoPhoto only", expCodes=(206, 222)
            )
            self.getPhotoNearRes(
                locInServer, "",
                title="On No Time", expCodes=400
            )
        if areasNotInServer:
            self.getPhotoNearRes(
                choice(areasNotInServer), atTime,
                title="On Invalid Location", expCodes=404
            )
        self.getPhotoNearRes(
            "", time2Str(dt.now()), fromMail,
            title="On No Location", expCodes=400
        )

    def testAll(self):
        self.testGetLocation()
        self.testCreateLocation()
        self.testPostCrowdAt()
        self.testGetEstimation()
        self.testGetPhotoNear()



# Run the test cases
if __name__ == "__main__":
    print("Testing API endpoints...")
    tester = ServerTester()
    tester.testAll()