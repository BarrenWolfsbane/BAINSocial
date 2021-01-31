package tv.bain.bainsocial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tv.bain.bainsocial.backend.Crypt;
import tv.bain.bainsocial.backend.DBManager;
import tv.bain.bainsocial.backend.FileControls;
import tv.bain.bainsocial.datatypes.User;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity{
    private Context context;
    private Crypt crypt;
    String CurrentLayout = "";

    private User me;
    public User getMe(){ return me; }

    private DBManager db;
    public DBManager getDb(){ return db;}

    private FileControls fc;
    public FileControls getFc(){ return fc;}

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getBaseContext();
    }
}
