package tv.bain.bainsocial;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import net.kibotu.pgp.Pgp;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyRingGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileControls {
    OldMainActivity main;
    private User me;
    private Context context;

    private String KeyDataDirectory;
    private File fileDir,privKeyFile,pubKeyFile;

    FileControls(OldMainActivity main, Context context){
        this.main = main; this.context = context;
        KeyDataDirectory =  "/key_data/";
        fileDir = new File(context.getFilesDir(), KeyDataDirectory);
        privKeyFile = new File(fileDir, "private.key");
        pubKeyFile = new File(fileDir, "public.key");
    }
    public boolean doesExist(File file){
        if(file.exists()) return true;
        else return false;
    }
    public void writeFile(File dir, String Filename, boolean Base64Encode, byte[] data) {
        if(Base64Encode) data = Crypt.b64Enc(data);
        if(!dir.exists()) dir.mkdirs(); // Make sure the path directory exists.
        final File file = new File(dir, Filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(data);
            fOut.flush(); fOut.close();
        }
        catch (IOException e) { Log.e("Exception", "File write failed: " + e.toString()); }
    }
    public byte[] readFile(File file, boolean Base64Decode){
        FileInputStream fis;
        StringBuffer fileContent = new StringBuffer("");
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1)
                fileContent.append(new String(buffer, 0, n));
        }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        if(Base64Decode)return Crypt.b64Dec(fileContent.toString());
        else return (fileContent.toString()).getBytes();
    }

    //Key File Operations
    public boolean keyChecker(User me){
        this.me = me;
        if(doesExist(privKeyFile)) return true;
        else return false;
    }
    public boolean createKeyFiles(TextView keyDataOut){

        keyDataOut.setText("Writing...");
        PGPKeyRingGenerator krgen = null;
        String publicKey = "NULL Key", privateKey = "NULL Key";
        try {
            krgen = Pgp.generateKeyRingGenerator(me.getHashedPass().toCharArray());
            publicKey = Pgp.genPGPPublicKey(krgen);
            privateKey = Pgp.genPGPPrivKey(krgen);
        }
        catch (PGPException e) { e.printStackTrace(); return false; }
        catch (IOException e) { e.printStackTrace(); return false; }

        byte[] LoginToken = Crypt.aesEncrypt("DecodeToken".getBytes(),me.getSecret());
        writeFile(fileDir, "private.key", true, privateKey.getBytes());
        writeFile(fileDir, "public.key", false, publicKey.getBytes());
        writeFile(fileDir, "LoginToken", true, LoginToken);
        keyDataOut.setText("Key Data has been Encrypted and Wrote - B64 Encode: "+new String(privateKey.getBytes()));
        //Send back a true or false if data was applied to me correctly.

        return true;
    }
    public boolean loadKeyFiles(TextView keyDataOut, boolean dbStore){
        keyDataOut.setText("Reading...");
        byte[] privateKey = readFile(privKeyFile,true);

        keyDataOut.setText("Reading BlockSize: "+privateKey.length+ " - "+new String(privateKey));
        if(dbStore){
            DBManager db = main.getDb();
            // TODO: 1/28/2021 Store Newly Generated Keys into the DB Return true
            return true;
        }
        return true;
    }
    public boolean saveKeyFiles(TextView keyDataOut){
        keyDataOut.setText("Database Entries found, Backing up to FS");
        //Take data from me and put it into the database for future use.
        return true;
    }
}