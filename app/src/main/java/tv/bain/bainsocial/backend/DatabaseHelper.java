package tv.bain.bainsocial.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final int DB_VERSION = 1;
    static final String DB_NAME = "BAINSOCIAL.DB";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String U_TABLE_NAME = "USERDATA"; //this table is for the local user.
    public static final String U_ID = "ID"; //MD5 Hash based on Private Key
    public static final String U_IS_FOLLOW = "isFollow"; //0 is no, 1 is Yes
    public static final String U_PRIV_KEY = "PrivateKey"; //Private Key This only gets used once by the user of the phone
    public static final String U_PUB_KEY = "PublicKey"; //Public Key this stores data of those who were connected to
    public static final String U_HANDLE = "Handle"; //What the user is called on the network
    public static final String U_SECRET = "Secret"; //AES Key or Hashed pass in case of local user.
    private static final String CREATE_U_TABLE =
            "create table " + U_TABLE_NAME +
                    "(" +
                    U_ID + " TEXT PRIMARY KEY," +
                    U_IS_FOLLOW + " BOOLEAN," +
                    U_HANDLE + " TEXT, " +
                    U_PRIV_KEY + " TEXT, " +
                    U_PUB_KEY + " TEXT," +
                    U_SECRET + " TEXT" +
                    ");";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String P_TABLE_NAME = "POSTDATA";
    public static final String P_BLOCKCHAIN = "blkChainTxn";
    public static final String P_TYPE = "pType"; //Use STATIC Variables to assign a type
    public static final String P_ID = "pID"; //Unique MD5 hash Identifying this post Based on the following criteria. Images+Text
    public static final String P_UID = "uID"; //MD5 Of Author
    public static final String P_TITLE = "title";
    public static final String P_TEXT = "text";
    public static final String P_TIME = "createTime";  // UTC time currentTimeMillis()
    public static final String P_REPLYTO = "replyTo"; // BAIN://uID:pID the user and post this connects to
    public static final String P_ANTITAMPER = "antiTamper"; //a MD5 Hash recalculated every time there os a change in replies
    public static final String P_REPLYLIST = "responseList"; // {<uID:pID>} //User ID and Post ID
    public static final String[] P_COLUMNS_LIST = new String[]{
            DatabaseHelper.P_BLOCKCHAIN,
            DatabaseHelper.P_TYPE,
            DatabaseHelper.P_ID,
            DatabaseHelper.P_UID,
            DatabaseHelper.P_TITLE,
            DatabaseHelper.P_TEXT,
            DatabaseHelper.P_TIME,
            DatabaseHelper.P_REPLYTO,
            DatabaseHelper.P_ANTITAMPER,
            DatabaseHelper.P_REPLYLIST
    };

    private static final String CREATE_P_TABLE =
            "create table " + P_TABLE_NAME +
                    "(" +
                    P_BLOCKCHAIN + " TEXT, " +
                    P_TYPE + " TEXT, " +
                    P_ID + " TEXT PRIMARY KEY, " +
                    P_UID + " TEXT NOT NULL, " +
                    P_TITLE + " TEXT, " +
                    P_TEXT + " TEXT," +
                    P_TIME + " TEXT," +
                    P_REPLYTO + " TEXT," +
                    P_ANTITAMPER + " TEXT," +
                    P_REPLYLIST + " TEXT" +
                    ");";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String D_TABLE_NAME = "DIRECTORY";
    public static final String D_UID = "uID"; //Public Key this stores data of those who were connected to
    public static final String D_PUB_ADDRESS = "Address"; //Public Address
    public static final String D_PORT = "Port"; //Public Address
    private static final String CREATE_D_TABLE =
            "create table " + D_TABLE_NAME +
                    "(" +
                    D_UID + " TEXT PRIMARY KEY, " +
                    D_PUB_ADDRESS + " TEXT NOT NULL, " +
                    D_PORT + " TEXT NOT NULL" +
                    ");";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_U_TABLE);
        db.execSQL(CREATE_P_TABLE);
        db.execSQL(CREATE_D_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + U_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + P_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + D_TABLE_NAME);
        onCreate(db);
    }
}