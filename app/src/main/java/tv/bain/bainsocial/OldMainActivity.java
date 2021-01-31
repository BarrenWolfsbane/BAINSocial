package tv.bain.bainsocial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import javax.crypto.SecretKey;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

@SuppressLint("SetTextI18n")
interface ICallback {
    public void loginSecretCallback(SecretKey secret);

    public void loginKeyDBCallback(int count);
}

public class OldMainActivity extends AppCompatActivity implements ICallback {
    private Context context;
    private Crypt crypt;
    String CurrentLayout = "";

    private User me;

    public User getMe() {
        return me;
    }

    private DBManager db;

    public DBManager getDb() {
        return db;
    }

    private FileControls fc;

    public FileControls getFc() {
        return fc;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();
        crypt = new Crypt(this); //Creates a Crypt object
        db = new DBManager(this, this);
        fc = new FileControls(this, context);
        me = new User(this);

        if (checkPermission()) LoadLoginScreen(); //required perms are good, go to Login Screen
        else PermissionSetup(); //Permissions are not set, run permissions

        //region commented code
        /*
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        changeBTN = findViewById(R.id.changeBtn);
        */
        //Even if the File exists we need to check the database and load this key into it provided it decrypts properly


        //Check for encrypted Key
        //Check for File first. If no file exists
        //PullDatabase Info.
        //Check for database Info.
        //If No key Exists, Load Login Page.

        //If Key Exists Display Main page.
        /*
        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMessages.setText("");
                SERVER_IP = etIP.getText().toString().trim();
                SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
                Thread1 = new Thread(new Thread1());
                Thread1.start();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) new Thread(new Thread3(message)).start();
            }
        });
        changeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.key_page);
            }
        });
*/
        //Lets Generate Key Data For User.
        /*
        Button Generate_Key = findViewById(R.id.Generate_Key_Submit);
        Generate_Key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    TextView Key_Pass_Entry = findViewById(R.id.Key_Pass_Entry);
                    PGPKeyRingGenerator krgen;
                    krgen = Pgp.generateKeyRingGenerator(Key_Pass_Entry.toString().toCharArray());
                    String publicKey = Pgp.genPGPPublicKey(krgen);
                    String privateKey = Pgp.genPGPPrivKey(krgen);
                    String Identifier = Crypt.md5(privateKey);


                    TextView Private_Key = findViewById(R.id.PrivateKey_Field);
                    TextView Public_Key = findViewById(R.id.PublicKey_Field);
                    Private_Key.setText(privateKey);
                    Public_Key.setText(publicKey);

                    DBManager Database = new DBManager(context);
                    Database.open();
                    Database.postMYKeyData(Identifier,privateKey,publicKey);
                    Database.close();

                } catch (PGPException | IOException e) { e.printStackTrace(); }
            }
        });
        */
        //endregion
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (CurrentLayout.matches("R.layout.permission_init")) {
                        Button agree = findViewById(R.id.agreeAndContinue);
                        if (checkPermission()) agree.setEnabled(true);
                    }
                } else {
                    if (CurrentLayout.matches("R.layout.permission_init")) {
                        Button agree = findViewById(R.id.agreeAndContinue);
                        if (!checkPermission()) agree.setEnabled(false);
                    }
                }
            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        if (CurrentLayout.matches("R.layout.permission_init")) {
            Button extStoragePermBtn = findViewById(R.id.extStoragePermBtn);
            extStoragePermBtn.setEnabled(result != PackageManager.PERMISSION_GRANTED);
        }
        //int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        //return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void PermissionSetup() {
        setContentView(R.layout.permission_init);
        CurrentLayout = "R.layout.permission_init";

        Button extStoragePermBtn = findViewById(R.id.extStoragePermBtn);
        extStoragePermBtn.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(OldMainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        });
        Button agree = findViewById(R.id.agreeAndContinue);
        agree.setEnabled(false);
        agree.setOnClickListener(v -> {
            LoadLoginScreen();
        });
    }

    public void LoadLoginScreen() {
        setContentView(R.layout.login_fragment);
        CurrentLayout = "R.layout.login_activity";
        Switch useBlockchain = findViewById(R.id.loginLayoutSwitch);
        useBlockchain.setChecked(false);
        useBlockchain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LinearLayout CryptoOptions = findViewById(R.id.cryptoRecoveryLayout);
                LinearLayout NormalOptions = findViewById(R.id.deviceLoginLayout);
                if (isChecked) {
                    NormalOptions.setVisibility(View.GONE);
                    CryptoOptions.setVisibility(View.VISIBLE);
                } else {
                    NormalOptions.setVisibility(View.VISIBLE);
                    CryptoOptions.setVisibility(View.GONE);
                }
            }
        });

        TextView loginPassEntry = findViewById(R.id.loginPassPhraseEdit);
        TextView loginPassEntryConfirm = findViewById(R.id.loginPassPhraseRepeatEdit);
        TextView LoginErrorDisplay = findViewById(R.id.loginErrorTxt);
        TextView loginTransID = findViewById(R.id.loginTransactionEdit);
        TextView loginTransPass = findViewById(R.id.loginTransactionPassEdit);

        Button LoginButton = findViewById(R.id.loginBtn);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (useBlockchain.isChecked()) {
                    String loginType = "Login Type - Blockchain Recovery";
                    String Txn = loginTransID.getText().toString();
                    String LoginPass = loginTransPass.getText().toString();
                    LoginErrorDisplay.setText("Blockchain Backups are not Functioning at this time"); //TODO: Make Blockchain Backups a thing
                } else {
                    String loginType = "Login Type - Local Passphrase";
                    String LoginPass = loginPassEntry.getText().toString();
                    String LoginRepeat = loginPassEntryConfirm.getText().toString();
                    if (isEmptyString(LoginPass) || isEmptyString(LoginRepeat))
                        LoginErrorDisplay.setText("Neither the Passphrase not the check may be Empty");
                    else if (!LoginPass.matches(LoginRepeat))
                        LoginErrorDisplay.setText("Both the Passphrase and the Check must match");
                    else
                        loginProcess(loginType, LoginPass);
                }
            }
        });
    } //this is the display and operations of the layout

    public void loginProcess(String LoginType, String LoginPass) {
        setContentView(R.layout.login_process);
        CurrentLayout = "R.layout.login_process";
        String hashedPass = crypt.md5(LoginPass);
        me.setHashedPass(hashedPass);

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

        crypt.generateSecret(this, hashedPass); //Sends back to loginSecretCallback(SecretKey secret)
    } //the active Login Process with ASync

    boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public void loginSecretCallback(SecretKey secret) {
        if (CurrentLayout.matches("R.layout.login_process")) {
            TextView loginProcess = findViewById(R.id.stepOneLoginProgress);
            loginProcess.setTextColor(Color.parseColor("#FF00FB97"));
            loginProcess.setText("Complete");
            me.setSecret(secret);

            TextView loginStepTwoLabel = findViewById(R.id.stepTwoLoginLabel);
            loginStepTwoLabel.setText("Checking For Keys -");
            TextView loginStepTwoProcess = findViewById(R.id.stepTwoLoginProgress);
            loginStepTwoProcess.setText(" In Progress");

            db.open();
            db.getMyKeyData(this, me, secret); //Sends back to loginKeyDBCallback(int count)
            db.close();
        }
    }

    public void loginKeyDBCallback(int count) {
        TextView loginStepTwoProcess = findViewById(R.id.stepTwoLoginProgress);
        TextView debugger = findViewById(R.id.KeyDisplay);

        if (count > 0) {
            loginStepTwoProcess.setText("FOUND");
            if (checkLoginToken(debugger)) ; //Can Decode, go to main page
            else LoadLoginScreen(); //cannot decode, go back to login
        } else {
            loginStepTwoProcess.setText("DB Entry Not Found");
            //TODO:If Database entry not found we check for files. and create User from it if we find it
            if (!fc.keyChecker(me))
                fc.createKeyFiles(debugger); //The File Creation method doesn't pull database info
            else if (fc.loadKeyFiles(debugger, true)) {
                if (checkLoginToken(debugger)) ; //Can Decode, go to main page
                else LoadLoginScreen(); //cannot decode, go back to login
            }
        }
    }

    public boolean checkLoginToken(TextView keyDataOut) { //ensures the password produces the same
        String KeyDataDirectory = "/key_data/";
        File fileDir = new File(context.getFilesDir(), KeyDataDirectory);
        keyDataOut.setText("Checking Secrets...");
        // byte[] base64Dec = fc.readFile(new File(fileDir, "LoginToken"),true);
        return true;
    }
}
