package com.test7;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Square {
	

	
	private static double calculateAngle(double px1, double py1, double px2, double py2, double px0, double py0){
		
		double dx1 = px1 - px0;
		double dy1 = py1 - py0;
		double dx2 = px2 - px0;
		double dy2 = py2 - py0;
		
		return (dx1*dx2 + dy1*dy2) / Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }
	
	public List<MatOfPoint> getSuitableSquares (List<MatOfPoint> contours) {
		
		List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
		List<MatOfPoint> approxList = new ArrayList<MatOfPoint>();
		
		// Test contours and assemble squares out of them
	    MatOfPoint2f approxcurve = new MatOfPoint2f();
	    
	    for (int i = 0; i < contours.size(); i++){	    	
	    	double epsilon = Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true)*0.02;
	        // approximate contour with accuracy proportional to the contour perimeter
	    	Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approxcurve, epsilon, true);
	    	//System.out.println("approxcurve.size()=" + approxcurve.size());

		double contourArea = Math.abs(Imgproc.contourArea(approxcurve.clone()));
		boolean isContourConvex = Imgproc.isContourConvex(new MatOfPoint(approxcurve.toArray()));
		
		// Note: absolute value of an area is used because area may be positive or negative - in accordance with the contour orientation
        if (approxcurve.toArray().length == 4 && contourArea > 1000 && isContourConvex){
            double maxCosine = 0;
            for (int j = 2; j < 5; j++){
            	double P1X = approxcurve.toArray()[j%4].x;
            	double P1Y = approxcurve.toArray()[j%4].y;
            	double P2X = approxcurve.toArray()[j-2].x;
            	double P2Y = approxcurve.toArray()[j-2].y;
            	double P3X = approxcurve.toArray()[j-1].x;
            	double P3Y = approxcurve.toArray()[j-1].y;
                double cosine = Math.abs(calculateAngle(P1X, P1Y, P2X, P2Y, P3X, P3Y));
                maxCosine = Math.max(maxCosine, cosine);
            }
            
            System.out.println("maxCosine :: " + maxCosine);

            if (maxCosine < 0.1)
            squares.add(new MatOfPoint(approxcurve.toArray()));
        }	
 	    approxList.add(new MatOfPoint(approxcurve.toArray()));
 	    System.out.println("approxList :: " + approxList.size());
        System.out.println("number of squares :: " + squares.size());
		
		}
    
        return squares; 
	}
	
	public MatOfPoint getBiggestSquares(List<MatOfPoint> squares,List<MatOfPoint> approxList, Mat src){
	    // Find largest square
	    int max_width = 0;
	    int max_height = 0;
	    int max_square_idx = 0;
	    for (int i = 0; i < squares.size(); i++)
	    {
	        // Convert a set of 4 unordered Points into a meaningful cv::Rect structure.
	        Rect rectangle = Imgproc.boundingRect(squares.get(i));

	        // Store the index position of the biggest square found
	        if ((rectangle.width >= max_width) && (rectangle.height >= max_height))
	        {
	            max_width = rectangle.width;
	            max_height = rectangle.height;
	            max_square_idx = i;
	        }
	    }

	    MatOfPoint biggest_square = squares.get(max_square_idx); 
	    // Draw circles at the corners
	    for (int i = 0; i < biggest_square.toArray().length; i++ ){
	    	Imgproc.circle(src, biggest_square.toArray()[i], 4, new Scalar(0, 0, 255), 1);
	        System.out.println("x = " + biggest_square.toArray()[i].x + ", y = " + biggest_square.toArray()[i].y);
	        }
	    Imgcodecs.imwrite("src//data//hkid_clockwise_corners.png", src);
	    //System.out.println("biggest_square.size" + biggest_square.size());

		return biggest_square;	
	}
    

	
	public RotatedRect getRotatedRect(MatOfPoint biggest_square) {
		 	
		RotatedRect rect = null;
	    MatOfPoint mpoints = new MatOfPoint(biggest_square);
		MatOfPoint2f points2f = new MatOfPoint2f(mpoints.toArray());
		System.out.println("points2f = " + points2f.size());

		if (points2f.rows() > 0) {
		    rect = Imgproc.minAreaRect(points2f);
		}  
		
		RotatedRect box = Imgproc.minAreaRect(points2f);
	    return box;
	}
	
	
	
	public double findRotatedAngle(MatOfPoint biggest_square) {
		
		double delta_x01 = biggest_square.toArray()[0].x - biggest_square.toArray()[1].x;
	    double delta_y01 = biggest_square.toArray()[0].y - biggest_square.toArray()[1].y;
	    
	    double delta_x12 = biggest_square.toArray()[1].x - biggest_square.toArray()[2].x;
	    double delta_y12 = biggest_square.toArray()[1].y - biggest_square.toArray()[2].y;
	    
	    double x01 = Math.abs(delta_x01);
	    double y01 = Math.abs(delta_y01);
	    
	    double x12 = Math.abs(delta_x12);
	    double y12 = Math.abs(delta_y12);
	    
	    if (Math.sqrt(x01*x01 + y01*y01) > Math.sqrt(x12*x12 + y12*y12) ) {
	    	 double angle = - 180 * Math.atan(x12/y12) / Math.PI ;
	    	 System.out.println("angle = " + angle);
	    	 return angle;
		} else {
			double angle = 90 - 180 * Math.atan(x12/y12) / Math.PI ;
			System.out.println("angle = " + angle);
			return angle;
		}
	}
	
}
