package vn.fstyle.opencvtest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

/**
 * Copyright © 2016 FStyleVN
 * Created by Sun on 23/10/2016.
 */

public class PermissionUtil {
    private PermissionUtil() {
        // No-op
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isCameraPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isReadExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isWriteExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }
}
