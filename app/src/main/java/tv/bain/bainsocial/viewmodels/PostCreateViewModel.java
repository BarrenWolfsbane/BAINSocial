package tv.bain.bainsocial.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.utils.MyState;

import static java.lang.System.currentTimeMillis;

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

    public void submitPost() {
        state.postValue(new MyState.LOADING());
        Post thisPost = new Post();

        String authorData = BAINServer.getInstance().getUser().getuID();
        thisPost.setPostType(Post.SHORT280);
        thisPost.setUid(authorData);
        thisPost.setText(postDescription);
        thisPost.setTimeCreated(currentTimeMillis());
        thisPost.setPid(Crypt.md5(postDescription));

        if (postDescription.trim().isEmpty()) {
            state.postValue(new MyState.ERROR("Please add a description to your post"));
            return;
        }

        BAINServer.getInstance().getDb().open();
        BAINServer.getInstance().getDb().insert_Post(thisPost);
        BAINServer.getInstance().getDb().close();
        state.postValue(new MyState.FINISHED());
    }

    public void setIdleState() {
        state.postValue(MyState.IDLE.INSTANCE);
    }
}