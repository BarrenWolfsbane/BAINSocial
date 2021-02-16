package tv.bain.bainsocial.datatypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.backend.BAINServer;

public class User implements Serializable {
    public static ArrayList<User> usrList; //this array list is used to store User Objects.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // The Following Variables are Exclusive to the logged in user and will never be pulled       //
    // for any other users who we load into our arrays                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String hashedPass; //hashed pass will be blank for all users not being the main user.
    public void setHashedPass(String hashedPass) {
        this.hashedPass = hashedPass;
    }
    public String getHashedPass() {
        return hashedPass;
    }
    private String PrivateKey; //Private key is exclusive to you
    public String getPrivateKey() {
        return PrivateKey;
    }
    public void setPrivateKey(String PrivateKey) {
        this.PrivateKey = PrivateKey;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // The following are used for encryptions                                                     //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String PublicKey;
    public void setPublicKey(String PublicKey) {
        this.PublicKey = PublicKey;
    }
    public String getPublicKey() {
        return PublicKey;
    }
    private SecretKey secret; //This will be used for direct communications after received via PGP
    public void setSecret(SecretKey secret) {
        this.secret = secret;
    }
    public SecretKey getSecret() {
        return secret;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String uID; //Unique MD5 hash Identifying this user based on the Private Key.
    public void setuID(String uID) {
        this.uID = uID;
    }
    public String getuID() {
        return uID;
    }

    private String DisplayName;
    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }
    public String getDisplayName() {
        return DisplayName;
    }

    private Boolean isFollowing;
    public void setIsFollowing(Boolean isFollowing) { this.isFollowing = isFollowing; }
    public Boolean getIsFollowing() { return isFollowing; }

    private String profileImageID;
    public void setProfileImageID(String image) { this.profileImageID = image; }
    public String getProfileImageID(){ return profileImageID; }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public User() {}
    public User(JSONObject object){
        try {
            this.uID = object.getString("uID");
            this.DisplayName = object.getString("DisplayName");
            this.isFollowing = object.getBoolean("isFollowing");
            this.PublicKey = object.getString("PublicKey");
        } catch (JSONException e) { BAINServer.getInstance().SendToast(e.getMessage()); }
    }
    public User(String uID, String DisplayName,Boolean isFollowing,String PublicKey,String profileImageID) {
        this.uID = uID;
        this.DisplayName = DisplayName;
        this.profileImageID = profileImageID;
        this.isFollowing = isFollowing;
        this.PublicKey = PublicKey;
    }
}
