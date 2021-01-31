package tv.bain.bainsocial.datatypes;

import javax.crypto.SecretKey;

import tv.bain.bainsocial.ICallback;

public class User {
    private ICallback cb;
    public void setCallback(ICallback cb) { this.cb = cb; }
    public ICallback getCallback() { return cb; }

    private String hashedPass;
    public void setHashedPass(String hashedPass) { this.hashedPass = hashedPass; }
    public String getHashedPass(){ return hashedPass; }

    private SecretKey secret; //the passphrase entered at the login page used to generate AES Secrets
    private String Identifier;
    private String PrivateKey;
    private String PublicKey;
    private String DisplayName;
    private ICallback CallbackActivity;

    public User() {}
    public void setCallbackActivity(ICallback CallbackActivity) { this.CallbackActivity = CallbackActivity; }

    public void setSecret(SecretKey secret) { this.secret = secret; }
    public SecretKey getSecret() { return secret; }
    public void setDisplayName(String DisplayName) { this.DisplayName = DisplayName; }
    public String getDisplayName() { return DisplayName; }
    public void setIdentifier(String Identifier) { this.Identifier = Identifier; }
    public String getIdentifier() { return Identifier; }
    public void setPrivateKey(String PrivateKey) { this.PrivateKey = PrivateKey; }
    public String getPrivateKey() { return PrivateKey; }
    public void setPublicKey(String PublicKey) { this.PublicKey = PublicKey; }
    public String getPublicKey() { return PublicKey; }

}
