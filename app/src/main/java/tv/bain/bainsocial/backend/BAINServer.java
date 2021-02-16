package tv.bain.bainsocial.backend;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.ArrayList;

import tv.bain.bainsocial.R;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;
import tv.bain.bainsocial.datatypes.User;

import static tv.bain.bainsocial.datatypes.Post.postList;
import static tv.bain.bainsocial.datatypes.Texture.textureList;
import static tv.bain.bainsocial.datatypes.User.usrList;

public class BAINServer extends Service {

    private static BAINServer instance = null; //we use this to call on functions here

    public static BAINServer getInstance() {
        return instance;
    }

    public static boolean isInstanceCreated() {
        return instance != null;
    }

    /* Stored User */
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private DBManager db;

    public DBManager getDb() {
        return db;
    }

    public void setDB(DBManager db) {
        this.db = db;
    }

    private FileControls fc;

    public FileControls getFc() {
        return fc;
    }

    public void setFC(FileControls fc) {
        this.fc = fc;
    }


    //region Service region
    int mStartMode = START_STICKY;       // indicates how to behave if the service is killed
    IBinder mBinder;                     // interface for clients that bind
    boolean mAllowRebind;                // indicates whether onRebind should be used

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BAINServer.getInstance().setDB(new DBManager(getApplicationContext()));
        BAINServer.getInstance().setFC(new FileControls(getApplicationContext()));
        BAINServer.getInstance().setUser(new User());
        NotificationMan.initiate(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type))
                    //email = intent.getStringExtra(Intent.EXTRA_EMAIL); //we get the email from the Login Activity
                    intent.setAction("");
                intent.setType("");
                intent.removeExtra(Intent.EXTRA_EMAIL);
            }
            // Toast.makeText(this, "Loading Email:" + email, Toast.LENGTH_SHORT).show();
            //User.findUser(this, getEmail()); //load the user object into the app
        }

        NotificationMan.createNotification(getApplicationContext(), R.drawable.ic_launcher_foreground, "BAIN Services", "Running in Background, Click to Access App");

        if(usrList == null) User.usrList = new ArrayList<>();
        if(postList == null) Post.postList = new ArrayList<>();
        if(textureList == null) Texture.textureList = new ArrayList<>();

        //SendToast("Ding");
        return mStartMode;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    } // A client is binding to the service with bindService()

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    } // All clients have unbound with unbindService()

    @Override
    public void onRebind(Intent intent) {
    } // A client is binding to the service with bindService(), after onUnbind() has already been called

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    } // The service is no longer used and is being destroyed

    //region Notice region
    public void SendToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    public static int A_USER = 0;
    public static int A_QUERY = 1;
    public Object Bain_Search(String BAINAddress){
        Object resultObject;
        if(BAINAddress == null) return null;
        if((BAINAddress.substring(0,7)).toLowerCase().contains("bain://")){
            BAINAddress = BAINAddress.substring(7); //strips protocol
            String[] searchArray = BAINAddress.split(":");
            String address = searchArray[A_USER];
            String query = searchArray[A_QUERY];
            /*
                - Do localized Search of array and DB to see
                - If we know of this object already Return it.
            */
            resultObject = BAINServer.getInstance().getDb().array_ID_Search(query);
            if(resultObject != null) return resultObject;
            resultObject = BAINServer.getInstance().getDb().db_ID_Search(query);
            if(resultObject != null) return resultObject;
            /*
                - Query Failed to produce results since return didn't trigger
                - Get the Directory Info from the Database
                - Send a message via HTTP to the address listed to have them preform search
            */
            Object addressObject = BAINServer.getInstance().getDb().array_ID_Search(address);
            if(addressObject == null) addressObject = BAINServer.getInstance().getDb().db_ID_Search(address);
            if(addressObject != null) {
                //use the directory information in the new object to send a message to the user to query the data
            }
            else {
                //No Address info found for the user Send a network broadcast to all other known users to do a search
            }
        }
        else {
            resultObject = BAINServer.getInstance().getDb().array_ID_Search(BAINAddress);
            if(resultObject != null) return resultObject;
            resultObject = BAINServer.getInstance().getDb().db_ID_Search(BAINAddress);
            if(resultObject != null) return resultObject;
        }
        return null;
    }
    public static String BAINStrip(String BAINAddress, Integer segment){
        if((BAINAddress.substring(0,7)).toLowerCase().contains("bain://")) {
            BAINAddress = BAINAddress.substring(7); //strips protocol
            String[] searchArray = BAINAddress.split(":");
            return searchArray[segment];
        }
        return "";
    }
}