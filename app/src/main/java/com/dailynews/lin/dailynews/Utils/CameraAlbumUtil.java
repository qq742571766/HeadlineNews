package com.dailynews.lin.dailynews.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by z-Lin on 2017/2/27.
 */

public class CameraAlbumUtil {
    private static Context mContext;
    private static File outputImage;//存放摄像头拍下的图片
    private static Uri imageUri;//图片路径的URI
    private static final int TAKE_PHOTO = 1;//拍照
    private static final int OPEN_ALBUM_ON_NOUGAT = 2;//7.0及以上打开相册
    private static final int FINAL_RESULT_CODE = 3;//裁剪以后返回的结果码
    private static final int OPEN_ALBUM_BELOW_NOUGAT = 22;//7.0以下打开相册

    public CameraAlbumUtil(Context context) {
        mContext = context;
        outputImage = new File(mContext.getExternalCacheDir(), "headPic.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isOnNougat()) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(mContext,
                    "com.dailynews.lin.dailynews", outputImage);
        }
    }

    /**
     * 选择拍照作为头像
     * 需在<application>标签内注册一个<provider>,其中authorities属性为自定义，
     * <meta-data>标签内resource属性指定了Uri的共享路径，引用了一个自定义资源。
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="com.zlin.myapp.myfileprovider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_paths"/>
     * </provider>
     * 在res下新建文件夹xml，然后新建一个资源文件，根节点为paths，<external-path>
     * 内name属性为自定义，path属性值为空表示将整个SD卡进行共享
     * <paths xmlns:android="http://schemas.android.com/apk/res/android">
     * <external-path name="my_images" path=""/>
     * </paths>
     */
    public void takePhoto() {
        //启动相机程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //通过MediaStore.EXTRA_OUTPUT这个Key告诉相机我想把数据保存到photoUri这个位置
        if (isOnNougat()) {//7.0及以上
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        ((Activity) mContext).startActivityForResult(intent, TAKE_PHOTO);
    }

    //相册
    public void openAlbum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        if (isOnNougat()) {//如果大于等于7.0使用FileProvider
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ((Activity) mContext).startActivityForResult(intent, OPEN_ALBUM_ON_NOUGAT);
        } else {
            ((Activity) mContext).startActivityForResult(intent, OPEN_ALBUM_BELOW_NOUGAT);
        }
    }

    //对应activity的onActivityResult()方法
    public static Bitmap onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (isOnNougat()) {
                        Uri inputUri = FileProvider.getUriForFile(mContext,
                                "com.dailynews.lin.dailynews", outputImage);//通过FileProvider创建一个content类型的Uri
                        startPhotoZoom(inputUri);//设置输入类型
                    } else {
                        Uri inputUri = Uri.fromFile(outputImage);
                        startPhotoZoom(inputUri);
                    }
                }
                break;

            case OPEN_ALBUM_ON_NOUGAT:
                File imgUri = new File(GetImagePath.getPath(mContext, data.getData()));
                Uri dataUri = FileProvider.getUriForFile(mContext,
                        "com.dailynews.lin.dailynews", imgUri);
                startPhotoZoom(dataUri);
                break;

            case OPEN_ALBUM_BELOW_NOUGAT:
                startPhotoZoom(data.getData());
                break;

            case FINAL_RESULT_CODE:
                try {
                    if (resultCode == RESULT_OK) {
                        if (isOnKiaKat()) {
                            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                                    .openInputStream(imageUri));
                            if (bitmap != null) {
                                return bitmap;
                            }
                        } else if (isOnNougat()) {
                            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                                    .openInputStream(data.getData()));
                            if (bitmap != null) {
                                return bitmap;
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
        return null;
    }

    //裁剪
    private static void startPhotoZoom(Uri inputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (isOnNougat()) {
            Uri outPutUri = Uri.fromFile(outputImage);
            intent.setDataAndType(inputUri, "image/*");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
            intent.putExtra("noFaceDetection", false);//去除默认的人脸识别，否则和剪裁匡重叠
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            Uri outPutUri = Uri.fromFile(outputImage);
            if (isOnKiaKat()) {
                //这个方法是处理4.4以上图片返回的Uri对象不同的处理方法
                String url = GetImagePath.getPath(mContext, inputUri);
                intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
            } else {
                intent.setDataAndType(inputUri, "image/*");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        }
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //裁剪之后的图片最终以Uri形式返回到onActivityResult()方法
        ((Activity) mContext).startActivityForResult(intent, FINAL_RESULT_CODE);
    }

    //判断运行设备的系统版本是否高于或等于Android7.0
    private static boolean isOnNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    //判断运行设备的系统版本是否高于或等于Android4.4
    private static boolean isOnKiaKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 从Android4.4开始，选取相册中的图片不再返回图片真实的Uri，而是一个封装过了的Uri，
     * 如：content://com.android.providers.media.documents/document/image:3951
     * 导致无法根据图片路径来裁剪，所以需要对该Uri进行解析
     */
    private static class GetImagePath {
        //  4.4以上  content://com.android.providers.media.documents/document/image:3952
        //  4.4以下  content://media/external/images/media/3951
        @SuppressLint("NewApi")
        private static String getPath(final Context context, final Uri uri) {
            // DocumentProvider
            if (isOnKiaKat() && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
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
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment();
                }
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
            return null;
        }

        //Android 4.4以下版本自动使用该方法
        private static String getDataColumn(Context context, Uri uri, String selection,
                                            String[] selectionArgs) {
            Cursor cursor = null;
            final String column = "_data";
            final String[] projection = {column};
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                        null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int index = cursor.getColumnIndexOrThrow(column);
                    return cursor.getString(index);
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            return null;
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is ExternalStorageProvider.
         */
        private static boolean isExternalStorageDocument(Uri uri) {
            return "com.android.externalstorage.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is DownloadsProvider.
         */
        private static boolean isDownloadsDocument(Uri uri) {
            return "com.android.providers.downloads.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is MediaProvider.
         */
        private static boolean isMediaDocument(Uri uri) {
            return "com.android.providers.media.documents".equals(uri.getAuthority());
        }

        /**
         * @param uri The Uri to check.
         * @return Whether the Uri authority is Google Photos.
         */
        private static boolean isGooglePhotosUri(Uri uri) {
            return "com.google.android.apps.photos.content".equals(uri.getAuthority());
        }
    }
}
