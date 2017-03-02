package com.test11;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class GetVerticalOrHorizonalLines {
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main(String[] args) {
		
		//Canny process before HoughLine Recognition
		
		Mat source = Imgcodecs.imread("src//data//bill.jpg");
		Mat gray = new Mat(source.rows(),source.cols(),CvType.CV_8UC1);
		Imgproc.cvtColor(source, gray, Imgproc.COLOR_BGR2GRAY);
		
		Mat binary = new Mat();
		Imgproc.adaptiveThreshold(gray, binary, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, -2);
		Imgcodecs.imwrite("src//data//binary.jpg", binary);
		
		Mat horizontal = binary.clone();
		int horizontalsize = horizontal.cols() / 30;
		int verticalsize = horizontal.rows() / 30;
		
		Mat horizontal_element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(horizontalsize,1));
		//Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		Imgcodecs.imwrite("src//data//horizontal_element.jpg", horizontal_element);
		
		Mat Linek = Mat.zeros(source.size(), CvType.CV_8UC1);
		//x =  Imgproc.morphologyEx(gray, dst, op, kernel, anchor, iterations);
		Imgproc.morphologyEx(gray, Linek,Imgproc.MORPH_BLACKHAT, horizontal_element);
		Imgcodecs.imwrite("src//data//bill_RECT_Blackhat.jpg", Linek);
		
		Mat vertical_element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(1,verticalsize));
		Imgcodecs.imwrite("src//data//vertical_element.jpg", vertical_element);
		
		Mat Linek2 = Mat.zeros(source.size(), CvType.CV_8UC1);
		//x =  Imgproc.morphologyEx(gray, dst, op, kernel, anchor, iterations);
		Imgproc.morphologyEx(gray, Linek2,Imgproc.MORPH_CLOSE, vertical_element);
		Imgcodecs.imwrite("src//data//bill_RECT_Blackhat2.jpg", Linek2);
		
			}
	}
