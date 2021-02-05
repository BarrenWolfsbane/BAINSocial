package tv.bain.bainsocial.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.databinding.LoginFragmentBinding;
import tv.bain.bainsocial.utils.MyException;
import tv.bain.bainsocial.viewmodels.LoginViewModel;

public class LoginFrag extends Fragment {

    //TODO: Manage the login process in the ViewModel instead of the Fragment class

    private LoginViewModel vm;
    private LoginFragmentBinding b = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return initiateDataBinder(container);
    }

    private View initiateDataBinder(ViewGroup container) {
        b = LoginFragmentBinding.inflate(getLayoutInflater(), container, false);
        return b.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vm = new ViewModelProvider(this).get(LoginViewModel.class);
        bindData();
        observeState();
    }

    private void bindData() {
        b.setLifecycleOwner(getViewLifecycleOwner());
        b.setViewModel(vm);
        b.setFrag(this);
    }

    private void observeState() {
        // Observes the switch changes and behaves accordingly
        vm.loginTypeSwitch.observe(getViewLifecycleOwner(), isChecked -> {
            b.cryptoRecoveryLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            b.deviceLoginLayout.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }


    private void goToLoginProcessFrag(String loginType, String loginPass) {
        Bundle bundle = new Bundle();
        bundle.putString("loginType", loginType);
        bundle.putString("loginPass", loginPass);

        NavHostFragment.findNavController(this).navigate(R.id.action_loginFrag_to_loginProcessFrag, bundle);
    }

    public void login() {
        try {
            boolean loginSucceeded = vm.login();
            if (loginSucceeded) goToLoginProcessFrag(vm.getLoginType(), vm.getLoginPass());
        } catch (MyException e) {
            Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}