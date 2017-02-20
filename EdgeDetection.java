package com.test7;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class EdgeDetection {
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
	
	public static void main( String[] args ) throws Exception
	{
		Mat src = Imgcodecs.imread("src//data//hkid2.png");
		Mat src_gray = new Mat();
		/**
		 * cvCvtColor(...)，是Opencv里的颜色空间转换函数，可以实现RGB颜色向HSV,HSI等颜色空间的转换，也可以转换为灰度图像。
		 * 参数CV_RGB2GRAY是RGB到gray。参数 CV_GRAY2RGB是gray到RGB。处理结果是彩色的，则转灰色就是了。
		 * 代碼的意思是：將src轉換成灰度圖像并存儲在src_gray的Mat實例中。
		*/
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);

	    // Blur helps to decrease the amount of detected edges
		/**
		 * blur的作用是对输入的图像src进行均值滤波后用dst输出
		 * 第一个参数，InputArray类型的src，输入图像，即源图像，填Mat类的对象即可。
		 * 该函数对通道是独立处理的，且可以处理任意通道数的图片，但需要注意，
		 * 待处理的图片深度应该为CV_8U, CV_16U, CV_16S, CV_32F 以及 CV_64F之一。
		 * 第二个参数，OutputArray类型的dst，即目标图像，需要和源图片有一样的尺寸和类型。
		 * 比如可以用Mat::Clone，以源图片为模板，来初始化得到如假包换的目标图。
		 * 第三个参数，Size类型（对Size类型稍后有讲解）的ksize，内核的大小。
		 * 一般这样写Size( w,h )来表示内核的大小( 其中，w 为像素宽度， h为像素高度)。
		 * Size（3,3）就表示3x3的核大小，Size（5,5）就表示5x5的核大小
		 * 一般來講，核越大，圖像越模糊
		 * */
		//生成單通道灰度圖filtered
	    Mat filtered = new Mat();
	    Imgproc.blur(src_gray, filtered, new Size(3, 3));
	    Imgcodecs.imwrite("D:\\out_blur.jpg", filtered);
	    
	    // Detect edges
	    //生成單通道黑白圖edges
	    Mat edges = new Mat();
	    double thresh = 128;
	    Imgproc.Canny(filtered, edges, thresh, thresh/5);
	    Imgcodecs.imwrite("D:\\out_edges.jpg", edges);
	    
	    // Dilate helps to connect nearby line segments
	    //生成膨脹圖
	    Mat dilated_edges = new Mat();
	    Imgproc.dilate(edges, dilated_edges, new Mat(), new Point(-1, -1), 2); // default 3x3 kernel
	    Imgcodecs.imwrite("D:\\out_dilated.jpg", dilated_edges);
	    
	    // Find contours and store them in a list
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(dilated_edges, contours, new Mat(), Imgproc.CHAIN_APPROX_NONE,Imgproc.CHAIN_APPROX_SIMPLE);
	    
	    
	    // Test contours and assemble squares out of them
	    List<MatOfPoint> squares = new ArrayList<MatOfPoint>();
	    List<MatOfPoint> approxList = new ArrayList<MatOfPoint>();
	    for (int i = 0; i < contours.size(); i++)
	    {
	    	MatOfPoint2f approx = new MatOfPoint2f();
	    	
	        // approximate contour with accuracy proportional to the contour perimeter
	    	Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approx, Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true)*0.02, true);
	    	
	        // Note: absolute value of an area is used because area may be positive or negative - in accordance with the contour orientation
	        if (approx.toArray().length == 4 && Math.abs(Imgproc.contourArea(approx.clone())) > 1000 &&
	        	Imgproc.isContourConvex(new MatOfPoint(approx.toArray())))
	        {
	            double maxCosine = 0;
	            for (int j = 2; j < 5; j++)
	            {
	            	double P1X = approx.toArray()[j%4].x;
	            	double P1Y = approx.toArray()[j%4].y;
	            	double P2X = approx.toArray()[j-2].x;
	            	double P2Y = approx.toArray()[j-2].y;
	            	double P3X = approx.toArray()[j-1].x;
	            	double P3Y = approx.toArray()[j-1].y;
	                double cosine = Math.abs(calculateAngle(P1X, P1Y, P2X, P2Y, P3X, P3Y));
	                maxCosine = Math.max(maxCosine, cosine);
	            }
	            System.out.println("maxCosine :: " + maxCosine);

	            if (maxCosine < 0.1)
                squares.add(new MatOfPoint(approx.toArray()));
	        }	        
	        approxList.add(new MatOfPoint(approx.toArray()));
	    }
	    
	    System.out.println("contours :: " + contours.size());
	    System.out.println("approxList :: " + approxList.size());
	    System.out.println("squares :: " + squares.size());
	    
	    // Draw all detected squares
	    Mat src_squares = src.clone();
	    for (int i = 0; i < approxList.size(); i++)
	    {
	        Imgproc.polylines(src_squares, squares, true, new Scalar(0, 255, 0), 2);
	    }
	    Imgcodecs.imwrite("D:\\out_squares.jpg", src_squares);
	    
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
	    for (int i = 0; i < biggest_square.toArray().length; i++ )
	    	Imgproc.circle(src, biggest_square.toArray()[i], 4, new Scalar(0, 0, 255), 1);
	    Imgcodecs.imwrite("D:\\out_corners.jpg", src);
	}

	private static double calculateAngle(double px1, double py1, double px2, double py2, double px0, double py0){
		
		double dx1 = px1 - px0;
		double dy1 = py1 - py0;
		double dx2 = px2 - px0;
		double dy2 = py2 - py0;
		
		return (dx1*dx2 + dy1*dy2) / Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);

    /*    double numerator = P2Y*(P1X-P3X) + P1Y*(P3X-P2X) + P3Y*(P2X-P1X);
        double denominator = (P2X-P1X)*(P1X-P3X) + (P2Y-P1Y)*(P1Y-P3Y);
        double ratio = numerator/denominator;

        double angleRad = Math.atan(ratio);
        double angleDeg = (angleRad*180)/Math.PI;

        if(angleDeg<0){
            angleDeg = 180+angleDeg;
        }
        return angleDeg;*/
    }
}
