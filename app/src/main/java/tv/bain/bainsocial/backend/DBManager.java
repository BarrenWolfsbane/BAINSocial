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

import static tv.bain.bainsocial.backend.DatabaseHelper.D_PORT;
import static tv.bain.bainsocial.backend.DatabaseHelper.D_PUB_ADDRESS;
import static tv.bain.bainsocial.backend.DatabaseHelper.D_TABLE_NAME;
import static tv.bain.bainsocial.backend.DatabaseHelper.D_UID;
import static tv.bain.bainsocial.backend.DatabaseHelper.I_ID;
import static tv.bain.bainsocial.backend.DatabaseHelper.I_STRING;
import static tv.bain.bainsocial.backend.DatabaseHelper.I_TABLE_NAME;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_ANTITAMPER;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_BLOCKCHAIN;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_COLUMNS_LIST;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_ID;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_IMAGELIST;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_REPLYLIST;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_REPLYTO;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_TABLE_NAME;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_TEXT;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_TIME;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_TYPE;
import static tv.bain.bainsocial.backend.DatabaseHelper.P_UID;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_HANDLE;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_ID;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_IS_FOLLOW;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_PRIV_KEY;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_PROF_IMG;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_PUB_KEY;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_SECRET;
import static tv.bain.bainsocial.backend.DatabaseHelper.U_TABLE_NAME;
import static tv.bain.bainsocial.backend.DatabaseHelper.convertArrayToString;
import static tv.bain.bainsocial.backend.DatabaseHelper.convertStringToArrayList;
import static tv.bain.bainsocial.datatypes.Post.postList;
import static tv.bain.bainsocial.datatypes.Texture.textureList;
import static tv.bain.bainsocial.datatypes.User.usrList;

public class DBManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public void open(Context ctx) throws SQLException {
        dbHelper = new DatabaseHelper(ctx);
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    boolean isEmptyString(String string) {
        return string == null || string.isEmpty();
    }

    public void getMyKeyData(User me) {
        Cursor cursor = query(U_TABLE_NAME, new String[]{U_ID, U_PRIV_KEY, U_PUB_KEY});

        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        if (!isEmptyString(cursor.getString(0))) me.setuID(cursor.getString(0));
        if (!isEmptyString(cursor.getString(1))) {
            me.setPrivateKey(new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), me.getSecret())));
        }
        if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));

        cursor.close();
    }

    public void getMyKeyData(ICallback cb, User me, SecretKey secret) {
        Cursor cursor = query(U_TABLE_NAME, new String[]{U_ID, U_PRIV_KEY, U_PUB_KEY});

        if (!cursor.moveToFirst()) {
            cb.loginKeyDBCallback(cursor.getCount());
            return;
        }

        if (!isEmptyString(cursor.getString(0))) me.setuID(cursor.getString(0));
        if (!isEmptyString(cursor.getString(1))) {
            String decryptedPrivateKey = new String(Crypt.aesDecrypt(cursor.getString(1).getBytes(), secret));
            me.setPrivateKey(decryptedPrivateKey);
            cb.loginKeyDBCallback(cursor.getCount());
            //cb.loginKeyDBCallback(decryptedPrivateKey);
        }
        if (!isEmptyString(cursor.getString(2))) me.setPublicKey(cursor.getString(2));
        cursor.close();
    }

    public void postMYKeyData(String Identifier, String privKey, String pubKey) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(U_ID, Identifier);
        contentValue.put(U_PRIV_KEY, privKey);
        contentValue.put(U_PUB_KEY, pubKey);

        String TAG = "DATABASE";
        Log.i(TAG, "DBManager.postMYKeyData(" + privKey + "" + pubKey + ")");

        database.insert(U_TABLE_NAME, null, contentValue);
    }

    public Cursor fetch() {
        return database.query(P_TABLE_NAME, P_COLUMNS_LIST, null, null, null, null, P_TIME + " DESC");
    }

    public void insert_Post(Post post) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(P_ID, post.getPid());
        contentValue.put(P_UID, post.getUid());
        contentValue.put(P_TYPE, post.getPostType());
        contentValue.put(P_TIME, post.getTimeCreated());
        contentValue.put(P_REPLYTO, post.getReplyTo());
        contentValue.put(P_TEXT, post.getText());
        contentValue.put(P_ANTITAMPER, post.getAntiTamper());
        if (post.getBlockChainTXN() != null) {
            contentValue.put(P_BLOCKCHAIN, convertArrayToString(post.getBlockChainTXN()));
        }
        if (post.getResponseList() != null) {
            contentValue.put(P_REPLYLIST, convertArrayToString(post.getResponseList()));
        }
        if (post.getImages() != null) {
            contentValue.put(P_IMAGELIST, convertArrayToString(post.getImages()));
        }
        database.insert(P_TABLE_NAME, null, contentValue);
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

            post.setPostType(cur.getInt(cur.getColumnIndex(P_TYPE)));
            post.setPid(cur.getString(cur.getColumnIndex(P_ID)));
            post.setUid(cur.getString(cur.getColumnIndex(P_UID)));
            post.setText(cur.getString(cur.getColumnIndex(P_TEXT)));
            post.setTimeCreated(cur.getLong(cur.getColumnIndex(P_TIME)));
            post.setReplyTo(cur.getString(cur.getColumnIndex(P_REPLYTO)));
            post.setAntiTamper(cur.getString(cur.getColumnIndex(P_ANTITAMPER)));

            if ((cur.getString(cur.getColumnIndex(P_BLOCKCHAIN))) != null) {
                post.setBlockChainTXN(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_BLOCKCHAIN))));
            }
            if ((cur.getString(cur.getColumnIndex(P_REPLYLIST))) != null) {
                post.setResponseList(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_REPLYLIST))));
            }
            if ((cur.getString(cur.getColumnIndex(P_IMAGELIST))) != null) {
                post.setImages(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_IMAGELIST))));
            }

            arr.add(post);
        } while (cur.moveToNext());

        return arr;
    }

    public void insert_User(User user, String Secret) {
        ContentValues contentValue = new ContentValues();
        contentValue.put(U_ID, user.getuID());
        contentValue.put(U_HANDLE, user.getDisplayName());
        contentValue.put(U_PUB_KEY, user.getPublicKey());
        contentValue.put(U_PRIV_KEY, user.getPrivateKey());
        contentValue.put(U_IS_FOLLOW, user.getIsFollowing());
        contentValue.put(U_SECRET, Secret);
        database.insert(U_TABLE_NAME, null, contentValue);
    }

    public void update_User(User user, String key, String Value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(key, Value);
        database.update(U_TABLE_NAME, contentValues, U_ID + " = ?", new String[]{user.getuID()});
    }

    public void get_User_By_Hash(ICallback cb, String hash) {
        User thisUser;
        Cursor cur = database.query(
                DatabaseHelper.U_TABLE_NAME,
                new String[]{
                        U_ID,
                        U_HANDLE,
                        U_PROF_IMG,
                        U_PUB_KEY,
                        U_PRIV_KEY,
                        U_IS_FOLLOW
                },
                U_SECRET + " = ?", //Where Clause
                new String[]{hash},
                null,
                null,
                null,
                null);

        if (!cur.moveToFirst()) {
            cb.loginKeyDBCallback(0);
            return;
        }

        do {
            String uID = cur.getString(cur.getColumnIndex(U_ID));
            String uHandle = cur.getString(cur.getColumnIndex(U_HANDLE));
            String uProfImg = cur.getString(cur.getColumnIndex(U_PROF_IMG));
            String uPubKey = cur.getString(cur.getColumnIndex(U_PUB_KEY));
            String uPrivKey = cur.getString(cur.getColumnIndex(U_PRIV_KEY));
            Boolean uIsFollow = (cur.getInt(cur.getColumnIndex(U_IS_FOLLOW)) == 1);

            thisUser = new User(uID, uHandle, uIsFollow, uPubKey, uProfImg);
            thisUser.setPrivateKey(uPrivKey);
        } while (cur.moveToNext());
        BAINServer.getInstance().setUser(thisUser); //Updates entire User with data pulled from DB
        cb.loginKeyDBCallback(cur.getCount());
        cur.close();
    }

    public void insert_Image(Texture image) {
        Cursor cur = database.query(I_TABLE_NAME,
                null, I_ID + " = ?", new String[]{image.getUUID()},
                null, null, null, "1");
        if (cur.moveToFirst()) return;

        ContentValues contentValue = new ContentValues();
        contentValue.put(I_ID, image.getUUID());
        contentValue.put(I_STRING, image.getImageString());
        database.insert(I_TABLE_NAME, null, contentValue);
        cur.close();
    }

    public Object array_ID_Search(String Hash) {
        for (User thisUser : usrList)
            if (thisUser.getuID().matches(Hash))
                return thisUser;
        for (Post thisPost : postList)
            if (thisPost.getPid().matches(Hash))
                return thisPost;
        for (Texture thisTexture : textureList)
            if (thisTexture.getUUID().matches(Hash))
                return thisTexture;
        return null;
    }

    public Object db_ID_Search(String hash) {
        String[][] searchArray = new String[3][2];
        searchArray[0][0] = U_TABLE_NAME;
        searchArray[0][1] = U_ID;
        searchArray[1][0] = P_TABLE_NAME;
        searchArray[1][1] = P_ID;
        searchArray[2][0] = I_TABLE_NAME;
        searchArray[2][1] = I_ID;

        for (String[] strings : searchArray) {
            Cursor cur = database.query(strings[0],
                    null, strings[1] + " = ?", new String[]{hash},
                    null, null, null, "1");
            if (cur.moveToFirst()) {
                do {
                    switch (strings[0]) {
                        case DatabaseHelper.U_TABLE_NAME:
                            User user = createUserFromCursor(cur);
                            usrList.add(user);
                            return user;
                        case DatabaseHelper.P_TABLE_NAME:
                            Post post = createPostFromCursor(cur);
                            postList.add(post);
                            return post;
                        case I_TABLE_NAME:
                            Texture tex = createTextureFromCursor(cur);
                            textureList.add(tex);
                            return tex;
                    }
                } while (cur.moveToNext());
            }
            cur.close();
        }
        return null;
    }

    private Texture createTextureFromCursor(Cursor cur) {
        Texture tex = new Texture();
        tex.setUUID(cur.getString(cur.getColumnIndex(I_ID)));
        tex.setImageStringD(cur.getString(cur.getColumnIndex(I_STRING)));
        return tex;
    }

    private User createUserFromCursor(Cursor cur) {
        User user = new User();
        user.setuID(cur.getString(cur.getColumnIndex(U_ID)));
        user.setDisplayName(cur.getString(cur.getColumnIndex(U_HANDLE)));
        user.setProfileImageID(cur.getString(cur.getColumnIndex(U_PROF_IMG)));
        user.setPublicKey(cur.getString(cur.getColumnIndex(U_PUB_KEY)));
        user.setIsFollowing((cur.getInt(cur.getColumnIndex(U_IS_FOLLOW)) == 1));
        return user;
    }

    private Post createPostFromCursor(Cursor cur) {
        Post post = new Post();
        post.setBlockChainTXN(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_BLOCKCHAIN))));
        post.setPostType(cur.getInt(cur.getColumnIndex(P_TYPE)));
        post.setPid(cur.getString(cur.getColumnIndex(P_ID)));
        post.setUid(cur.getString(cur.getColumnIndex(P_UID)));
        post.setText(cur.getString(cur.getColumnIndex(P_TEXT)));
        post.setTimeCreated(cur.getLong(cur.getColumnIndex(P_TIME)));
        post.setReplyTo(cur.getString(cur.getColumnIndex(P_REPLYTO)));
        post.setAntiTamper(cur.getString(cur.getColumnIndex(P_ANTITAMPER)));
        post.setResponseList(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_REPLYLIST))));
        post.setImages(convertStringToArrayList(cur.getString(cur.getColumnIndex(P_IMAGELIST))));
        return post;
    }

    public String[] directory_Search(String hash) {
        Cursor res = database.query(D_TABLE_NAME, null,
                D_UID + " = ?", //Where Clause
                new String[]{hash}, null, null, null);

        if (!res.moveToFirst()) return null;

        String[] address = new String[3];
        do {
            address[res.getColumnIndex(D_UID)] = res.getString(res.getColumnIndex(D_UID));
            address[res.getColumnIndex(D_PUB_ADDRESS)] = res.getString(res.getColumnIndex(D_PUB_ADDRESS));
            address[res.getColumnIndex(D_PORT)] = res.getString(res.getColumnIndex(D_PORT));
        } while (res.moveToNext());

        res.close();
        return address;
    }

    public void directory_Insert(String hash, String address, String port) {
        String[] dirSearch = directory_Search(hash);
        directory_update(hash, address, port);

        if (dirSearch != null) {
            directory_update(hash, address, port);
            return;
        }

        ContentValues contentValue = new ContentValues();
        contentValue.put(D_UID, hash);
        contentValue.put(D_PUB_ADDRESS, address);
        contentValue.put(D_PORT, port);
        database.insert(D_TABLE_NAME, null, contentValue);
    }

    public void directory_update(String hash, String address, String port) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(D_PUB_ADDRESS, address);
        contentValues.put(D_PORT, port);
        database.update(D_TABLE_NAME, contentValues, D_UID + " = ?", new String[]{hash});
    }

    private Cursor query(String table, String[] columns) {
        return database.query(table, columns, null, null, null, null, null);
    }

}