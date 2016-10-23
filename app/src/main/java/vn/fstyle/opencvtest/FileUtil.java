package vn.fstyle.opencvtest;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * Copyright © 2016 FStyleVN
 * Created by Sun on 23/10/2016.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static final String ARG_PATH = "path";
    public static final String ARG_SIZE = "size";
//    public static final String ARG_MIME = "mime";
    public static final String PARSE_URI = "content://downloads/public_downloads";

    private FileUtil() {
        // No-op
    }

    public static File createImageFile(@NonNull Context context) throws IOException {
        //Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName =
                context.getString(R.string.app_name) + System.currentTimeMillis() + "_";
        imageFileName = imageFileName.replaceAll("\\s", "");
        File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);
        return imageFile;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)//
    public static Hashtable<String, Object> getFileInfo(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Hashtable<String, Object> value = new Hashtable<>();
                    value.put(ARG_PATH, Environment.getExternalStorageDirectory() + "/" + split[1]);
                    value.put(ARG_SIZE, (int) new File((String) value.get(ARG_PATH)).length());
//                    value.put(ARG_MIME, "application/octet-stream");

                    return value;
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri =
                        ContentUris.withAppendedId(Uri.parse(PARSE_URI), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isNewGooglePhotosUri(uri)) {
                Hashtable<String, Object> value = getDataColumn(context, uri, null, null);
                Bitmap bitmap;
                try {
                    InputStream input = context.getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(input);
                    File file = File.createTempFile("sendbird", ".jpg");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
                            new BufferedOutputStream(new FileOutputStream(file)));
                    value.put(ARG_PATH, file.getAbsolutePath());
                    value.put(ARG_SIZE, (int) file.length());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                return value;
            } else {
                return getDataColumn(context, uri, null, null);
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Hashtable<String, Object> value = new Hashtable<String, Object>();
            value.put(ARG_PATH, uri.getPath());
            value.put(ARG_SIZE, (int) new File((String) value.get(ARG_PATH)).length());
//            value.put(ARG_MIME, "application/octet-stream");

            return value;
        }

        return null;
    }

    private static Hashtable<String, Object> getDataColumn(Context context, Uri uri,
                                                           String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
        };

        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(column_index);

//                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                String mime = cursor.getString(column_index);

                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int size = cursor.getInt(column_index);

                Hashtable<String, Object> value = new Hashtable<String, Object>();
                if (path == null) {
                    path = "";
                }

                value.put(ARG_PATH, path);
//                value.put(ARG_MIME, mime);
                value.put(ARG_SIZE, size);

                return value;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
}
