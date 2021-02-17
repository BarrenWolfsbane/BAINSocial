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
import java.util.ArrayList;
import java.util.List;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.backend.DatabaseHelper;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;
import tv.bain.bainsocial.utils.MyState;

import static java.lang.System.currentTimeMillis;
import static tv.bain.bainsocial.datatypes.Texture.textureList;

public class PostCreateViewModel extends AndroidViewModel {

    private String postDescription = "";
    private final MutableLiveData<MyState> state = new MutableLiveData<>(MyState.IDLE.INSTANCE);

    public PostCreateViewModel(@NonNull Application application) {
        super(application);
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public MutableLiveData<MyState> getState() {
        return state;
    }

    public void submitPost(String textureHashes) {
        state.postValue(new MyState.LOADING());
        Post thisPost = new Post();
        BAINServer.getInstance().SendToast("Compiling Post");

        String authorData = BAINServer.getInstance().getUser().getuID();
        thisPost.setPostType(Post.SHORT280);
        thisPost.setUid(authorData);
        thisPost.setText(postDescription);
        thisPost.setTimeCreated(currentTimeMillis());
        thisPost.setPid(Crypt.md5(currentTimeMillis()+postDescription)); //has time and post to make ID
        if (postDescription.trim().isEmpty()) {
            state.postValue(new MyState.ERROR("Please add a description to your post"));
            return;
        }
        thisPost.setResponseList(null);
        thisPost.setBlockChainTXN(null);

        ArrayList<String> tempImgList = new ArrayList<String>();
        List<String> imgList = new ArrayList<String>();
        if(textureHashes != null){
            imgList = DatabaseHelper.convertStringToArrayList(textureHashes);
            for(String hash : imgList){
                tempImgList.add("BAIN://"+BAINServer.getInstance().getUser().getuID()+":"+hash);
            }
            thisPost.setImages(tempImgList);
        }

        BAINServer.getInstance().getDb().open();
        BAINServer.getInstance().getDb().insert_Post(thisPost);
        BAINServer.getInstance().getDb().close();
        state.postValue(new MyState.FINISHED());
    }

    public void setIdleState() {
        state.postValue(MyState.IDLE.INSTANCE);
    }

    public Texture saveAndGetImage(Intent data) {
        String imgPath = getSelectedImagePath(data.getData());
        if (imgPath.isEmpty()) return null;

        try {
            Texture imgTexture = createTextureFromBitmap(imgPath);
            if (imgTexture == null) return null;

            saveImageToDatabase(imgTexture);
            textureList.add(imgTexture);
            return imgTexture;
        } catch (IOException e) { state.postValue(new MyState.ERROR(e.getLocalizedMessage())); }
        return null;
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
        BAINServer.getInstance().getDb().close();
    }
}