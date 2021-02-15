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
import androidx.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.List;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.backend.DatabaseHelper;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;
import tv.bain.bainsocial.utils.MyState;

public class HomeViewModel extends AndroidViewModel {

    private Bitmap profileImage;

    private final MutableLiveData<MyState> state = new MutableLiveData<>(MyState.IDLE.INSTANCE);

    public MutableLiveData<MyState> getState() {
        return state;
    }

    public void setIdleState() {
        state.postValue(MyState.IDLE.INSTANCE);
    }


    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Post> getAllLocalPosts() {
        BAINServer.getInstance().getDb().open();
        List<Post> list = BAINServer.getInstance().getDb().get_Recent_Posts_Local();
        BAINServer.getInstance().getDb().close();
        return list;
    }

    public void saveProfileImage(Intent data) {
        String imgPath = getSelectedImagePath(data.getData());
        if (imgPath.isEmpty()) return;

        try {
            Texture imgTexture = createTextureFromBitmap(imgPath);
            if (imgTexture == null) return;

            profileImage = Texture.base64StringToBitMap(imgTexture.getImageString());
            saveImageToDatabase(imgTexture);
        } catch (IOException e) {
            state.postValue(new MyState.ERROR(e.getLocalizedMessage()));
        }
    }

    private String getSelectedImagePath(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getApplication().getApplicationContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

        String imgPath = cursor.getString(columnIndex);
        cursor.close();
        return imgPath;
    }

    private Texture createTextureFromBitmap(String imgPath) throws IOException {
        int intendedSize = 150;
        int orientation; //variable stored to figure out if Orientation is at an angle

        Bitmap selectedImg = Texture.rescale(imgPath, intendedSize);
        ExifInterface ei = new ExifInterface(imgPath);
        orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        if (selectedImg == null) return null;

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                selectedImg = Texture.rotate(selectedImg, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                selectedImg = Texture.rotate(selectedImg, 180);
                break;
        }

        String base64Thumbnail = Texture.BitMapToBase64String(selectedImg); //we get the base64 string for the thumbnail
        String UUID = Crypt.md5(base64Thumbnail);
        selectedImg.recycle();

        return new Texture(UUID, base64Thumbnail);
    }

    private void saveImageToDatabase(Texture imgTexture) {
        BAINServer.getInstance().getDb().open();
        BAINServer.getInstance().getDb().insert_Image(imgTexture); //adds Image to database
        BAINServer.getInstance().getUser().setProfileImageID(imgTexture.getUUID()); //sets the User
        BAINServer.getInstance().getDb().update_User(BAINServer.getInstance().getUser(), DatabaseHelper.U_PROF_IMG, imgTexture.getUUID()); // Updates Database
        BAINServer.getInstance().getDb().close();
    }

    public Bitmap getProfileImage() {
        //TODO: if image is null get it from storage or get the default one
        return profileImage;
    }
}