// deskew without frames
package com.test13;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class EdgeDetection {
	
	static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

	public static void main( String[] args ) throws Exception{		
		//cvtColor()将彩色图转换成灰度图
		Mat src = Imgcodecs.imread("src//data//inclined_text.jpg");
		Mat src_gray = new Mat();
		Imgproc.cvtColor(src, src_gray, Imgproc.COLOR_BGR2GRAY);
		Imgcodecs.imwrite("src//data//inclined_text_src_gray.jpg", src_gray);

		Mat output = new Mat();
		Core.bitwise_not(src_gray, output);
		Imgcodecs.imwrite("src//data//inclined_text_output.jpg", output);

		Mat points = Mat.zeros(output.size(),output.type());  //新建一个和矩阵output大小、类型一样的矩阵points
		Core.findNonZero(output, points);	

		MatOfPoint mpoints = new MatOfPoint(points);	//以points为参数构造出MatOfPoint类型矩阵
		MatOfPoint2f points2f = new MatOfPoint2f(mpoints.toArray());
		RotatedRect box = Imgproc.minAreaRect(points2f);

		Mat src_squares = src.clone();
		Mat rot_mat = Imgproc.getRotationMatrix2D(box.center, box.angle, 1);
		Mat rotated = new Mat(); 
		Imgproc.warpAffine(src_squares, rotated, rot_mat, src_squares.size(), Imgproc.INTER_CUBIC);
		Imgcodecs.imwrite("src//data//inclined_text_squares_rotated.jpg",rotated);    
	}
}
