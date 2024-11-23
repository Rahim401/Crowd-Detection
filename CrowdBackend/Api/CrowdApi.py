import base64
import requests
from datetime import datetime as dt
from CrowdBackend.Utils import Colors, time2Str

BASE_URL = "http://127.0.0.1:5000"
VerboseByDefault = True

class CrowdApi:
    # details = args, keyedDetails = kwargs
    def __init__(self, baseUrl=BASE_URL):
        self.baseUrl = baseUrl

    # /getLocations (GET)
    def getLocation(self):
        return requests.get(f"{self.baseUrl}/getLocations")
    def getLocationRes(self, verbose=VerboseByDefault, *args, **kwargs):
        try:
            response = self.getLocation()
            result = response.status_code, response.json()
        except Exception as e: result = -1, e
        if verbose:
            if "title" not in kwargs: kwargs["title"] = "GetLocation"
            if "expCodes" not in kwargs: kwargs["expCodes"] = (200, )
            logApi("/getLocation",  result, *args, **kwargs)
        return result

    # /createLocation (POST)
    def createLocation(self, place, address):
        data = {"place": place, "address": address}
        return requests.post(f"{self.baseUrl}/createLocation", json=data)
    def createLocationRes(self, place, address, verbose=VerboseByDefault, *args, **kwargs):
        try:
            response = self.createLocation(place, address)
            result = response.status_code, response.json()
        except Exception as e: result = -1, e
        if verbose:
            if "title" not in kwargs: kwargs["title"] = "CreateLocation"
            if "expCodes" not in kwargs: kwargs["expCodes"] = (201, 400, 482)
            kwargs.update(place=place, address=address)
            logApi("/createLocation",  result, *args, **kwargs)
        return result

    # /postCrowdAt (POST)
    def postCrowdAt(self, atLocation, atTime, fromMail, message, photoPath=None, crowdAt=-1):
        if isinstance(atTime, dt): atTime = time2Str(atTime)
        encodedPhoto = None
        try:
            with open(photoPath, "rb") as photoFile:
                encodedPhoto = base64.b64encode(photoFile.read()).decode("utf-8")
        except Exception: pass
        return requests.post(f"{self.baseUrl}/postCrowdAt", json={
            "atLocation": atLocation, "atTime": atTime,
            "fromMail": fromMail, "message": message,
            "photo": encodedPhoto,"crowdAt": crowdAt,
        })
    def postCrowdAtRes(
            self, atLocation, atTime, fromMail, message, photoPath=None,
            crowdAt=-1, verbose=VerboseByDefault, *args, **kwargs
    ):
        try:
            response = self.postCrowdAt(atLocation, atTime, fromMail, message, photoPath, crowdAt)
            result = response.status_code, response.json()
        except Exception as e: result = -1, e
        if verbose:
            if "title" not in kwargs: kwargs["title"] = "PostCrowdAt"
            if "expCodes" not in kwargs: kwargs["expCodes"] = (200, )
            kwargs.update(atLocation=atLocation, atTime=atTime, fromMail=fromMail, message=message, photoPath=photoPath, crowdAt=crowdAt)
            logApi("/postCrowdAt",  result, *args, **kwargs)
        return result

    # /getEstimation (GET)
    def getEstimation(self, atLocation, atTime, fromMail="noUser@crowd.com"):
        if isinstance(atTime, dt): atTime = time2Str(atTime)
        return requests.get(f"{self.baseUrl}/getEstimation", params={
            "atLocation": atLocation, "atTime": atTime,
            "fromMail": fromMail
        })
    def getEstimationRes(
            self, atLocation, atTime, fromMail="noUser@crowd.com",
            verbose=VerboseByDefault, *args, **kwargs
    ):
        try:
            response = self.getEstimation(atLocation, atTime, fromMail)
            result = response.status_code, response.json()
        except Exception as e: result = -1, e
        if verbose:
            if "title" not in kwargs: kwargs["title"] = "GetEstimation"
            if "expCodes" not in kwargs: kwargs["expCodes"] = (200, 206)
            kwargs.update(atLocation=atLocation, atTime=atTime, fromMail=fromMail)
            logApi("/getEstimation",  result, *args, **kwargs)
        return result

    # /getPhotoNear (GET)
    def getPhotoNear(self, atLocation, atTime, recordWith="PhotoOnly"):
        if isinstance(atTime, dt): atTime = time2Str(atTime)
        params = {"atLocation": atLocation, "atTime": atTime, "recordWith": recordWith}
        return requests.get(f"{self.baseUrl}/getPhotoNear", params=params)
    def getPhotoNearRes(self, atLocation, atTime, recordWith="PhotoOnly", verbose=VerboseByDefault, *args, **kwargs):
        try:
            response = self.getPhotoNear(atLocation, atTime, recordWith)
            result = response.status_code, response.json()
        except Exception as e: result = -1, e
        if verbose:
            if "title" not in kwargs: kwargs["title"] = "GetPhotoNear"
            if "expCodes" not in kwargs: kwargs["expCodes"] = (200, )
            kwargs.update(atLocation=atLocation, atTime=atTime, recordWith=recordWith)
            logApi("/getPhotoNear",  result, *args, **kwargs)
        return result

# Helper to pretty print responses
def logApi(endpoint, result, *args, **kwargs):
    try:
        expCodes = kwargs.pop("expCodes")
        if isinstance(expCodes, int): expCodes = (expCodes,)
    except: expCodes = ()
    try: title = kwargs.pop("title")
    except: title = "Unknown Api"

    if title:
        expCodeStr = ', '.join([str(fk) for fk in expCodes])
        print(f"{Colors.Blue}#### {title}({expCodeStr}) ####{Colors.White}")
        startPad = "  "
    else: startPad = "  "

    print(
        f"{startPad}*->Request, ToEndpoint: {endpoint}",
        *(f"{k}: {v}" for k, v in kwargs.items()),
        *args, sep=", "
    )
    resCode = result[0]
    if resCode >= 0 and len(result) == 2:
        resCode, resData = result
        if "photo" in resData and resData["photo"]: resData["photo"] = hash(resData["photo"])
        print(f"{startPad}|->Response({resCode}), Json:", resData, end="")
        if len(expCodes) > 0:
            if resCode in expCodes: testResMsg = f"{Colors.Green}Passed the test (✓){Colors.White}"
            else:
                if len(expCodes) == 1: testResMsg = f"Failed({resCode} != {expCodes[0]})"
                else: testResMsg = f"Failed({resCode} !in {expCodes})"
                testResMsg = f"{Colors.Red}{testResMsg} (𝒙){Colors.White}"
            print(f"\n---> {testResMsg}")
    else:
        if len(result) >= 2: error = result[1]
        else: error = Exception("Unexpected Error Occurred!")
        print(f"{startPad}|->{Colors.Red}Error({error}): {error}{Colors.White}")

if __name__ == '__main__':
    api = CrowdApi()
    api.createLocationRes("Miami", "Florida, USA", expCodes=482)