import os
import cv2
import numpy as np

class CrowdDetector:
    mainPath = f"{os.getcwd()}/model"
    cfgPath = f"{mainPath}/yolov3.cfg"
    weightPath = f"{mainPath}/yolov3.weights"
    classNamePath = f"{mainPath}/coco.names"

    def __init__(self, modelCfg=cfgPath, modelWeights=weightPath, classNamesFile=classNamePath):
        # Load the class names (COCO dataset)
        with open(classNamesFile, 'r') as f:
            self.classes = [line.strip() for line in f.readlines()]

        # Load the YOLO network
        self.net = cv2.dnn.readNet(modelWeights, modelCfg)

        # Set the preferable backend and target (CPU/GPU)
        self.net.setPreferableBackend(cv2.dnn.DNN_BACKEND_OPENCV)
        self.net.setPreferableTarget(cv2.dnn.DNN_TARGET_CPU)

        # Detection thresholds
        self.confThreshold = 0.55  # Confidence threshold
        self.nmsThreshold = 0.4   # Non-Max Suppression threshold

    # Function to get the output layer names
    def getOutputLayers(self):
        layerNames = self.net.getLayerNames()
        return [layerNames[i - 1] for i in self.net.getUnconnectedOutLayers()]

    # Detect people from an ndarray image
    def detect(self, image):
        height, width = image.shape[:2]

        # Prepare the image for YOLO (blob creation)
        blob = cv2.dnn.blobFromImage(image, 1/255.0, (416, 416), (0, 0, 0), True, crop=False)
        self.net.setInput(blob)

        # Run forward pass and get the output layers
        outs = self.net.forward(self.getOutputLayers())

        classIds = []
        confidences = []
        boxes = []

        # Process the output layers
        for out in outs:
            for detection in out:
                scores = detection[5:]  # Ignore the first 5 elements (bounding box info)
                classId = np.argmax(scores)
                confidence = scores[classId]

                if confidence > self.confThreshold and classId == 0:  # Detecting people (classId = 0)
                    centerX = int(detection[0] * width)
                    centerY = int(detection[1] * height)
                    w = int(detection[2] * width)
                    h = int(detection[3] * height)

                    x = centerX - w // 2
                    y = centerY - h // 2

                    classIds.append(classId)
                    confidences.append(float(confidence))
                    boxes.append([x, y, w, h])

        # Apply Non-Max Suppression to remove redundant overlapping boxes
        indices = cv2.dnn.NMSBoxes(boxes, confidences, self.confThreshold, self.nmsThreshold)

        # Return the positions and confidences of detected people
        results = []
        for i in indices:
            box = boxes[i]
            x, y, w, h = box
            results.append({"position": (x, y, w, h), "confidence": confidences[i]})

        return results

    # Detect people from an image file path
    def detectFromPath(self, imagePath):
        # Load the image from the provided path
        image = cv2.imread(imagePath)
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
            cv2.putText(image, label, (x - 10, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)
        return image

# Write an FlaskRestApi based python server running in port 16420, Which will contains an database folder, with Various locations as Subfolders, with an db.pk an pickel file, where we store de
if __name__ == "__main__":
    detector = CrowdDetector()
    image = cv2.imread(f"{os.getcwd()}/testImages/WhatsApp Image 2024-10-21 at 22.10.37 (1).jpeg")
    if image is not None:
        det = detector.detect(image)
        res = detector.drawDetections(image, det)
        cv2.imshow(f"Crowd count: {len(det)}", res)
        cv2.waitKey(0)
        cv2.destroyAllWindows()