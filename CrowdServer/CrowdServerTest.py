import base64
import requests, json
from Utils import Colors
from random import randint, choice
from datetime import datetime as dt

# Base URL for the server
BASE_URL = "http://127.0.0.1:5000"
TIMEFORMAT = "%Y-%m-%d %H:%M:%S"

def formatTime(time): return time.strftime(TIMEFORMAT)

# Helper to pretty print responses
def printRes(endpoint, response, *details, **keyedDetails):
    try: expectedCode = keyedDetails.pop("expectedCode")
    except: expectedCode = -1
    try: title = keyedDetails.pop("title")
    except: title = None
    title2Print = f"#### {(f"{title}({expectedCode})" \
       if(expectedCode>=0) else title)} ####" if title else ""
    startPad = "  " if title2Print else ""

    if title2Print: print(title2Print)
    if "photo" in keyedDetails and keyedDetails["photo"]:
        keyedDetails["photo"] = hash(keyedDetails["photo"])
    print(
        f"{startPad}*->Request, ToEndpoint: {endpoint}",
        *(f"{k}: {v}" for k, v in keyedDetails.items()),
        *details, sep=", "
    )
    # print(f"Status Code: {response.status_code}")
    statusCode = response.status_code
    try:
        responseData = response.json()
        if "photo" in responseData and responseData["photo"]:
            responseData["photo"] = hash(responseData["photo"])
        testResult2Print = "" if(expectedCode < 0) else f"{Colors.Green}Passed (✓){Colors.White}" \
            if(expectedCode == statusCode) else f"{Colors.Red}Failed({expectedCode} != {statusCode}){Colors.White} (x)"
        print(f"{startPad}|->Response({statusCode}), Json:", responseData, end="")
        print("\n--->" if title2Print else "", testResult2Print)
    except Exception as e:
        print(f"{startPad}|->Error({statusCode}: {e}), Text:", response.text)

class ServerTest:
    # details = args, keyedDetails = kwargs
    @staticmethod
    def getRandomAreaOnBangalore():
        try:
            areasInBangalore = [
                "Cantonment","Domlur","Indiranagar","Rajajinagar","Malleswaram",
                "Pete","Sadashivanagar","Seshadripuram","Shivajinagar","Ulsoor",
                "Vasanth Nagar","R. T. Nagar","Bellandur","CV Raman Nagar","Hoodi",
                "Krishnarajapuram","Mahadevapura","Marathahalli","Varthur","Whitefield",
                "Banaswadi","HBR Layout","Horamavu","Kalyan Nagar","Kammanahalli",
                "Lingarajapuram","Ramamurthy Nagar","Hebbal","Jalahalli","Mathikere",
                "Peenya","Vidyaranyapura","Yelahanka","Yeshwanthpur","Bommanahalli","Bommasandra",
                "BTM Layout","Electronic City","HSR Layout","Koramangala","Madiwala","Banashankari",
                "Basavanagudi","Girinagar","J. P. Nagar","Jayanagar","Kumaraswamy Layout","Padmanabhanagar",
                "Uttarahalli","Anjanapura","Arekere","Begur","Gottigere","Hulimavu","Kothnur",
                "Basaveshwaranagar","Kamakshipalya","Kengeri","Mahalakshmi Layout","Nagarbhavi",
                "Nandini Layout","Nayandahalli","Rajajinagar","Rajarajeshwari Nagar","Vijayanagar",
                "Devanahalli","Hoskote","Bidadi","Bannerghatta","Hosur"
            ]
            if areasInBangalore: return choice(areasInBangalore)
        except Exception as e: e.with_traceback()
        return ""


    @staticmethod
    def getRandomLocation():
        try:
            locations = requests.get(f"{BASE_URL}/getLocations").json()["allLocations"]
            if locations: return choice(locations)
        except Exception as e: e.with_traceback()
        return ""

    @staticmethod # 1. Test /getLocations (GET)
    def getLocation(*args, **kwargs):
        response = requests.get(f"{BASE_URL}/getLocations")
        printRes("/getLocations", response, *args, **kwargs)

    @staticmethod # 2. Test /createLocation (POST)
    def createLocation(place, address, *args, **kwargs):
        data = {"place": place, "address": address}
        response = requests.post(f"{BASE_URL}/createLocation", json=data)
        if isinstance(data, dict): kwargs.update(data)
        if isinstance(data, list): args = data.extend(args)
        printRes("/createLocation", response, *args, **kwargs)

    @staticmethod # 3. Test /postCrowdAt (POST)
    def postCrowdAt(data, photo_path, *args, **kwargs):
        encodedPhoto = None
        try:
            with open(photo_path, "rb") as photoFile:
                encodedPhoto = base64.b64encode(photoFile.read()).decode("utf-8")
        except FileNotFoundError: pass

        data["photo"] = encodedPhoto
        response = requests.post(f"{BASE_URL}/postCrowdAt", json=data)
        if isinstance(data, dict): kwargs.update(data)
        if isinstance(data, list): args = data.extend(args)
        printRes("/postCrowdAt", response, *args, **kwargs)

    @staticmethod # 4. Test /getEstimation (GET)
    def getEstimation(params, *args, **kwargs):
        response = requests.get(f"{BASE_URL}/getEstimation", params=params)
        if isinstance(params, dict): kwargs.update(params)
        if isinstance(params, list): args = params.extend(args)
        printRes("/getEstimation", response, *args, **kwargs)

    @staticmethod # 5. Test /getPhotoNear (GET)
    def getPhotoNear(params, *args, **kwargs):
        response = requests.get(f"{BASE_URL}/getPhotoNear", params=params)
        if isinstance(params, dict): kwargs.update(params)
        if isinstance(params, list): args = params.extend(args)
        printRes("/getPhotoNear", response, *args, **kwargs)

# Run the test cases
if __name__ == "__main__":
    print("Testing API endpoints...")

    # Test 1: /getLocations
    print("\n1.Testing /getLocations")
    ServerTest.getLocation(title="On Proper Input", expectedCode=200)

    # Test 2: /createLocation
    print("\n2.Testing /createLocation")
    ServerTest.createLocation(
        ServerTest.getRandomAreaOnBangalore(), "Area in Bangalore",
        title="On Proper Input", expectedCode=201
    )
    ServerTest.createLocation("", "", title="On No Input Provided", expectedCode=400)  # Failure scenario
    ServerTest.createLocation("Marri", "", title="On No Place", expectedCode=400)  # Failure scenario
    ServerTest.createLocation("", "Palaya", title="On No Address", expectedCode=400)  # Failure scenario

    # Test 3: /postCrowdAt
    print("\n3.Testing /postCrowdAt")
    ServerTest.postCrowdAt({
        "fromMail": "test@example.com",
        # "atLocation": "Rajaji@1321",
        "atLocation":  ServerTest.getRandomLocation(),
        "atTime": formatTime(dt.now()),  # .strftime("%Y-%m-%d %H:%M:%S"),
        "crowdAt": -1,#randint(0, 25),
        "message": "Moderate crowd"
    }, "testImages/WhatsApp Image 2024-10-21 at 22.10.36.jpeg", title="On Proper Input",
        expectedCode=201)  # Replace with a valid image path
    ServerTest.postCrowdAt({
        "fromMail": "test@example.com",
        "atLocation": "HDBHIBHBhudhBHUBub",
        "atTime": formatTime(dt.now()),  # .strftime("%Y-%m-%d %H:%M:%S"),
        "crowdAt": randint(0, 25),
        "message": "Moderate crowd"
    }, "testImages/WhatsApp Image 2024-10-21 at 22.10.36.jpeg", title="On Invalid Location",
        expectedCode=404)  # Replace with a valid image path
    ServerTest.postCrowdAt({
        "fromMail": "test@example.com",
        "atLocation": "",
        "atTime": formatTime(dt.now()),  # .strftime("%Y-%m-%d %H:%M:%S"),
        "crowdAt": randint(0, 25),
        "message": "Moderate crowd"
    }, "testImages.jpeg", title="On Location Missing", expectedCode=400)  # Missing location

    # Test 4: /getEstimation
    print("\n4.Testing /getEstimation")
    ServerTest.getEstimation({
        "fromMail": "test@example.com",
        # "atLocation": "Rajaji@1321",
        "atLocation":  ServerTest.getRandomLocation(),
        "atTime": formatTime(dt.now()),#.strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Proper Input", expectedCode=200)
    ServerTest.getEstimation({
        "fromMail": "test@example.com",
        "atLocation": "Park",
        "atTime": formatTime(dt.now()),#.strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Invalid Location", expectedCode=404)
    ServerTest.getEstimation({
        "fromMail": "test@example.com",
        "atLocation": "",
        "atTime": formatTime(dt.now())#strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Location Missing", expectedCode=400)

    # Test 5: /getPhotoNear
    print("\n5.Testing /getPhotoNear")
    ServerTest.getPhotoNear({
        # "atLocation": "Rajaji@1321",
        "atLocation":  ServerTest.getRandomLocation(),
        "atTime": formatTime(dt.now())#strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Proper Input", expectedCode=200)
    ServerTest.getPhotoNear({
        "atLocation": "Park",
        "atTime": formatTime(dt.now())#strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Invalid Location", expectedCode=404)
    ServerTest.getPhotoNear({
        "atLocation": "",
        "atTime": formatTime(dt.now())#strftime("%Y-%m-%d %H:%M:%S")
    }, title="On Location Missing", expectedCode=400)
    ServerTest.getPhotoNear({
        # "atLocation": "Rajaji@1321",
        "atLocation":  ServerTest.getRandomLocation(),
        "atTime": "2024-11-20 23:20:41",
    }, title="Accessing old Record", expectedCode=200)
    ServerTest.getPhotoNear({
        # "atLocation": "Rajaji@1321",
        "atLocation":  ServerTest.getRandomLocation(),
        "atTime": "2024-11-20 23:32:45",
    }, title="Accessing Record with no photo", expectedCode=200)
