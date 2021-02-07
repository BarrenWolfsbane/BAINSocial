package tv.bain.bainsocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.adapters.PostsAdapter;
import tv.bain.bainsocial.databinding.HomeFragmentBinding;
import tv.bain.bainsocial.viewmodels.HomeViewModel;

public class HomeFrag extends Fragment {

    private HomeViewModel vm;
    private HomeFragmentBinding b = null;
    private PostsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinder(container);
    }

    private View initiateDataBinder(ViewGroup container) {
        System.out.println("INITIATED");
        b = HomeFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(HomeViewModel.class);
        bindData();
    }

    private void bindData() {
        initiateAdapter();
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setFrag(this);
        b.recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    private void initiateAdapter() {
        adapter = new PostsAdapter(vm.getAllLocalPosts());
    }


    public void goToNewPostFrag() {
        NavHostFragment.findNavController(this).navigate(R.id.action_homeFrag_to_postCreateFrag);
    }

}