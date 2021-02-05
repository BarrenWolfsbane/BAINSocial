package tv.bain.bainsocial.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.User;

public class DBManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
        context = c;
    }

    public DBManager(Context c, ICallback callback) {
        context = c;
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
        Cursor cursor = database.query(DatabaseHelper.U_TABLE_NAME, new String[]{DatabaseHelper.U_ID, DatabaseHelper.U_PRIV_KEY, DatabaseHelper.U_PUB_KEY}, null,
                null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        if (!isEmptyString(cursor.getString(0))) me.setuID(cursor.getString(0));
        if (!isEmptyString(cursor.getString(1)))
            me.setPrivateKey(new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), me.getSecret())));
        if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));

        cursor.close();
    }

    public void getMyKeyData(ICallback cb, User me, SecretKey secret) {
        String decryptedPrivateKey = "No Key Found";
        Cursor cursor = database.query(DatabaseHelper.U_TABLE_NAME, new String[]{DatabaseHelper.U_ID, DatabaseHelper.U_PRIV_KEY, DatabaseHelper.U_PUB_KEY}, null,
                null, null, null, null, null);

        if (cursor.getCount() > 0) {
            if (!isEmptyString(cursor.getString(0))) me.setuID(cursor.getString(0));
            if (!isEmptyString(cursor.getString(1))) {
                decryptedPrivateKey = new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), secret));
                me.setPrivateKey(decryptedPrivateKey);
                cb.loginKeyDBCallback(cursor.getCount());
                //cb.loginKeyDBCallback(decryptedPrivateKey);
            }
            if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));
        } else cb.loginKeyDBCallback(cursor.getCount());
        cursor.close();
    }

    public void postMYKeyData(String Identifier, String privKey, String pubKey) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.U_ID, Identifier);
        contentValue.put(DatabaseHelper.U_PRIV_KEY, privKey);
        contentValue.put(DatabaseHelper.U_PUB_KEY, pubKey);

        String TAG = "DATABASE";
        Log.i(TAG, "DBManager.postMYKeyData(" + privKey + "" + pubKey + ")");

        database.insert(DatabaseHelper.U_TABLE_NAME, null, contentValue);
    }

    public void postInsert(String name, String desc) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_TITLE, name);
        contentValue.put(DatabaseHelper.P_TEXT, desc);
        database.insert(DatabaseHelper.P_TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        String[] columns = new String[]{DatabaseHelper.P_ID, DatabaseHelper.P_TITLE, DatabaseHelper.P_TEXT};
        Cursor cursor = database.query(DatabaseHelper.P_TABLE_NAME, columns, null, null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        return cursor;
    }

    public int update(long _id, String name, String desc) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.P_TITLE, name);
        contentValues.put(DatabaseHelper.P_TEXT, desc);
        int i = database.update(DatabaseHelper.P_TABLE_NAME, contentValues, DatabaseHelper.P_ID + " = " + _id, null);
        return i;
    }

    public void delete(long _id) {
        database.delete(DatabaseHelper.P_TABLE_NAME, DatabaseHelper.P_ID + "=" + _id, null);
    }

    public void insert_Post(Post post) {

    }

    public Post get_Post(String pID) {
        return null;
    }

    public void insert_User(User user) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.U_ID, user.getuID());
        contentValue.put(DatabaseHelper.U_HANDLE, user.getDisplayName());
        contentValue.put(DatabaseHelper.U_PUB_KEY, user.getPublicKey());
        contentValue.put(DatabaseHelper.U_PRIV_KEY, user.getPrivateKey());
        contentValue.put(DatabaseHelper.U_IS_FOLLOW, user.getIsFollowing());
        //contentValue.put(DatabaseHelper.U_SECRET, Secret); //we can store AES here later
        database.insert(DatabaseHelper.U_TABLE_NAME, null, contentValue);
    }

    public void insert_User(User user, String Secret) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.U_ID, user.getuID());
        contentValue.put(DatabaseHelper.U_HANDLE, user.getDisplayName());
        contentValue.put(DatabaseHelper.U_PUB_KEY, user.getPublicKey());
        contentValue.put(DatabaseHelper.U_PRIV_KEY, user.getPrivateKey());
        contentValue.put(DatabaseHelper.U_IS_FOLLOW, user.getIsFollowing());
        contentValue.put(DatabaseHelper.U_SECRET, Secret);
        database.insert(DatabaseHelper.U_TABLE_NAME, null, contentValue);
    }

    public User get_User(String uID) {
        return null;
    }

    public User get_User_By_Hash(ICallback cb, String hash) {
        User thisUser = null;
        Cursor res = database.query(
                DatabaseHelper.U_TABLE_NAME,
                new String[]{
                        DatabaseHelper.U_ID,
                        DatabaseHelper.U_HANDLE,
                        DatabaseHelper.U_PUB_KEY,
                        DatabaseHelper.U_PRIV_KEY,
                        DatabaseHelper.U_IS_FOLLOW
                },
                DatabaseHelper.U_SECRET + " = ?", //Where Clause
                new String[]{hash},
                null,
                null,
                null,
                null);
        if (res.moveToFirst()) {
            do {
                String uID = res.getString(res.getColumnIndex(DatabaseHelper.U_ID));
                String uHandle = res.getString(res.getColumnIndex(DatabaseHelper.U_HANDLE));
                String uPubKey = res.getString(res.getColumnIndex(DatabaseHelper.U_PUB_KEY));
                String uPrivKey = res.getString(res.getColumnIndex(DatabaseHelper.U_PRIV_KEY));
                Boolean uIsFollow = (res.getInt(res.getColumnIndex(DatabaseHelper.U_IS_FOLLOW)) == 1);

                thisUser = new User(uID, uHandle, uIsFollow, uPubKey);
                thisUser.setPrivateKey(uPrivKey);

                Toast.makeText(context, "UserNum:" + uID, Toast.LENGTH_SHORT).show();
            } while (res.moveToNext());
            //cb.loginHashCallback();
            cb.loginKeyDBCallback(res.getCount());
        } else {
            cb.loginKeyDBCallback(0);
            //cb.loginHashCallback();
            // Toast.makeText(context, "No Results in database "+this.getDatabaseName()+", Load from web instead", Toast.LENGTH_SHORT).show();
            //if this is a first time user then the information wont be available in the database so we would instead create a new user
        }
        res.close(); //cursors need to be closed to prevent memory leaks
        return thisUser;
    }
}