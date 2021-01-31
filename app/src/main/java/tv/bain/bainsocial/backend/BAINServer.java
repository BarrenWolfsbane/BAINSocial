package tv.bain.bainsocial.backend;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

import tv.bain.bainsocial.LoginActivity;
import tv.bain.bainsocial.R;
import tv.bain.bainsocial.datatypes.User;

@SuppressLint("SetTextI18n")
public class BAINServer extends Service {
    private static BAINServer instance = null; //we use this to call on functions here
    public static  BAINServer getInstance() { return instance; }
    public static boolean isInstanceCreated(){ return instance != null; }

    private LinearLayout notificationLayout;
    public void setNotificationLayout(LinearLayout notificationLayout) { this.notificationLayout = notificationLayout; }
    private ArrayList<TextView> inAppNoticeTextViews;

    /* Stored User */
    private User user;
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    /* Stored Context */
    private Context context;
    public Context getContext() { return context; }
    public void setContext(Context context) { this.context = context; }

    private DBManager db;
    public DBManager getDb(){ return db; }
    public void setDB(DBManager db) { this.db = db; }

    private FileControls fc;
    public FileControls getFc(){ return fc; }
    public void setFC(FileControls fc) { this.fc = fc;}

    private Activity currActivity;
    public Activity getCurrActivity(){ return currActivity; }
    public void setCurrActivity(Activity currActivity){ this.currActivity = currActivity; }

    /////////////////////
    ///SERVICE PORTION///
    /////////////////////
    int mStartMode =  START_STICKY;       // indicates how to behave if the service is killed
    IBinder mBinder;      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    @Override public void onCreate(){
        super.onCreate();
        instance = this;
        //CreateNotification(R.drawable.ic_launcher_foreground, "BAIN Services", "Running in Background, Click to Access App");
        BAINServer.getInstance().setContext(getBaseContext());
        BAINServer.getInstance().setDB(new DBManager(getBaseContext()));
        BAINServer.getInstance().setFC(new FileControls(context));
        BAINServer.getInstance().setUser(new User());
        createNotificationChannel();
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId){
        // The service is starting, due to a call to startService()
        super.onStartCommand(intent, flags, startId);
        if(intent != null) {
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
        CreateNotification(R.drawable.ic_launcher_foreground, "BAIN Services", "Running in Background, Click to Access App","");
        SendToast("Ding");
        Intent i = new Intent(this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return mStartMode;
    }
    @Override public IBinder onBind(Intent arg0){ return mBinder; } // A client is binding to the service with bindService()
    @Override public boolean onUnbind(Intent intent) { return mAllowRebind; } // All clients have unbound with unbindService()
    @Override public void onRebind(Intent intent) { } // A client is binding to the service with bindService(), after onUnbind() has already been called
    @Override public void onDestroy() { super.onDestroy(); instance = null; } // The service is no longer used and is being destroyed

    /////////////////////
    ///NOTICE  PORTION///
    /////////////////////
    private static final String CHANNEL_ID = "";
    private boolean isForeground = false;
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void CreateNotification(int icon, String title, String Text, String longText){
        int notifyID = 1;
        NotificationCompat.Builder mBuilder;
        if(longText.isEmpty()){
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(Text)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        }
        else {
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(Text)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(longText))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LoginActivity.class); //must be the main activity as defined in Manifest

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack( LoginActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_UPDATE_CURRENT );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        if(isForeground) { mNotificationManager.notify(notifyID, mBuilder.build()); }
        else { startForeground(notifyID, mBuilder.build()); isForeground = true; }
    }


    public void SendToast(String msg){ Toast.makeText(context, msg, Toast.LENGTH_LONG).show(); }
    /*public void SendDataSMS(String phoneNumber, byte[] smsBody, short port){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendDataMessage(phoneNumber, null, port, smsBody, null, null);
    }*/
    //BAINServer.getInstance().inAppNotice(0, "Systems Started, All Green");
    //BAINServer.getInstance().inAppNotice(1, "Maybe Not Situation Yellow");
    //BAINServer.getInstance().inAppNotice(2, "Code Red, its worse then we thought!");
    public void inAppNotice(int priority, String notice) {
        //priotrity 0 = Notice, cancellable  //priority 1 = Warning, cancellable   //priority 2 = error, cant cancel
        if(inAppNoticeTextViews == null) inAppNoticeTextViews = new ArrayList<TextView>();
        boolean addNotice = true;
        if(notificationLayout != null){
            for (TextView tv: inAppNoticeTextViews)
                if(tv.getText().toString().equals(notice))
                    addNotice = false; //this ensures the same notice isn't posted twice

            if(addNotice) {
                TextView thisNotice = new TextView(getApplicationContext()); //create a notice
                thisNotice.setText(notice);
                thisNotice.setTextSize(18);
                thisNotice.setTextColor(Color.BLACK);
                thisNotice.setGravity(Gravity.END);

                if (priority == 0) thisNotice.setBackgroundColor(0x7f87ff6f);
                else if (priority == 1) thisNotice.setBackgroundColor(0x7fe0ff54);
                else if (priority == 2) thisNotice.setBackgroundColor(0x7fff6b68);

                thisNotice.setOnClickListener(v -> {
                    inAppNoticeTextViews.remove(v);
                    notificationLayout.removeAllViews(); //remove all current views
                    for (TextView tv : inAppNoticeTextViews) notificationLayout.addView(tv);
                });
                inAppNoticeTextViews.add(thisNotice); //adds new notice to the list of notices
                notificationLayout.removeAllViews(); //remove all current views
                for (TextView tv : inAppNoticeTextViews) notificationLayout.addView(tv);
            }
        }
    }
    public void remInAppNotice(String notice) {
        if(inAppNoticeTextViews == null) inAppNoticeTextViews = new ArrayList<TextView>();
        if(notificationLayout != null){
            for (TextView tv: inAppNoticeTextViews)
                if(tv.getText().toString().equals(notice)) inAppNoticeTextViews.remove(tv); //remove notice with same string

            notificationLayout.removeAllViews(); //remove all current views
            for (TextView tv : inAppNoticeTextViews) notificationLayout.addView(tv);
        }
    }






}