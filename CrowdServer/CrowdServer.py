import os, json
from datetime import datetime, timedelta

from flask import Flask, request
from flask_restful import Api, Resource

from Utils import response
from DataManager import DataManager
from CrowdDetector import CrowdDetector

# Initialize Flask and Flask-RESTful
app = Flask(__name__)
api = Api(app)

# Initialize DataManager
dataManager = DataManager()
crowdDetector = CrowdDetector()
stdTimeFormat = "%Y-%m-%d %H:%M:%S"

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
                return response(400, error="Place and address are required")

            # Insert the location into the database
            if dataManager.isLocationIn(place):
                return response(409, message="Location already exists")
            dataManager.insertLocation(place, address)
            return response(201, message="Location created")
        except Exception as e:
            return response(500, error=str(e))


# Resource for /postCrowdAt (POST)
class PostCrowdAt(Resource):
    def post(self):
        try:
            # Get input from request
            data = request.form.get('data')
            if data: data = json.loads(data)
            fromMail = data.get('fromMail')
            atLocation = data.get('atLocation')
            atTime = data.get('atTime')
            crowdAt = data.get('crowdAt')
            message = data.get('message')
            image = request.files.get('image')

            if not all([fromMail, atLocation, atTime, message, image]):
                return response(400, error="Some fields are missing!")

            if not dataManager.isLocationIn(atLocation):
                return response(404, error="Location not found!")

            # Convert string time to datetime object
            atTimeStr = datetime.strptime(atTime, stdTimeFormat)

            # Save the image (as an example, saving the image to a folder)
            locPath = f"database/{atLocation}"
            if not os.path.exists(locPath): os.makedirs(locPath)
            imagePath = f"database/{atLocation}/{atTimeStr}.jpg"
            image.save(imagePath)
            if crowdAt is None or crowdAt < 0:
                crowdAt = len(crowdDetector.detectFromPath(imagePath))

            # Insert record into the database
            dataManager.insertRecord(atLocation, atTimeStr, fromMail, message, crowdAt)
            return response(201, message="Crowd record added", crowdDetected=crowdAt)
        except Exception as e:
            return response(500, error=str(e))


# Resource for /getEstimation (GET)
class GetEstimation(Resource):
    def get(self):
        try:
            args = request.args
            data = request.json
            if args:
                fromMail = args.get('fromMail')
                atLocation = args.get('atLocation')
                atTime = args.get('atTime')
            else:
                fromMail = data.get('fromMail')
                atLocation = data.get('atLocation')
                atTime = data.get('atTime')

            print(fromMail, atLocation, atTime)
            if not all([fromMail, atLocation, atTime]):
                return response(400, error="Some fields are missing!")

            # Convert string time to datetime object
            atTime = datetime.strptime(atTime, stdTimeFormat)

            # Fetch advanced crowd details from DataManager
            resCode, avgCrowd, avgCrowdOn4Hrs, lowCrowdAt, crownOnN4Hrs = \
                (dataManager.getAdvCrowdDetailsAt(atLocation, atTime))

            if resCode == 2:
                status = 200
                message = ""
                if avgCrowdOn4Hrs > avgCrowd:
                    # diff = avgCrowdOn4Hrs / avgCrowd
                    message = f"For the given time Crowd could be higher than usual!"
                else:
                    # diff = avgCrowd / avgCrowdOn4Hrs
                    message = f"For the given time Crowd should be lower than usual!"
                bestTime = atTime + timedelta(hours=lowCrowdAt)
                bestTimeStr = "now" if lowCrowdAt == 0 else bestTime.strftime("%-I%p")
                message += f"\n and {bestTimeStr} is the best time to go!"
                data = {
                    "avgCrowd": avgCrowd,
                    "avgCrowdOnNext4Hrs": avgCrowdOn4Hrs,
                    "lowCrowdAtHour": lowCrowdAt,
                    "lowCrowdTime": bestTime.strftime(stdTimeFormat),
                    "detailsNext4Hrs": crownOnN4Hrs
                }
            else:
                status = 404
                message = "Invalid Location, or No data about the location!"
                data = {}

            # Prepare response data
            return response(status, message=message, data=data)
        except Exception as e:
            return response(500, error=str(e))


# Adding routes/endpoints to the API
api.add_resource(GetLocations, '/getLocations')
api.add_resource(CreateLocation, '/createLocation')
api.add_resource(PostCrowdAt, '/postCrowdAt')
api.add_resource(GetEstimation, '/getEstimation')


if __name__ == '__main__':
    # Start the Flask server
    app.run(debug=True)
