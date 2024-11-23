import cv2
import numpy as np
from cv2 import imwrite
from PIL.Image import Image
from datetime import datetime as dt
from flask import make_response, jsonify

def avg(lst: list[int]):
    if len(lst) == 0: return 0
    return sum(lst) / len(lst)

def response(status: int = 200, *args, **kwargs):
    return make_response(jsonify(*args, **kwargs), status)

def fixAndRespond(status = 0, message="", error="", data=None):
    if data is None: data = {}
    if status == 0: status, error = 501, "Unexpected Error!"
    if status >= 400 or error: data["error"] = error
    else: data["message"] = message
    return response(status, **data)


stdTimeFormat = "%Y-%m-%d %H:%M:%S"
def str2Time(strTime: str):
    try: return dt.fromisoformat(strTime)#dt.strptime(strTime, stdTimeFormat)
    except Exception as e: return None
def time2Str(time: dt):
    try: return dt.isoformat(time, " ") #dt.strftime(time, stdTimeFormat)
    except Exception as e: return None

def saveImage(image, path):
    if isinstance(image, bytes):
        with open(path, "wb") as fl:
            fl.write(image)
            return True
    elif isinstance(image, Image):
        image.save(path)
        return True
    elif isinstance(image, np.ndarray):
        imwrite(path, image)
        return True
    return False

class Colors:
    Green, Red, White = '\033[92m', '\033[91m', '\033[0m'
    Blue, Orange = '\033[94m', '\033[93m'
    Bold, Italics = '\033[1m', '\x1B[3m'


AreasInBangalore = {
    "Cantonment", "Domlur", "Indiranagar", "Rajajinagar", "Malleswaram",
    "Pete", "Sadashivanagar", "Seshadripuram", "Shivajinagar", "Ulsoor",
    # "Vasanth Nagar", "R. T. Nagar", "Bellandur", "CV Raman Nagar", "Hoodi",
    # "Krishnarajapuram", "Mahadevapura", "Marathahalli", "Varthur", "Whitefield",
    "Banaswadi", "HBR Layout", "Horamavu", "Kalyan Nagar", "Kammanahalli",
    # "Lingarajapuram", "Ramamurthy Nagar", "Hebbal", "Jalahalli", "Mathikere",
    # "Peenya", "Vidyaranyapura", "Yelahanka", "Yeshwanthpur", "Bommanahalli", "Bommasandra",
    # "BTM Layout", "Electronic City", "HSR Layout", "Koramangala", "Madiwala", "Banashankari",
    # "Basavanagudi", "Girinagar", "J. P. Nagar", "Jayanagar", "Kumaraswamy Layout", "Padmanabhanagar",
    # "Uttarahalli", "Anjanapura", "Arekere", "Begur", "Gottigere", "Hulimavu", "Kothnur",
    # "Basaveshwaranagar", "Kamakshipalya", "Kengeri", "Mahalakshmi Layout", "Nagarbhavi",
    # "Nandini Layout", "Nayandahalli", "Rajajinagar", "Rajarajeshwari Nagar", "Vijayanagar",
    # "Devanahalli", "Hoskote", "Bidadi", "Bannerghatta", "Hosur"
}

def stampImage(type="red", **kwargs):
    font = cv2.FONT_HERSHEY_SIMPLEX
    fontColor = (255, 255, 255)
    fontScale, fontThick = 1, 2
    startTextPos, nextYIncr = (10, 70), 35
    image = None
    imageShape = (854, 480)
    imageRevShape = reversed((854, 480))

    if type not in ("red", "blue"):
        try:
            image = cv2.imread(type)
            image = cv2.resize(image, imageShape)
            # fontScale, fontThick = 3, 5
            fontColor = (0, 255, 255)
        except Exception as e: pass
    if image is None:
        image = np.zeros((*imageRevShape, 3), np.uint8)
        if type == "red": image[:] = (0, 0, 255)
        elif type == "blue": image[:] = (255, 0, 0)
        else: image[:] = (0, 0, 0)

    textNextYPos = startTextPos[1]
    for key, value in kwargs.items():
        cv2.putText(
            image, f"{key}: {value}", (startTextPos[0], textNextYPos),
            font, fontScale, fontColor, fontThick, cv2.LINE_AA,
        )
        textNextYPos += nextYIncr
    return image

if __name__ == '__main__':
    photoPath = "/media/rahim401/DevStuffs/Some Projects/Crowd Detection/CrowdBackend/TestImages/WhatsApp Image 2024-10-21 at 22.10.36.jpeg"
    image = stampImage(
        Location="Pes Canteen", AtTime="10:30PM",
        type="blue"
    )
    cv2.imshow("Ommbu", image)
    cv2.waitKey(-1)
