package tv.bain.bainsocial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getBaseContext();
        LoadLoginScreen();
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
                    Private_Key.setText(privateKey);
                    TextView Public_Key = findViewById(R.id.PublicKey_Field);
                    Public_Key.setText(publicKey);

                    DBManager Database = new DBManager(context);
                    Database.open();
                    Database.postMYKeyData(Identifier,privateKey,publicKey);
                    Database.close();

                } catch (PGPException | IOException e) { e.printStackTrace(); }
            }
        });
        */
    }
    public void LoadLoginScreen(){
        setContentView(R.layout.login_activity);
        Switch useBlockchain = findViewById(R.id.LoginLayoutOption);
        useBlockchain.setChecked(false);

        TextView loginPassEntry = findViewById(R.id.loginPassPhraseEntry);
        TextView loginPassEntryConfirm = findViewById(R.id.loginPassPhraseRepeatEntry);
        TextView LoginErrorDisplay = findViewById(R.id.LoginErrorDisplay);

        String LoginPass = loginPassEntry.getText().toString();
        String LoginRepeat = loginPassEntryConfirm.getText().toString();

        Button LoginButton = findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toastServer("attempting Login");
                LoginErrorDisplay.setText("LoginPass:"+LoginPass+" - Repeat: "+LoginRepeat);
                //LoadMainPage();
                /*
                if(!useBlockchain.isChecked()) {
                    if (LoginPass.isEmpty()) LoginErrorDisplay.setText("Login pass is empty");
                    else if (LoginRepeat.isEmpty()) LoginErrorDisplay.setText("Login Check pass is empty");
                    else {
                        LoginErrorDisplay.setText("");
                        if (LoginPass.equals(LoginRepeat)) {
                            LoginErrorDisplay.setText("Accepted");
                            //me.setSecret(Crypt.generateSecret(loginPassEntry.getText().toString()));
                            //me.initializeUser();
                            LoadMainPage();
                        } else {
                            LoginErrorDisplay.setText("Both Passwords Must Match");
                        }
                    }
                }*/
            }
        });
    }
    public void LoadMainPage(){
        setContentView(R.layout.activity_main);
    }
    public void toastServer(String message) { Toast.makeText(context, message, Toast.LENGTH_SHORT).show(); }
    /*


    String SERVER_IP;
    int SERVER_PORT;
    Thread Thread1 = null;
    EditText etIP, etPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    Button changeBTN;
    BAINServer newBAIN;

    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { tvMessages.setText("Connected\n"); }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class Thread2 implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = input.readLine();
                    if (message != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() { tvMessages.append("server: " + message + "\n"); }
                        });
                    } else {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) { e.printStackTrace(); }
            }
        }
    }
    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
            output.write(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("client: " + message + "\n");
                    etMessage.setText("");
                }
            });
        }
    }

     */
}