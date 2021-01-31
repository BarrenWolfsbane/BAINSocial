package tv.bain.bainsocial;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tv.bain.bainsocial.backend.BAINServer;

public class ServerBootActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_booting_fragment);
        startService();
        goToPermissionsFrag();
    }

    private void startService() {
        Intent service = new Intent(this, BAINServer.class); //This service will handle all important information
        if (!BAINServer.isInstanceCreated()) startService(service);
    }

    private void goToPermissionsFrag() {
        //TODO: go to permissions frag
    }

}
