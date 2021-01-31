package tv.bain.bainsocial;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tv.bain.bainsocial.backend.BAINServer;

public class ServerBootActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.login_process);
        super.onCreate(savedInstanceState);
        Intent service = new Intent(this, BAINServer.class); //this service will handle all important information
        if(!BAINServer.isInstanceCreated()) startService(service);
        else startActivity(new Intent(this, LoginActivity.class));
    }
}
