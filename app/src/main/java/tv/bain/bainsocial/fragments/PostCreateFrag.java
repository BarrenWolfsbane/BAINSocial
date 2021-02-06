package tv.bain.bainsocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.databinding.PostCreateFragmentBinding;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.viewmodels.PostCreateViewModel;

import static java.lang.System.currentTimeMillis;

public class PostCreateFrag extends Fragment {

    private PostCreateViewModel vm;
    private PostCreateFragmentBinding b;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinding(container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(PostCreateViewModel.class);
        setOnClickListeners();
    }

    private View initiateDataBinding(ViewGroup container) {
        b = PostCreateFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    private void setOnClickListeners() {
        b.setTags.setOnClickListener(v -> {

        });
        b.submitPost.setOnClickListener(v -> {
            Post thisPost = new Post();
            String authorData = BAINServer.getInstance().getUser().getuID();
            String postData = b.postEntryField.getText().toString();

            thisPost.setpType(Post.SHORT280);
            thisPost.setuID(authorData);
            thisPost.setText(postData);
            thisPost.setTimeCreated(currentTimeMillis());
            thisPost.setpID(Crypt.md5(thisPost.getText()));

            //thisPost.setReplyTo();
            //thisPost.setAntiTamper();

            BAINServer.getInstance().getDb().open();
            BAINServer.getInstance().getDb().insert_Post(thisPost);
            BAINServer.getInstance().getDb().close();
        });
        b.openImageSelect.setOnClickListener(v -> {

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }
}