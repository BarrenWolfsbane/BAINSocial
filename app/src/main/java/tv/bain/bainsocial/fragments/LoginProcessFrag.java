package tv.bain.bainsocial.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;
import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.databinding.LoginProcessFragmentBinding;
import tv.bain.bainsocial.viewmodels.LoginProcessViewModel;

public class LoginProcessFrag extends Fragment implements ICallback {

    //TODO: Manage the login process in the ViewModel instead of the Fragment class

    private LoginProcessViewModel mViewModel;
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginProcessViewModel.class);
        //TODO: get navigation args from login fragment
        login(null, null);
    }

    public void login(String LoginType, String LoginPass) {
        String hashedPass = Crypt.md5(LoginPass);
        BAINServer.getInstance().getUser().setHashedPass(hashedPass);

        b.loginProcessTypeLabel.setText(LoginType);
        b.stepTwoLoginProgressTxt.setText("Hash:" + hashedPass);

        Crypt.generateSecret(this, hashedPass); //Sends back to loginSecretCallback(SecretKey secret)

    }

    @Override
    public void loginSecretCallback(SecretKey secret) {
        b.stepOneLoginProgressTxt.setTextColor(Color.parseColor("#FF00FB97"));
        b.stepOneLoginProgressTxt.setText("Complete");
        BAINServer.getInstance().getUser().setSecret(secret);
        b.stepTwoLoginProgressTxt.setText("In Progress");

        BAINServer.getInstance().getDb().open();
        BAINServer.getInstance().getDb().getMyKeyData(this, BAINServer.getInstance().getUser(), secret); //Sends back to loginKeyDBCallback(int count)
        BAINServer.getInstance().getDb().close();
    }

    @Override
    public void loginKeyDBCallback(int count) {
        if (count > 0) {
            b.stepTwoLoginProgressTxt.setText("FOUND");
            if (checkLoginToken(b.debugger)) goToMainFrag();
            else goBackToLoginFrag();

        } else {
            b.stepTwoLoginProgressTxt.setText("DB Entry Not Found");
            //TODO: If Database entry not found we check for files. and create User from it if we find it
            if (!BAINServer.getInstance().getFc().keyChecker(BAINServer.getInstance().getUser())) {
                if (BAINServer.getInstance().getFc().createKeyFiles(b.debugger)) goToMainFrag();
            } else if (BAINServer.getInstance().getFc().loadKeyFiles(b.debugger, true)) {
                if (checkLoginToken(b.debugger)) goToMainFrag();
                else goBackToLoginFrag();

            }
        }
    }

    public boolean checkLoginToken(TextView keyDataOut) { //ensures the password produces the same
        String KeyDataDirectory = "/key_data/";
        File fileDir = new File(BAINServer.getInstance().getContext().getFilesDir(), KeyDataDirectory);
        keyDataOut.setText("Checking Secrets...");
        // byte[] base64Dec = fc.readFile(new File(fileDir, "LoginToken"),true);
        return true;
    }

    private void goToMainFrag() {
        //TODO: go to main page
    }

    private void goBackToLoginFrag() {
        //TODO: return to the Login screen
    }
}