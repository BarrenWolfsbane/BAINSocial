package tv.bain.bainsocial.viewmodels;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.io.IOException;
import java.util.List;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.backend.DatabaseHelper;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;

public class HomeViewModel extends AndroidViewModel {

    private Bitmap profileImage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Post> getAllLocalPosts() {
        BAINServer.getInstance().getDb().open();
        List<Post> list = BAINServer.getInstance().getDb().get_Recent_Posts_Local();
        BAINServer.getInstance().getDb().close();
        return list;
    }

    public void saveProfileImageToDatabase(Intent data) {
        int intendedSize = 150;
        int orientation; //variable stored to figure out if Orientation is at an angle

        Uri selectedImage = data.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplication().getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        if (filePath.isEmpty()) return;

        Bitmap yourSelectedImage;
        try {
            yourSelectedImage = Texture.rescale(filePath, intendedSize);
            ExifInterface ei = new ExifInterface(filePath);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (yourSelectedImage == null) return;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    yourSelectedImage = Texture.rotate(yourSelectedImage, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    yourSelectedImage = Texture.rotate(yourSelectedImage, 180);
                    break;
            }
            String base64Thumbnail = Texture.BitMapToBase64String(yourSelectedImage); //we get the base64 string for the thumbnail
            String UUID = Crypt.md5(base64Thumbnail);
            yourSelectedImage.recycle();
            Texture thisImage = new Texture(UUID, base64Thumbnail);

            profileImage = Texture.base64StringToBitMap(thisImage.getImageString());

            BAINServer.getInstance().getDb().open();
            BAINServer.getInstance().getDb().insert_Image(thisImage); //adds Image to database
            BAINServer.getInstance().getUser().setProfileImageID(thisImage.getUUID()); //sets the User
            BAINServer.getInstance().getDb().update_User(BAINServer.getInstance().getUser(), DatabaseHelper.U_PROF_IMG, thisImage.getUUID()); // Updates Database
            BAINServer.getInstance().getDb().close();
        } catch (IOException e) {
            BAINServer.getInstance().SendToast("IOError In Image Opening error:" + e.toString());
        }
    }

    public Bitmap getProfileImage() {
        //TODO: if image is null get it from storage or get the default one
        return profileImage;
    }
}