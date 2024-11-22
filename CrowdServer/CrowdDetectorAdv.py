import cv2
from ultralytics import YOLO

class CrowdDetector:
    def __init__(self, modelPath="model/yolov5xu.pt", confThreshold=0.25):
        # Load YOLOv8 model
        self.model = YOLO(modelPath)
        self.confThreshold = confThreshold  # Confidence threshold

    # Detect people from an ndarray image
    def detect(self, image):
        # Run the YOLOv8 model on the input image
        results = self.model(image)

        # Extract detections
        detections = results[0]  # The first result corresponds to the input image
        height, width = image.shape[:2]

        results = []
        for detection in detections.boxes:
            confidence = float(detection.conf[0])
            classId = int(detection.cls[0])

            # Filter detections by confidence and class (classId 0 = "person" in COCO dataset)
            if confidence > self.confThreshold and classId == 0:
                x1, y1, x2, y2 = detection.xyxy[0]  # Bounding box coordinates
                results.append({
                    "position": (int(x1), int(y1), int(x2 - x1), int(y2 - y1)),
                    "confidence": confidence
                })

        return results

    # Detect people from an image file path
    def detectFromPath(self, imagePath):
        # Load the image from the provided path
        image = cv2.imread(imagePath)
        if image is None:
            raise FileNotFoundError(f"Image at {imagePath} could not be loaded.")
        return self.detect(image)

    # Draw bounding boxes on the image and return the updated image
    def drawDetections(self, image, detections):
        for detection in detections:
            x, y, w, h = detection['position']
            confidence = detection['confidence']
            label = f"Person: {confidence:.2f}"
            color = (0, 255, 0)  # Green for person detection

            # Draw bounding box and label on image
            cv2.rectangle(image, (x, y), (x + w, y + h), color, 2)
            cv2.putText(image, label, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
        return image

# Instantiate the CrowdDetector
crowdDetector = CrowdDetector()

if __name__ == "__main__":
    # imagePath = f"{os.getcwd()}/testImages/WhatsApp Image 2024-10-21 at 22.10.37 (1).jpeg"
    imagePath = "/home/rahim401/Pictures/Screenshots/Screenshot from 2024-11-20 02-16-40.png"
    image = cv2.imread(imagePath)
    if image is not None:
        # Detect and annotate the image
        detections = crowdDetector.detect(image)
        annotatedImage = crowdDetector.drawDetections(image, detections)

        # Display the results
        cv2.imshow(f"Crowd count: {len(detections)}", annotatedImage)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        print("Error: Image could not be loaded.")
