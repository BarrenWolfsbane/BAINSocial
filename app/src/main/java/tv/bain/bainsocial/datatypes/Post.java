package tv.bain.bainsocial.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import tv.bain.bainsocial.backend.BAINServer;

/*
    When a post is started it boots up a Blank Post Object
    a Screen appears to compose a post, any images selected will be fingerprinted to a MD5 hash
    Those hashes will be included in this post at the top above the text
    BAIN://ec55d3e698d289f2afd663725127bace:ec55d3e698d289f2afd663725127bace
    We will teach the code to understand anything starting with BAIN is a network Search
    in This case the first hash will be the creators uID, and the second is the ImageHash.
    for the user to have replied to your post they would of had to have connected to you
    Meaning they should be in the database. If not we can just perform a search.

    The Image Data and the Text are combined, hashed and become an ID. for the post.
    This info is then stored in the DB, recalled when your page is visited.
 */
public class Post implements Serializable {
    private String[] blockChainTXN; // this array will contain any blockchain txns where this post can be found.

    public String[] getBlockChainTXN() {
        return blockChainTXN;
    }

    public static Integer SHORT280 = 0; //Text Limited to 280 characters Commonly used on Twitter.

    private Integer pType; //Use STATIC Variables to assign a type

    public Integer getpType() {
        return pType;
    }

    public void setpType(Integer pType) {
        this.pType = pType;
    }

    private String uID; //MD5 Of Author

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    private String pID; //Unique MD5 hash Identifying this post Based on the following criteria. Images+Text

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    private long timeCreated; // UTC time currentTimeMillis()

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    private String replyTo; // BAIN://uID:pID //the user and post this connects to

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    private String text; //the message Text

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String antiTamper; //a MD5 Hash recalculated every time there os a change in replies

    public String getAntiTamper() {
        return antiTamper;
    }

    public void setAntiTamper(String antiTamper) {
        this.antiTamper = antiTamper;
    }

    private String[] responseList; // {<uID:pID>} //User ID and Post ID

    public String[] getResponseList() {
        return responseList;
    }

    public Post(){
    }
    public Post(JSONObject object){
        try {
            this.pID = object.getString("pID");
            this.uID = object.getString("uID");
            this.pType = object.getInt("pType");
            this.timeCreated = object.getLong("timeCreated");
            this.replyTo = object.getString("replyTo");
            this.text = object.getString("text");
            this.antiTamper = object.getString("antiTamper");
        } catch (JSONException e) { BAINServer.getInstance().SendToast(e.getMessage()); }
    }


}
