package tv.bain.bainsocial.datatypes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.DatabaseHelper;

public class Texture implements Serializable {
    public static ArrayList<Texture> textureList; //this array list is used to store Texture Objects.
    private String UUID; //UUID is a HASH Value of the uploaded image
    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
    public String getUUID() {
        return UUID;
    }

    private String ImageString; //image string is the String Value of the Image which can be manipulated or stored.
    public void setImageStringD(String ImageString) {
        this.ImageString = ImageString;
    }
    public String getImageString() {
        return ImageString;
    }

    public Texture(){};

    public Texture(JSONObject object){
        try {
            this.UUID = object.getString(DatabaseHelper.I_ID);
            this.ImageString = object.getString(DatabaseHelper.I_STRING);
        } catch (JSONException e) { BAINServer.getInstance().SendToast(e.getMessage()); }
    }

    public Texture(String UUID, String ImageString) {
        this.UUID = UUID;
        this.ImageString = ImageString;
    }

    public static Bitmap bitmapToMutable(Bitmap bitmap) {
        try {
            File file = new File("/mnt/sdcard/sample/temp.txt");
            file.getParentFile().mkdirs();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, width * height * 4);
            bitmap.copyPixelsToBuffer(map);

            bitmap.recycle(); //recycle the source bitmap, this will be no longer used.
            //Create a new bitmap to load the bitmap again.
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            map.position(0);

            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            BAINServer.getInstance().SendToast("File Not found:" + e.toString());
            return bitmap;
        } catch (IOException e) {
            BAINServer.getInstance().SendToast("IOException:" + e.toString());
            return bitmap;
        }
    }

    public static String BitMapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        //bitmap.compress(Bitmap.CompressFormat.PNG,100, baos); //seems to eat up memory
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap base64StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap resize(Bitmap originalImage, int newHeight, int newWidth) {
        int width;
        int height;
        float scaleWidth;
        float scaleHeight;

        Bitmap resizedBitmap;
        ByteArrayOutputStream outputStream;

        width = originalImage.getWidth();
        height = originalImage.getHeight();

        Matrix matrix = new Matrix();
        scaleWidth = ((float) newWidth) / width;
        scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        //matrix.postRotate(45);

        resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width, height, matrix, true);
        outputStream = new ByteArrayOutputStream();

        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return resizedBitmap;
    }

    public static Bitmap rescale(String selectedImagePath, int size) {
        //This checks Dimensions without loading bitmap into memory
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);

        //this calculates the insamplesize otherwise known as scale in project
        int scale = 1;
        while (options.outWidth / scale / 2 >= size && options.outHeight / scale / 2 >= size)
            scale *= 2;
        options.inSampleSize = scale;

        //this decodes the image with the insamplesize set and returns a bitmap
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(selectedImagePath, options);
    }

    public static Bitmap rotate(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        try {
            jo.put(DatabaseHelper.I_ID, UUID);
            jo.put(DatabaseHelper.I_STRING, ImageString);
        }
        catch (JSONException e) { e.printStackTrace(); }
        return jo;
    }
}