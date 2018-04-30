package com.example.cpsadmin.cps.image_processing;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paola Leon on 4/30/2018.
 * Image defines all image processing operations supported by PDT Imaging Helper app
 */

public class Image {

    /* imageToMat converts ImageView to Mat
    @params: the imageView that is going to be converted
    @return: the equivalent Mat
     */
    public static Mat imageToMat(ImageView view) {
        BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
        Bitmap originalBitmap = drawable.getBitmap();
        Mat mat = new Mat(originalBitmap.getHeight(), originalBitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(originalBitmap, mat);
        return mat;
    }

    /* matToImage converts a matrix to an ImageView using a Bitmap
    @params: the Mat that needs to be converted, the ImageView in which the result will be displayed
    @return: void
     */
    public static void matToImage(Mat mat, ImageView image){
        Bitmap tempBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(mat, tempBitmap);
        image.setImageBitmap(tempBitmap);
    }

    /* sixteenColors applies a multicolor LUT to the image
    @params: the image in Mat form
    @return: the Mat with the LUT applied
     */
    public static Mat sixteenColors(Mat mat) {
        Mat processedMat = new Mat(mat.cols(), mat.rows(), CvType.CV_8UC3);
        Imgproc.applyColorMap(mat, processedMat, Imgproc.COLORMAP_HSV);
        return processedMat;
    }

    /* getChannel extracts any RGB channel from the image
    @params: the Mat we need to extract the channels from, an integer that defines the channel to be extracted
        channel 1: Red
        channel 2: Blue
        channel 3: Green
    @return: the Mat with the specified channel only
     */
    public static Mat getChannel(Mat mat, int channel) {
        Mat redMat = new Mat(mat.cols(), mat.rows(), CvType.CV_8UC1);
        Mat blueMat = new Mat(mat.cols(), mat.rows(), CvType.CV_8UC1);
        Mat greenMat = new Mat(mat.cols(), mat.rows(), CvType.CV_8UC1);
        List<Mat> channels = new ArrayList<Mat>();
        channels.add(redMat);
        channels.add(blueMat);
        channels.add(greenMat);
        Core.split(mat, channels);
        return channels.get(channel);
    }

    /* adjustBrightness modifies the brightness of the image
     @params: the Mat we need to modify, the amount of progress in the seekbar selected
     @return: a new Mat with the brightness modified
     */
    public static Mat adjustBrightness(Mat mat, double progress) {
        Mat copy = mat.clone();
        mat.convertTo(copy,-1,1.0, progress);
        return copy;
    }

    /* adjustContrast modifies the contrast of the image
    @params: the Mat we need to modify, the amount of progress in the seekbar selected
    @return: a new Mat with contrast modified
     */
    public static Mat adjustContrast(Mat mat, double progress) {
        Mat copy = mat.clone();
        mat.convertTo(copy,-1, progress, 0);
        return copy;
    }

    /* getAlpha is a helper method for adjusting contrast/brightness. Calculates the alpha value
        given the input from the seekbar
    @params: the progress selected in the seekbar
    @return: the scaled alpha value between 1.0 and 3.0
     */
    public static double getAlpha(int i) {
        double alpha = ((double) i )/100 + 1;
        Log.e("value", "Progress is " + i);
        Log.e("value", "Alpha is " +  alpha);
        return alpha;
    }
}
