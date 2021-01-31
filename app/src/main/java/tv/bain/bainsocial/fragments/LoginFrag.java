package tv.bain.bainsocial.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        String LoginPass = b.loginPassPhraseEdit.getText().toString();
        String LoginRepeat = b.loginPassPhraseRepeatEdit.getText().toString();
        if (LoginPass.isEmpty() || LoginRepeat.isEmpty())
            b.loginErrorTxt.setText("Neither the Passphrase not the check may be Empty");
        else if (!LoginPass.matches(LoginRepeat))
            b.loginErrorTxt.setText("Both the Passphrase and the Check must match");
        else {
            //TODO: go to LoginProcess fragment
        }
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