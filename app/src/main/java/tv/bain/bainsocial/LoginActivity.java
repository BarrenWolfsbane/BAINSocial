package tv.bain.bainsocial;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.backend.BAINServer;
import tv.bain.bainsocial.backend.Crypt;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LoginActivity extends AppCompatActivity implements ICallback {
    private Context context;
    String CurrentLayout = "";

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(CurrentLayout.matches("R.layout.permission_init")) {
                        Button agree = findViewById(R.id.agreeAndContinue);
                        if(checkPermission()) agree.setEnabled(true);
                    }
                } else {
                    if(CurrentLayout.matches("R.layout.permission_init")) {
                        Button agree = findViewById(R.id.agreeAndContinue);
                        if(!checkPermission()) agree.setEnabled(false);
                    }
                }
            }
        }
    }
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        if(CurrentLayout.matches("R.layout.permission_init")){
            Button extStoragePermBtn = findViewById(R.id.extStoragePermBtn);
            if(result == PackageManager.PERMISSION_GRANTED) extStoragePermBtn.setEnabled(false);
            else if (result == PackageManager.PERMISSION_DENIED) extStoragePermBtn.setEnabled(true);
        }
        //int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        //return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        return result == PackageManager.PERMISSION_GRANTED;
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkPermission()) LoadLoginScreen(); //required perms are good, go to Login Screen
        else PermissionSetup(); //Permissions are not set, run permissions
    }
    public void PermissionSetup(){
        setContentView(R.layout.permission_init);
        CurrentLayout = "R.layout.permission_init";

        Button extStoragePermBtn = findViewById(R.id.extStoragePermBtn);
        extStoragePermBtn.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{WRITE_EXTERNAL_STORAGE},1);
        });
        Button agree = findViewById(R.id.agreeAndContinue);
        agree.setEnabled(false);
        agree.setOnClickListener(v -> { LoadLoginScreen(); });
    }
    public void LoadLoginScreen(){
        setContentView(R.layout.login_activity);
        CurrentLayout = "R.layout.login_activity";
        Switch useBlockchain = findViewById(R.id.LoginLayoutOption);
        useBlockchain.setChecked(false);
        useBlockchain.setOnCheckedChangeListener((buttonView, isChecked) -> {
            LinearLayout CryptoOptions = findViewById(R.id.CryptoRecovery);
            LinearLayout NormalOptions = findViewById(R.id.DeviceLogin);
            if(isChecked) { NormalOptions.setVisibility(View.GONE); CryptoOptions.setVisibility(View.VISIBLE); }
            else { NormalOptions.setVisibility(View.VISIBLE); CryptoOptions.setVisibility(View.GONE); }
        });

        TextView loginPassEntry = findViewById(R.id.loginPassPhraseEntry);
        TextView loginPassEntryConfirm = findViewById(R.id.loginPassPhraseRepeatEntry);
        TextView LoginErrorDisplay = findViewById(R.id.LoginErrorDisplay);
        TextView loginTransID = findViewById(R.id.loginTransactionEntry);
        TextView loginTransPass = findViewById(R.id.loginTransactionPassEntry);

        Button LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(v -> {
            if(useBlockchain.isChecked()){
                String loginType = "Login Type - Blockchain Recovery";
                String Txn = loginTransID.getText().toString();
                String LoginPass = loginTransPass.getText().toString();
                LoginErrorDisplay.setText("Blockchain Backups are not Functioning at this time"); //TODO: Make Blockchain Backups a thing
            }
            else {
                String loginType = "Login Type - Local Passphrase";
                String LoginPass = loginPassEntry.getText().toString();
                String LoginRepeat = loginPassEntryConfirm.getText().toString();
                if(isEmptyString(LoginPass) || isEmptyString(LoginRepeat))
                    LoginErrorDisplay.setText("Neither the Passphrase not the check may be Empty");
                else if(!LoginPass.matches(LoginRepeat))
                    LoginErrorDisplay.setText("Both the Passphrase and the Check must match");
                else
                    loginProcess(loginType, LoginPass);
            }
        });
    } //this is the display and operations of the layout
    public void loginProcess(String LoginType ,String LoginPass){
        setContentView(R.layout.login_process);
        CurrentLayout = "R.layout.login_process";
        String hashedPass = Crypt.md5(LoginPass);
        BAINServer.getInstance().getUser().setHashedPass(hashedPass);

        TextView loginProcessTypeLabel = findViewById(R.id.loginProcessTypeLabel);
        loginProcessTypeLabel.setText(LoginType);

        TextView loginStepOneLabel = findViewById(R.id.stepOneLoginLabel);
        loginStepOneLabel.setVisibility(View.VISIBLE);
        TextView loginStepOneProcess = findViewById(R.id.stepOneLoginProgress);
        loginStepOneProcess.setVisibility(View.VISIBLE);
        loginStepOneProcess.setTextColor(Color.parseColor("#CDDC39"));

        TextView loginStepTwoLabel = findViewById(R.id.stepTwoLoginLabel);
        loginStepTwoLabel.setVisibility(View.VISIBLE);
        loginStepTwoLabel.setText(" ");
        TextView loginStepTwoProcess = findViewById(R.id.stepTwoLoginProgress);
        loginStepTwoProcess.setVisibility(View.VISIBLE);
        loginStepTwoProcess.setText(" ");
        loginStepTwoProcess.setText("Hash:"+hashedPass);

        Crypt.generateSecret(this, hashedPass); //Sends back to loginSecretCallback(SecretKey secret)

    } //the active Login Process with ASync
    boolean isEmptyString(String string) { return string == null || string.isEmpty(); }

    public void loginSecretCallback(SecretKey secret) {
        if(CurrentLayout.matches("R.layout.login_process")){
            TextView loginProcess = findViewById(R.id.stepOneLoginProgress);
            loginProcess.setTextColor(Color.parseColor("#FF00FB97"));
            loginProcess.setText("Complete");
            BAINServer.getInstance().getUser().setSecret(secret);

            TextView loginStepTwoLabel = findViewById(R.id.stepTwoLoginLabel);
            loginStepTwoLabel.setText("Checking For Keys -");
            TextView loginStepTwoProcess = findViewById(R.id.stepTwoLoginProgress);
            loginStepTwoProcess.setText(" In Progress");

            BAINServer.getInstance().getDb().open();
            BAINServer.getInstance().getDb().getMyKeyData(this, BAINServer.getInstance().getUser(), secret); //Sends back to loginKeyDBCallback(int count)
            BAINServer.getInstance().getDb().close();
        }
    }
    public void loginKeyDBCallback(int count){
        TextView loginStepTwoProcess = findViewById(R.id.stepTwoLoginProgress);
        TextView debugger = findViewById(R.id.KeyDisplay);

        if(count > 0){
            loginStepTwoProcess.setText("FOUND");
            if(checkLoginToken(debugger)) loadMain(); //Can Decode, go to main page
            else LoadLoginScreen(); //cannot decode, go back to login
        }
        else{
            loginStepTwoProcess.setText("DB Entry Not Found");
            //TODO:If Database entry not found we check for files. and create User from it if we find it
            if(!BAINServer.getInstance().getFc().keyChecker(BAINServer.getInstance().getUser())) {
                if (BAINServer.getInstance().getFc().createKeyFiles(debugger)) loadMain();
            }
            else if(BAINServer.getInstance().getFc().loadKeyFiles(debugger,true)){
                if(checkLoginToken(debugger)) loadMain(); //Can Decode, go to main page
                else LoadLoginScreen(); //cannot decode, go back to login
            }
        }
    }
    public void loadMain(){
        Intent i = new Intent(this, MainActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(new Intent(this, MainActivity.class));
    }
    public boolean checkLoginToken(TextView keyDataOut){ //ensures the password produces the same
        String KeyDataDirectory =  "/key_data/";
        File fileDir = new File(BAINServer.getInstance().getContext().getFilesDir(), KeyDataDirectory);
        keyDataOut.setText("Checking Secrets...");
        // byte[] base64Dec = fc.readFile(new File(fileDir, "LoginToken"),true);
        return true;
    }
}
