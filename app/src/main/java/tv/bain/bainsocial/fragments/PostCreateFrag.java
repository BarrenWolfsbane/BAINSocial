package tv.bain.bainsocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import tv.bain.bainsocial.databinding.PostCreateFragmentBinding;
import tv.bain.bainsocial.viewmodels.PostCreateViewModel;

public class PostCreateFrag extends Fragment {

    private PostCreateViewModel mViewModel;
    private PostCreateFragmentBinding b;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinding(container);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PostCreateViewModel.class);
    }

    private View initiateDataBinding(ViewGroup container) {
        b = PostCreateFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }


}