package com.example.budgetingappv1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;

public class Add_Category_Icon_Utils {
    // get bitmap of an image from its bytes
    public static Bitmap getImage(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    // get bytes of an image from an input stream
    public static byte[] getBytes(InputStream inputStream) throws IOException{
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while((len = inputStream.read(buffer)) != -1){
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}
