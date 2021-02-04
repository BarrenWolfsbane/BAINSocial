package tv.bain.bainsocial.fragments;

import android.graphics.Color;
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
import tv.bain.bainsocial.databinding.LoginProcessFragmentBinding;
import tv.bain.bainsocial.utils.MyState;
import tv.bain.bainsocial.viewmodels.LoginProcessViewModel;

public class LoginProcessFrag extends Fragment {

    private LoginProcessViewModel vm;
    private LoginProcessFragmentBinding b;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return initiateDataBinder(container);
    }

    private View initiateDataBinder(ViewGroup container) {
        b = LoginProcessFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    private void bindData() {
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setFrag(this);
        b.setViewModel(vm);
        vm.setLoginPass(requireArguments().getString("loginPass"));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(LoginProcessViewModel.class);
        bindData();
        observeState();
        vm.login();
    }

    private void observeState() {
        // Observes the switch changes and behaves accordingly
        vm.getState().observe(getViewLifecycleOwner(), myState -> {
            if (myState instanceof MyState.FINISHED) goToServerChoice();
            else if (myState instanceof MyState.ERROR) goBackToLogin();
        });
        vm.getStepOneProgress().observe(getViewLifecycleOwner(), st -> {
            if (st.equals("Complete"))
                b.stepOneLoginProgressTxt.setTextColor(Color.parseColor("#FF00FB97"));
        });
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    private void goToServerChoice() {
        NavHostFragment.findNavController(this).navigate(R.id.action_loginProcessFrag_to_serverChoiceFrag);
    }

    private void goBackToLogin() {
        NavHostFragment.findNavController(this).popBackStack();
    }
}