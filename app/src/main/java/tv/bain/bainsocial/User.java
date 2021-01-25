package tv.bain.bainsocial;

import javax.crypto.SecretKey;

public class User {
    SecretKey secret; //the passphrase entered at the login page used to generate AES Secrets
    String Identifier;
    String PrivateKey;
    String PublicKey;
    String DisplayName;

    User() { }
    public void setSecret(SecretKey secret) { this.secret = secret; }
    public SecretKey getSecret() { return secret; }
    public void initializeUser(DBManager DB){
        DB.open(); DB.getMyKeyData(this); DB.close();
        if(PrivateKey.isEmpty()) {
            FileControls fc = new FileControls();
            if (fc.doesExist("priv.key")) {
                byte[] encryptedPrivateKey = fc.readFile("priv.key").getBytes();
                setPrivateKey(Crypt.aesDecrypt(encryptedPrivateKey, getSecret()));
                setIdentifier(Crypt.md5(getPrivateKey())); //Identifier is based on the Private Key.
            }
        }
        if(PublicKey.isEmpty()) {
            FileControls fc = new FileControls();
            if (fc.doesExist("pub.key")) {
                String PubKey = fc.readFile("pub.key");
                setPublicKey(PubKey);
            }
        }
    }

    public void setDisplayName(String DisplayName) { this.DisplayName = DisplayName; }
    public String getDisplayName() { return DisplayName; }
    public void setIdentifier(String Identifier) { this.Identifier = Identifier; }
    public String getIdentifier() { return Identifier; }
    public void setPrivateKey(String PrivateKey) { this.PrivateKey = PrivateKey; }
    public String getPrivateKey() { return PrivateKey; }
    public void setPublicKey(String PublicKey) { this.PublicKey = PublicKey; }
    public String getPublicKey() { return PublicKey; }

}
