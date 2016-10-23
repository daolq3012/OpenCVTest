package vn.fstyle.opencvtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private static final int GET_FILE_REQUEST_CODE = 101;
    private static final int REQUEST_PERMISSION = 102;

    private ImageView mImgRootPhoto, mImgResultPhoto;

    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImgRootPhoto = (ImageView) findViewById(R.id.img_root_photo);
        mImgResultPhoto = (ImageView) findViewById(R.id.img_result_photo);
    }

    public void onClickSelectPhoto(View view) {
        checkPermissionOS6();
    }

    private void checkPermissionOS6() {
        if (PermissionUtil.isCameraPermissionOn(this)
                && PermissionUtil.isReadExternalPermissionOn(this)
                && PermissionUtil.isWriteExternalPermissionOn(this)) {
            getPhoto();
            return;
        }
        String[] permissions = {
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
    }

    private void getPhoto() {
        // Camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = FileUtil.createImageFile(this);
            mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
        } catch (IOException e) {
            mCameraPhotoPath = null;
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        // Gallery
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");

        Intent[] intents;
        if (cameraIntent != null && mCameraPhotoPath != null) {
            intents = new Intent[]{cameraIntent};
        } else {
            intents = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, gallery);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);

        startActivityForResult(chooserIntent, GET_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            onClickSelectPhoto(null);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != GET_FILE_REQUEST_CODE || resultCode != RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            mCameraPhotoPath = null;
            return;
        }
        Uri[] results = null;

        if (data == null || data.getData() == null) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                mCameraPhotoPath = null;
            }
        } else {
            Uri dataUri = data.getData();
            Hashtable<String, Object> info = FileUtil.getFileInfo(this, dataUri);
            String imagePath = (String) info.get(FileUtil.ARG_PATH);
            results = new Uri[]{Uri.fromFile(new File(imagePath))};
        }
        // Fill root photo into image view
        Glide.with(this)
                .load(results[0])
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .into(mImgRootPhoto);
        // Fill effect photo into image view
        Glide.with(this)
                .load(results[0])
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .transform(new BlurOpenCVEffectTransaction(this))
                .into(mImgResultPhoto);
    }
}
