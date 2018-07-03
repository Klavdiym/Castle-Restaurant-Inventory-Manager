package com.example.lordi.restaurantcastlesuppliesmanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

//Big thanks to the InventoryApp-Master creator on showing how to neatly organize database/calls and packages
public class ImageUtility {

    /**
     * Class converts selected images for use in the list
     */
    public static Bitmap uriToBitmap(Context context, Uri selectedImageUri, int REQUIRED_SIZE) {

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImageUri),
                    null, options);

            int width = options.outWidth;
            int height = options.outHeight;
            int scale = ProductUtility.ONE;

            while (true) {
                if (width / ProductUtility.TWO < REQUIRED_SIZE || height / ProductUtility.TWO < REQUIRED_SIZE) {
                    break;
                }
                width /= ProductUtility.TWO;
                height /= ProductUtility.TWO;
                scale *= ProductUtility.TWO;
            }
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = scale;
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImageUri),
                    null, options2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * to convert bitmap to byte[]
     *
     * @param bitmap image as bitmap will be compressed as PNG and ZERO quality
     * @return as a ByteArrayOutputStream object
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, ProductUtility.ZERO, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * to convert byte[] to bitmap
     *
     * @param imageByteArray from db
     * @return as Bitmap
     */
    public static Bitmap byteArrayToBitmap(byte[] imageByteArray) {
        if (imageByteArray == null) return null;
        return BitmapFactory.decodeByteArray(imageByteArray, ProductUtility.ZERO, imageByteArray.length);
    }

    /**
     * to convert image resource id to bitmap
     *
     * @param context    of the activity
     * @param idResource of the image wanted to be converted
     * @return bitmap
     */
    private static Bitmap intIdResourceToBitmap(Context context, int idResource) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeResource(context.getResources(), idResource, options);
    }

    /**
     * one method to convert @param imageResourceId to byte[] directly
     *
     * @param context         of the activity
     * @param imageResourceId wanted to be converted
     * @return byte[]
     */
    public static byte[] intResourceIdToByteArray(Context context, int imageResourceId) {
        return bitmapToBytes(intIdResourceToBitmap(context, imageResourceId));
    }

    /**
     * one method to convert @param selectedImageUri to byte[] directly
     *
     * @param context          of the activity
     * @param selectedImageUri wanted to be converted
     * @param REQUIRED_SIZE    wanted image size
     * @return byte[]
     */
    public static byte[] uriToByteArray(Context context, Uri selectedImageUri, int REQUIRED_SIZE) {
        return bitmapToBytes(uriToBitmap(context, selectedImageUri, REQUIRED_SIZE));
    }
}
