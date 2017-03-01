package com.test11;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class HoughLineTransform {
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) {
		
		//Canny process before HoughLine Recognition
		
		Mat source = Imgcodecs.imread("src//data//bill.jpg");
		Mat gray = new Mat(source.rows(),source.cols(),CvType.CV_8UC1);
		Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);
		
		Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		
		Mat Linek = Mat.zeros(source.size(), CvType.CV_8UC1);
		//x =  Imgproc.morphologyEx(gray, dst, op, kernel, anchor, iterations);
		Imgproc.morphologyEx(gray, Linek,Imgproc.MORPH_CLOSE, element);
		Imgcodecs.imwrite("src//data//linek.jpg", Linek);
		
			}
	}
