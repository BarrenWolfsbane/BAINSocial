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
import tv.bain.bainsocial.databinding.LoginFragmentBinding;
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
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        b = null;
        super.onDestroyView();
    }

    private void loginWithBlockChain() {
        String loginType = "Login Type - Blockchain Recovery";
        String Txn = b.loginTransactionEdit.getText().toString();
        String LoginPass = b.loginTransactionPassEdit.getText().toString();
        b.loginErrorTxt.setText("Blockchain Backups are not Functioning at this time"); //TODO: Make Blockchain Backups a thing
    }

    private void loginWithPassphrase() {
        String loginType = "Login Type - Local Passphrase";
        String loginPass = b.loginPassPhraseEdit.getText().toString();
        String loginRepeat = b.loginPassPhraseRepeatEdit.getText().toString();
        if (loginPass.isEmpty() || loginRepeat.isEmpty())
            b.loginErrorTxt.setText("Neither the Passphrase not the check may be Empty");
        else if (!loginPass.matches(loginRepeat))
            b.loginErrorTxt.setText("Both the Passphrase and the Check must match");
        else goToLoginProcess(loginType, loginPass);

    }

    private void goToLoginProcess(String loginType, String loginPass) {
        Bundle bundle = new Bundle();
        bundle.putString("loginType", loginType);
        bundle.putString("loginPass", loginPass);

        NavHostFragment.findNavController(this).navigate(R.id.action_loginFrag_to_loginProcessFrag, bundle);
    }

    private void setOnClickListeners() {
        b.useBlockChainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            b.cryptoRecoveryLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            b.deviceLoginLayout.setVisibility(!isChecked ? View.VISIBLE : View.GONE);
        });

        b.loginBtn.setOnClickListener(v -> {
            if (b.useBlockChainSwitch.isChecked()) loginWithBlockChain();
            else loginWithPassphrase();
        });
    }
}