import base64
import os, json
from datetime import datetime, timedelta

from flask import Flask, request
from flask_restful import Api, Resource

from CrowdDetector import crowdDetector
from Utils import response, time2Str, str2Time, saveImage
from DataManager import DataManager

# Initialize Flask and Flask-RESTful
app = Flask(__name__)
api = Api(app)

# Initialize DataManager
dataManager = DataManager()

# Resource for /getLocations (GET)
class GetLocations(Resource):
    def get(self):
        try:
            # Fetch all locations from the database
            locations = dataManager.getAllLocations()
            # Extract only the location names
            allLocations = [location[0] for location in locations]
            return response(allLocations=allLocations)
        except Exception as e:
            return response(500, error=str(e))


# Resource for /createLocation (POST)
class CreateLocation(Resource):
    def post(self):
        try:
            # Get input from request
            data = request.json
            place = data.get('place')
            address = data.get('address')

            if not place or not address:
                return response(400, error="Place and address are required!")

            # Insert the location into the database
            if dataManager.isLocationIn(place):
                return response(409, message="Location already exists!", place=place)
            dataManager.insertLocation(place, address)
            return response(201, message="Location created!")
        except Exception as e:
            return response(500, error=str(e))


# Resource for /postCrowdAt (POST)
class PostCrowdAt(Resource):
    def post(self):
        try:
            # Get input from request
            data = request.json
            fromMail = data.get('fromMail')
            atLocation = data.get('atLocation')
            atTime = data.get('atTime')
            crowdAt = data.get('crowdAt')
            message = data.get('message')
            encodedPhoto = data.get('photo')
            try: photoBytes = base64.b64decode(encodedPhoto)
            except TypeError: photoBytes = None

            if not all([fromMail, atLocation, atTime, photoBytes]):
                # print([(i, bool(i)) for i in [fromMail, atLocation, atTime, photoBytes]])
                return response(400, error="Some fields are missing!")

            if not dataManager.isLocationIn(atLocation):
                return response(404, error="Location not found!")

            if photoBytes is not None:
                # Save the image (as an example, saving the image to a folder)
                locPath = f"database/{atLocation}"
                if not os.path.exists(locPath): os.makedirs(locPath)
                photoPath = f"database/{atLocation}/{atTime}.jpg"

                if not saveImage(photoBytes, photoPath):
                    photoPath = ""
            else: photoPath = ""

            if crowdAt is None or crowdAt < 0:
                if photoBytes is None: crowdAt = 0
                else: crowdAt = len(crowdDetector.detectFromPath(photoPath))
            dataManager.insertRecord(
                atLocation, atTime, fromMail, message,
                photoPath, crowdAt
            )
            return response(201, message="Crowd record added!", crowdDetected=crowdAt)
        except Exception as e:
            return response(500, error=str(e))


# Resource for /getEstimation (GET)
class GetEstimation(Resource):
    def get(self):
        try:
            args = request.args
            fromMail = args.get('fromMail')
            atLocation = args.get('atLocation')
            atTime = args.get('atTime')

            # print(fromMail, atLocation, atTime)
            if not all([fromMail, atLocation, atTime]):
                return response(400, error="Some fields are missing!")

            # Fetch advanced crowd details from DataManager
            result = dataManager.getAdvCrowdDetailsAt(atLocation, atTime)
            if result[0] == 2 and len(result) == 5:
                resCode, avgCrowd, avgCrowdOn4Hrs, \
                    lowCrowdAt, crownOnN4Hrs = result
                status = 200
                if avgCrowdOn4Hrs > avgCrowd:
                    # diff = avgCrowdOn4Hrs / avgCrowd
                    message = f"For the given time Crowd could be higher than usual!"
                else:
                    # diff = avgCrowd / avgCrowdOn4Hrs
                    message = f"For the given time Crowd should be lower than usual!"
                atTimeInTime = str2Time(atTime)
                bestTime = atTimeInTime + timedelta(hours=lowCrowdAt)
                bestTimeStr = "now" if lowCrowdAt == 0 else bestTime.strftime("%-I%p")
                message += f"\n and {bestTimeStr} is the best time to go!"
                data = {
                    "avgCrowd": avgCrowd,
                    "avgCrowdOnNext4Hrs": avgCrowdOn4Hrs,
                    "lowCrowdAtHour": lowCrowdAt,
                    "lowCrowdTime": time2Str(bestTime),
                    "detailsNext4Hrs": crownOnN4Hrs
                }
            else:
                status = 404
                message = "Invalid Location, or No data about the location!"
                data = {}

            # Prepare response data
            return response(status, message=message, **data)
        except Exception as e:
            e.with_traceback()
            return response(500, error=str(e))

# Resource for /getPhotoNear (GET)
class GetPhotoNear(Resource):
    def get(self):
        try:
            args = request.args
            atLocation = args.get('atLocation')
            atTime = args.get('atTime')

            if not all([atLocation, atTime]):
                return response(400, error="Some fields are missing!")

            # Query the database for the nearest record with an image
            result = dataManager.getRecordNear(atLocation, atTime)

            if result[0] > 0 and len(result) == 4:
                status = 200
                message = "Done!"
                encodedPhoto = None
                resCode, recordTime, photoPath, crowdCount = result

                try:
                    with open(photoPath, "rb") as photoFile:
                        encodedPhoto = base64.b64encode(photoFile.read()).decode("utf-8")
                except: pass

                data = {
                    "accCode": resCode,
                    "recordTime": recordTime,
                    "photo": encodedPhoto,
                    "crowdInPhoto": crowdCount
                }
            else:
                status = 404
                message = "Invalid Location, or No data about the location at given Time!"
                data = {}

            # Prepare response data
            return response(status, message=message, **data)
        except Exception as e:
            e.with_traceback()
            return response(500, error=str(e))


# Adding routes/endpoints to the API
api.add_resource(GetLocations, '/getLocations')
api.add_resource(CreateLocation, '/createLocation')
api.add_resource(PostCrowdAt, '/postCrowdAt')
api.add_resource(GetEstimation, '/getEstimation')
api.add_resource(GetPhotoNear, '/getPhotoNear')


if __name__ == '__main__':
    # Start the Flask server
    app.run(debug=True)
