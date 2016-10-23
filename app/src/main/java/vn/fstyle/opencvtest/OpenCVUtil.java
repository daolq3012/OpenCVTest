package vn.fstyle.opencvtest;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * Copyright © 2016 FStyleVN
 * Created by Sun on 23/10/2016.
 */

public class OpenCVUtil {
    private OpenCVUtil() {
        // No-op
    }

    public static Bitmap Effect(Bitmap input) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(),
                input.getHeight(),
                Bitmap.Config.ARGB_8888);
        Mat source = new Mat();
        Utils.bitmapToMat(input, source);
        Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(source, source, new Size(3, 3), 0);
        Imgproc.adaptiveThreshold(source, source, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 5, 4);
        Utils.matToBitmap(source, output);
        return output;
    }
}
