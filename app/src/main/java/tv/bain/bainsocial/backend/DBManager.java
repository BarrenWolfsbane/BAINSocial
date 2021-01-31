package tv.bain.bainsocial.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;
import tv.bain.bainsocial.datatypes.User;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;
    private ICallback callbackActivity;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager(Context c, ICallback callbackActivity) {
        context = c;
        this.callbackActivity = callbackActivity;
    }

    public DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public void getMyKeyData(User me) {
        Cursor cursor = database.query(dbHelper.KEY_TABLE_NAME, new String[]{dbHelper.ID, dbHelper.PRIV_KEY, dbHelper.PUB_KEY}, null,
                null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        if (!isEmptyString(cursor.getString(0))) me.setIdentifier(cursor.getString(0));
        if (!isEmptyString(cursor.getString(1)))
            me.setPrivateKey(new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), me.getSecret())));
        if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));
    }

    public void getMyKeyData(ICallback cb, User me, SecretKey secret) {
        String decryptedPrivateKey = "No Key Found";
        Cursor cursor = database.query(dbHelper.KEY_TABLE_NAME, new String[]{dbHelper.ID, dbHelper.PRIV_KEY, dbHelper.PUB_KEY}, null,
                null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            if (!isEmptyString(cursor.getString(0))) me.setIdentifier(cursor.getString(0));
            if (!isEmptyString(cursor.getString(1))) {
                decryptedPrivateKey = new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), secret));
                me.setPrivateKey(decryptedPrivateKey);
                cb.loginKeyDBCallback(cursor.getCount());
                //cb.loginKeyDBCallback(decryptedPrivateKey);
            }
            if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));
        } else cb.loginKeyDBCallback(cursor.getCount());
    }

    public void postMYKeyData(String Identifier, String privKey, String pubKey) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.ID, Identifier);
        contentValue.put(DatabaseHelper.PRIV_KEY, privKey);
        contentValue.put(DatabaseHelper.PUB_KEY, pubKey);

        String TAG = "DATABASE";
        Log.i(TAG, "DBManager.postMYKeyData(" + privKey + "" + pubKey + ")");

        database.insert(DatabaseHelper.KEY_TABLE_NAME, null, contentValue);
    }

    public void postInsert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.SUBJECT, name);
        contentValue.put(DatabaseHelper.DESC, desc);
        database.insert(DatabaseHelper.POST_TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper._ID, DatabaseHelper.SUBJECT, DatabaseHelper.DESC};
        Cursor cursor = database.query(DatabaseHelper.POST_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SUBJECT, name);
        contentValues.put(DatabaseHelper.DESC, desc);
        int i = database.update(DatabaseHelper.POST_TABLE_NAME, contentValues, DatabaseHelper._ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.POST_TABLE_NAME, DatabaseHelper._ID + "=" + _id, null);
    }
}