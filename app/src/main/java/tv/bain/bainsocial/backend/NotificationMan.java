package tv.bain.bainsocial.backend;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.DrawableRes;
import androidx.core.app.NotificationCompat;

import tv.bain.bainsocial.MainActivity;
import tv.bain.bainsocial.R;
import tv.bain.bainsocial.utils.Version;

public class NotificationMan {

    private NotificationMan() {
    }

    private static final String CHANNEL_ID = "";
    static int NOTIFICATION_ID = 1;

    /**
     * Creates the NotificationChannel (only on SDK 26+)
     */
    public static void initiate(Context ctx) {
        if (!Version.INSTANCE.oreoOrOver()) return;

        String name = ctx.getString(R.string.channel_name);
        String description = ctx.getString(R.string.channel_description);
        int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = ctx.getSystemService(android.app.NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        createNotification(ctx, R.drawable.bainsocialscreen3, "BATATA", "Title", null);
    }

    public static void createNotification(Context ctx, @DrawableRes int icon, String title, String text) {
        createNotification(ctx, icon, title, text, null);
    }

    public static void createNotification(Context ctx, @DrawableRes int icon, String title, String Text, String longText) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(Text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (longText == null || longText.isEmpty()) mBuilder.setStyle(null);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(ctx, MainActivity.class); //must be the main activity as defined in Manifest

        NotificationManager mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(++NOTIFICATION_ID, mBuilder.build());
    }

}
