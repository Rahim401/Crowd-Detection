import numpy as np
from cv2 import imwrite
from PIL.Image import Image
from datetime import datetime as dt
from flask import make_response, jsonify

def avg(lst: list[int]): return sum(lst) / len(lst)

def response(status: int = 200, *args, **kwargs):
    return make_response(jsonify(*args, **kwargs), status)


stdTimeFormat = "%Y-%m-%d %H:%M:%S"
def str2Time(strTime: str):
    try: return dt.strptime(strTime, stdTimeFormat)
    except: return None
def time2Str(time: dt):
    try: return dt.strftime(time, stdTimeFormat)
    except: return None

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
    Bold, Italics = '\033[1m', '\x1B[3m'