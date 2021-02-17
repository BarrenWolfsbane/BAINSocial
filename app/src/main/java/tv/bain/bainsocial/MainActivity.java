package tv.bain.bainsocial;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import tv.bain.bainsocial.backend.BAINServer;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        BAINServer.getInstance().getDb().close();
        super.onDestroy();
    }
}