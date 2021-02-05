package tv.bain.bainsocial.backend;

import android.content.Context;
import android.util.Log;

import net.kibotu.pgp.Pgp;

import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPKeyRingGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import tv.bain.bainsocial.datatypes.User;

public class FileControls {
    private User me;

    private String KeyDataDirectory;
    private File fileDir, privKeyFile, pubKeyFile;

    public FileControls(Context context) {
        KeyDataDirectory = "/key_data/";
        fileDir = new File(context.getFilesDir(), KeyDataDirectory);
        privKeyFile = new File(fileDir, "private.key");
        pubKeyFile = new File(fileDir, "public.key");
    }

    public void writeFile(File dir, String Filename, boolean Base64Encode, byte[] data) {
        if (Base64Encode) data = Crypt.b64Enc(data);
        if (!dir.exists()) dir.mkdirs(); // Make sure the path directory exists.

        final File file = new File(dir, Filename);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(data);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public byte[] readFile(File file, boolean Base64Decode) {
        FileInputStream fis;
        StringBuilder fileContent = new StringBuilder("");
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1)
                fileContent.append(new String(buffer, 0, n));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Base64Decode) return Crypt.b64Dec(fileContent.toString());
        else return (fileContent.toString()).getBytes();
    }

    //Key File Operations
    public boolean keyChecker(User me) {
        this.me = me;
        return privKeyFile.exists();
    }

    /**
     * Returns true if the key data has been applied to me correctly, false otherwise.
     */
    public boolean createKeyFiles() {
        PGPKeyRingGenerator keyRingGen;
        String publicKey, privateKey;
        try {
            keyRingGen = Pgp.generateKeyRingGenerator(me.getHashedPass().toCharArray());
            publicKey = Pgp.genPGPPublicKey(keyRingGen);
            privateKey = Pgp.genPGPPrivKey(keyRingGen);
        } catch (PGPException | IOException e) {
            e.printStackTrace();
            return false;
        }

        writeFile(fileDir, "private.key", true, privateKey.getBytes());
        writeFile(fileDir, "public.key", false, publicKey.getBytes());

        me.setuID(Crypt.md5(privateKey));
        me.setIsFollowing(false);
        me.setPrivateKey(privateKey);
        me.setPublicKey(publicKey);
        BAINServer.getInstance().getDb().insert_User(me,me.getHashedPass());

        return true;
    }

    public boolean loadKeyFiles(boolean dbStore) {
        byte[] privateKey = readFile(privKeyFile, true);

        if (dbStore) {
            /* Was previously
            DBManager db = main.getDb();
             */
            DBManager db = BAINServer.getInstance().getDb();
            // TODO: 1/28/2021 Store Newly Generated Keys into the DB Return true
            return true;
        }
        return true;
    }

    public boolean saveKeyFiles() {
        //Take data from me and put it into the database for future use.
        return true;
    }
}