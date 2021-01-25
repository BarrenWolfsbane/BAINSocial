package tv.bain.bainsocial;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
    static final int DB_VERSION = 1;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    static final String DB_NAME = "BAINSOCIAL.DB";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String KEY_TABLE_NAME = "KEYDATA"; //this table is for the local user.
    public static final String ID = "ID"; //Private Key This only gets used once by the user of the phone
    public static final String PRIV_KEY = "PrivateKey"; //Private Key This only gets used once by the user of the phone
    public static final String PUB_KEY = "PublicKey"; //Public Key this stores data of those who were connected to
    private static final String CREATE_KEY_TABLE = "create table "+KEY_TABLE_NAME+"("+ID+" TEXT PRIMARY KEY,"+PRIV_KEY+" TEXT, "+PUB_KEY+" TEXT);";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String PUB_TABLE_NAME = "PUBINFO"; //Store your data in it's own Table
    public static final String MY_PUB_ADDRESS = "Address"; //Encrypted Public Address
    public static final String MY_HANDLE = "Handle"; //What the user is called on the network
    private static final String CREATE_PUBINFO_TABLE = "create table "+PUB_TABLE_NAME+"("+MY_PUB_ADDRESS+" TEXT PRIMARY KEY, "+MY_HANDLE+" TEXT);";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String POST_TABLE_NAME = "POSTDATA";
    public static final String _ID = "_id";
    public static final String SUBJECT = "subject";
    public static final String DESC = "description";
    private static final String CREATE_POST_TABLE = "create table "+POST_TABLE_NAME+"("+_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+SUBJECT+" TEXT NOT NULL, "+DESC+" TEXT);";
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static final String DIR_TABLE_NAME = "DIRECTORY";
    public static final String USR_PUB_KEY = "PublicKey"; //Public Key this stores data of those who were connected to
    public static final String USR_PUB_ADDRESS = "Address"; //Encrypted Public Address
    public static final String USR_HANDLE = "Handle"; //What the user is called on the network
    public static final String IS_FOLLOW = "ISFOLLOW"; //Boolean, Are we following, If so we also pull recent post data for timeline
    private static final String CREATE_DIRECTORY_TABLE = "create table "+DIR_TABLE_NAME+"("+USR_PUB_KEY+" TEXT PRIMARY KEY, "+USR_PUB_ADDRESS+" TEXT NOT NULL, "+USR_HANDLE+" TEXT, "+IS_FOLLOW+" BOOLEAN);";

    public DatabaseHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }
    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_KEY_TABLE);
        db.execSQL(CREATE_PUBINFO_TABLE);
        db.execSQL(CREATE_POST_TABLE);
        db.execSQL(CREATE_DIRECTORY_TABLE);
    }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PUB_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + POST_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DIR_TABLE_NAME);
        onCreate(db);
    }
}