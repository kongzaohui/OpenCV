package com.test7;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class EdgeDetection {
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }


	public static void main( String[] args ) throws Exception{
		

		//cvtColor()将彩色图转换成灰度图
		Mat src = Imgcodecs.imread("src//data//hkid_clockwise_4.png");
		Mat src_gray = new Mat();
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgcodecs.imwrite("src//data//hkid_clockwise_src_gray.png", src_gray);

	    // Blur helps to decrease the amount of detected edges;生成單通道灰度圖filtered
	    Mat filtered = new Mat();
	    Imgproc.blur(src_gray, filtered, new Size(5, 5));
	    Imgcodecs.imwrite("src//data//hkid_clockwise_blur.png", filtered);		
	    
	    // Detect edges;生成單通道黑白圖edges
	    Mat edges = new Mat();
	    double thresh = 32;
	    Imgproc.Canny(filtered, edges, thresh/5, thresh);
	    Imgcodecs.imwrite("src//data//hkid_clockwise_edges.png", edges);
	    
	    // Dilate helps to connect nearby line segments;生成膨脹圖
	    Mat dilated_edges = new Mat();
	    Imgproc.dilate(edges, dilated_edges, new Mat(), new Point(-1, -1), 2); // default 3x3 kernel
	    Imgcodecs.imwrite("src//data//hkid_clockwise_dilated.png", dilated_edges);
	    
	    // Find contours and store them in a list
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(dilated_edges, contours, new Mat(), Imgproc.CHAIN_APPROX_NONE,Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("number of contours :: " + contours.size());
        
		Square square_class = new Square();  
	   List<MatOfPoint> squares = square_class.getSuitableSquares(contours);
	   List<MatOfPoint> approxList = new ArrayList<MatOfPoint>();

	   
	   MatOfPoint biggest_square = square_class.getBiggestSquares(squares, approxList, src_gray);
	   
		// Draw all detected squares
	    Mat src_squares = src.clone();
	    for (int i = 0; i < approxList.size(); i++){
	        Imgproc.polylines(src_squares, squares, true, new Scalar(0, 255, 0), 2);
	    }
	    Imgcodecs.imwrite("src//data//hkid_clockwise_squares.png", src_squares);
	   
	    
		Mat rot_mat = Imgproc.getRotationMatrix2D(square_class.getRotatedRect(biggest_square).center, square_class.findRotatedAngle(biggest_square), 1);
		Mat rotated = new Mat(); 
		Imgproc.warpAffine(src_squares, rotated, rot_mat, src_squares.size(), Imgproc.INTER_CUBIC);
		Imgcodecs.imwrite("src//data//hkid_clockwise_squares_rotated.png",rotated);    
	}

	
}

