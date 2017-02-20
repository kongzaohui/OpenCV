package com.test8;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

class FaceDetection {
	// Load the native library.
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
	
	public static void main(String[] args) {

		System.out.println("\nRunning DetectFaceDemo");
		
		CascadeClassifier faceDetector = new CascadeClassifier("D:\\AntonKONG\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
		Mat image = Imgcodecs.imread("src//data//hkid2.png");
		
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		
		System.out.println(String.format("Faces detected: %s ", faceDetections.toArray().length));
		
		for (Rect rect : faceDetections.toArray()) {
			Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 3);
		}
		
		String filename = "detcSuccessful.png";
		Imgcodecs.imwrite(filename, image);
    	
	}
}




