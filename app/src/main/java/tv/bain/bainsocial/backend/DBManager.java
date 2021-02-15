package tv.bain.bainsocial.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;
import tv.bain.bainsocial.datatypes.Post;
import tv.bain.bainsocial.datatypes.Texture;
import tv.bain.bainsocial.datatypes.User;

public class DBManager {

    private DatabaseHelper dbHelper;
    private final Context context;
    private SQLiteDatabase database;

    public DBManager(Context c) {
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

    public Cursor fetch() {
        String[] columns = DatabaseHelper.P_COLUMNS_LIST;
        return database.query(DatabaseHelper.P_TABLE_NAME, columns, null, null, null, null, DatabaseHelper.P_TIME + " DESC");
    }

    public void insert_Post(Post post) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.P_ID, post.getPid());
        contentValue.put(DatabaseHelper.P_UID, post.getUid());
        contentValue.put(DatabaseHelper.P_TYPE, post.getPostType());
        contentValue.put(DatabaseHelper.P_TIME, post.getTimeCreated());
        contentValue.put(DatabaseHelper.P_REPLYTO, post.getReplyTo());
        contentValue.put(DatabaseHelper.P_TEXT, post.getText());
        contentValue.put(DatabaseHelper.P_ANTITAMPER, post.getAntiTamper());
        database.insert(DatabaseHelper.P_TABLE_NAME, null, contentValue);
    }

    public Post get_Post(String pID) {
        return null;
    }

    public List<Post> get_Recent_Posts_Local() {
        Cursor cur = fetch();
        ArrayList<Post> arr = new ArrayList<>();

        if (!cur.moveToFirst()) return arr;

        do {
            Post post = new Post();
            post.setText(cur.getString(5));
            post.setTimeCreated(cur.getLong(6));
            post.setUid(cur.getString(3));
            arr.add(post);
        } while (cur.moveToNext());

        return arr;
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

    public int update_User(User user, String key, String Value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(key, Value);
        int i = database.update(DatabaseHelper.U_TABLE_NAME, contentValues, DatabaseHelper.U_ID + " = '" + user.getuID()+"'", null);
        return i;
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
                        DatabaseHelper.U_PROF_IMG,
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
                String uProfImg = res.getString(res.getColumnIndex(DatabaseHelper.U_PROF_IMG));
                String uPubKey = res.getString(res.getColumnIndex(DatabaseHelper.U_PUB_KEY));
                String uPrivKey = res.getString(res.getColumnIndex(DatabaseHelper.U_PRIV_KEY));
                Boolean uIsFollow = (res.getInt(res.getColumnIndex(DatabaseHelper.U_IS_FOLLOW)) == 1);

                thisUser = new User(uID, uHandle, uIsFollow, uPubKey, uProfImg);
                thisUser.setPrivateKey(uPrivKey);
                //Toast.makeText(context, "UserNum:" + uID, Toast.LENGTH_SHORT).show();
            } while (res.moveToNext());
            BAINServer.getInstance().setUser(thisUser); //Updates entire User with data pulled from DB
            cb.loginKeyDBCallback(res.getCount());
        } else {
            cb.loginKeyDBCallback(0);
        }
        res.close(); //cursors need to be closed to prevent memory leaks
        return thisUser;
    }

    public void insert_Image(Texture image) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.I_ID, image.getUUID());
        contentValue.put(DatabaseHelper.I_STRING, image.getImageString());

        database.insert(DatabaseHelper.I_TABLE_NAME, null, contentValue);
    }
}