package vn.fstyle.opencvtest;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Copyright © 2016 FStyleVN
 * Created by Sun on 23/10/2016.
 */

public class BlurOpenCVEffectTransaction extends BitmapTransformation {
    public BlurOpenCVEffectTransaction(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap bmp = OpenCVUtil.Effect(toTransform);
        toTransform.recycle();
        return bmp;
    }

    @Override
    public String getId() {
        return "blur OpenCV";
    }
}
