package com.test12;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageSubstrate {
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main(String[] args) {
		   
		   Mat source = Imgcodecs.imread("src//data//bill.jpg");
		   
		   Mat image_h = Mat.zeros(source.size(), CvType.CV_8UC1);
		   Mat image_v = Mat.zeros(source.size(), CvType.CV_8UC1); 
		   
		   Mat output = new Mat();
		   Core.bitwise_not(source, output);
		   Mat output_result = new Mat();
		   
		   Mat kernel_h = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 1));
		   Imgproc.morphologyEx(output, image_h, Imgproc.MORPH_OPEN, kernel_h);
		   Imgcodecs.imwrite("src//data//output.jpg", output);	
		   
		   Core.subtract(output, image_h, output_result);
		   Imgcodecs.imwrite("src//data//output_result.jpg", output_result);	
		   
		   
		   Mat kernel_v = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1, 20));   
		   Imgproc.morphologyEx(output_result, image_v, Imgproc.MORPH_OPEN, kernel_v);
		   Mat output_result2 = new Mat();
		   
		   Core.subtract(output_result, image_v, output_result2);		   
		   Imgcodecs.imwrite("src//data//output_result2.jpg", output_result2);
	}
}
