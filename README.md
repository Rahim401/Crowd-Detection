# Crowd Detection Server

This repository contains the **Crowd Detection Server**—a Python-based RESTful server using Flask to detect and manage crowd data for different locations. It interacts with a SQLite database to store and retrieve crowd information, using image processing (via OpenCV and YOLO) to analyze crowd density from uploaded images.

## Features

- **Location Management**: Manage different locations where crowd data is collected.
- **Crowd Detection**: Analyze images to detect and estimate crowd counts at a specific time.
- **Historical Data and Predictions**: Estimate crowd density based on historical data for informed decision-making on future visits.
- **RESTful Endpoints**: Seamlessly interact with the server using HTTP methods for location creation, crowd detection, and crowd density prediction.

## Directory Structure

```plaintext
CrowdDetectionServer/
├── database/                # Folder containing SQLite database files
├── model/                   # YOLO model files (e.g., yolov3.cfg, yolov3.weights, coco.names)
├── testImages/              # Sample images for testing
├── CrowdDetector.py         # YOLO-based crowd detection class
├── DataManager.py           # SQLite data management class
├── CrowdServer.py           # Flask server with RESTful API endpoints
├── Utils.py                 # Utility functions, e.g., response formatting
└── requirements.txt         # Python dependencies
```

## Getting Started

### Prerequisites

- **Python 3.6+**
- **YOLO Model Files**: Place `yolov3.cfg`, `yolov3.weights`, and `coco.names` in the `model` directory for crowd detection.
- **Database**: A pre-populated SQLite database will be created on server initialization.

### Installing Dependencies

Install required Python packages with:

```bash
pip install -r requirements.txt
```

### Starting the Server

1. **Start the Flask Server**:
   - Run the server from `CrowdServer.py`:
     ```bash
     python CrowdServer.py
     ```
   - The server runs on `localhost:16420` by default.

## REST API Endpoints

The Crowd Detection Server offers various endpoints to interact with the data.

### 1. **Get Locations**

   - **Endpoint**: `/getLocations`
   - **Method**: `GET`
   - **Description**: Retrieves a list of all registered locations.
   - **Response**: JSON list of locations.

### 2. **Create Location**

   - **Endpoint**: `/createLocation`
   - **Method**: `POST`
   - **Parameters**: JSON object with `place` and `address`.
   - **Description**: Registers a new location.
   - **Response**: Success message if location is created or error if location exists.

### 3. **Post Crowd Data**

   - **Endpoint**: `/postCrowdAt`
   - **Method**: `POST`
   - **Parameters**:
     - **Form Data**:
       - `fromMail`: Email of the user.
       - `atLocation`: Location of the crowd data.
       - `atTime`: Time in `YYYY-MM-DD HH:MM:SS` format.
       - `message`: Optional message.
       - `image`: Image file for crowd detection.
       - `crowdAt` (optional): Manual crowd count.
   - **Description**: Uploads crowd data at a location and time.
   - **Response**: JSON with detected crowd count.

### 4. **Get Estimation**

   - **Endpoint**: `/getEstimation`
   - **Method**: `GET`
   - **Parameters**:
     - **Query Params**: `fromMail`, `atLocation`, `atTime` in `YYYY-MM-DD HH:MM:SS`.
   - **Description**: Provides advanced crowd estimation based on historical data.
   - **Response**: JSON with crowd predictions, recommended times, and other stats.

## Technical Details

### Crowd Detection Model

The YOLO (You Only Look Once) model processes each uploaded image to detect the crowd density. The model configurations and weights are stored in the `model` folder and are loaded on server initialization.

### Data Management

The server uses SQLite to store location and crowd information:
- **Location**: Stores unique locations.
- **Record**: Logs crowd data records with timestamps for historical insights.

### Utilities

- **Utils.py**: Contains helper functions such as JSON response formatting.

## Testing the Server

1. **Using Curl**:
   - Get Locations:
     ```bash
     curl -X GET http://localhost:16420/getLocations
     ```
   - Create Location:
     ```bash
     curl -X POST -H "Content-Type: application/json" -d '{"place": "MG Road", "address": "Bangalore"}' http://localhost:16420/createLocation
     ```
2. **Using Postman**: Import API details and send requests to test different endpoints.

## Troubleshooting

- **Database Initialization**: If the database is not found, it will be created in the `database` directory.
- **Image Not Found**: Ensure images are uploaded correctly in `POST /postCrowdAt` requests.

## Future Enhancements

- **Web Interface**: Implement a frontend to interact with the server.
- **Real-Time Notifications**: Set up crowd alerts based on current trends.