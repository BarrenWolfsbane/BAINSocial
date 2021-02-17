package tv.bain.bainsocial.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import tv.bain.bainsocial.backend.DatabaseHelper;

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
    public static ArrayList<Post> postList; //this array list is used to store Post Objects.
    public static Integer SHORT280 = 0; //Text Limited to 280 characters Commonly used on Twitter.
    private Integer postType; //Use STATIC Variables to assign a type
    private List<String> blockChainTXN; // this array will contain any blockchain txns where this post can be found.
    private String uid; //MD5 Of Author
    private String pid; //Unique MD5 hash Identifying this post Based on the following criteria. Images+Text
    private long timeCreated; // UTC time currentTimeMillis()
    private String replyTo; // BAIN://uID:pID //the user and post this connects to
    private List<String> responseList; // {<uID:pID>} //User ID and Post ID
    private String antiTamper; //a MD5 Hash recalculated every time there os a change in replies
    private String text; //the message Text
    private List<String> images;

    public Post() {}

    public Post(JSONObject object) throws JSONException {
        this.pid = object.getString(DatabaseHelper.P_BLOCKCHAIN);
        this.pid = object.getString(DatabaseHelper.P_ID);
        this.uid = object.getString(DatabaseHelper.P_UID);
        this.postType = object.getInt(DatabaseHelper.P_TYPE);
        this.timeCreated = object.getLong(DatabaseHelper.P_TIME);
        this.replyTo = object.getString(DatabaseHelper.P_REPLYLIST);
        this.text = object.getString(DatabaseHelper.P_TEXT);
        this.antiTamper = object.getString(DatabaseHelper.P_ANTITAMPER);

        //Need to add array items
        //Blockchain
        //Images
        //Responses
    }

    //region Getters and Setters
    public List<String> getImages(){ return images; }
    public void setImages(List<String> images){ this.images = images; }

    public static Integer getSHORT280() {
        return SHORT280;
    }

    public static void setSHORT280(Integer SHORT280) {
        Post.SHORT280 = SHORT280;
    }

    public Integer getPostType() {
        return postType;
    }

    public void setPostType(Integer postType) {
        this.postType = postType;
    }

    public List<String> getBlockChainTXN() {
        return blockChainTXN;
    }

    public void setBlockChainTXN(List<String> blockChainTXN) {
        this.blockChainTXN = blockChainTXN;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public List<String> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<String> responseList) {
        this.responseList = responseList;
    }

    public String getAntiTamper() {
        return antiTamper;
    }

    public void setAntiTamper(String antiTamper) {
        this.antiTamper = antiTamper;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public JSONObject toJSON(){
        JSONObject jo = new JSONObject();
        try {
            jo.put(DatabaseHelper.P_BLOCKCHAIN, blockChainTXN);
            jo.put(DatabaseHelper.P_ID, pid);
            jo.put(DatabaseHelper.P_UID, uid);
            jo.put(DatabaseHelper.P_TYPE, postType);
            jo.put(DatabaseHelper.P_TIME, timeCreated);
            jo.put(DatabaseHelper.P_REPLYLIST, replyTo);
            jo.put(DatabaseHelper.P_TEXT, text);
            jo.put(DatabaseHelper.P_ANTITAMPER, antiTamper);
            //We will need to convert the array items and place
            //And place those here next
            //Blockchain
            //Images
            //Responses
        }
        catch (JSONException e) { e.printStackTrace(); }
        return jo;
    }
}
