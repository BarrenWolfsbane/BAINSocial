package tv.bain.bainsocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import tv.bain.bainsocial.databinding.PostCreateFragmentBinding;
import tv.bain.bainsocial.utils.MyState;
import tv.bain.bainsocial.viewmodels.PostCreateViewModel;

public class PostCreateFrag extends Fragment {

    private PostCreateViewModel vm;
    private PostCreateFragmentBinding b;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinding(container);
    }

    private View initiateDataBinding(ViewGroup container) {
        b = PostCreateFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(PostCreateViewModel.class);
        bindData();
        observeState();
    }

    private void bindData() {
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setViewModel(vm);
        b.setFrag(this);
    }

    private void observeState() {
        vm.getState().observe(getViewLifecycleOwner(), myState -> {
            if (myState instanceof MyState.FINISHED) goBackToHomeFrag();
            else if (myState instanceof MyState.ERROR) {
                Toast.makeText(requireActivity(), ((MyState.ERROR) myState).getMsg(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Keeps the bottom side buttons hidden when the keyboard is shown */
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    public void submitPost() {
        vm.submitPost();
    }

    public void goBackToHomeFrag() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}