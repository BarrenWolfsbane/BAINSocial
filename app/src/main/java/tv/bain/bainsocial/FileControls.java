package tv.bain.bainsocial;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileControls {
    public boolean doesExist(String Filename){
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, Filename);
        if(file.exists()) return true;
        else return false;
    }
    public String readFile(String Filename) {
        File sdcard = Environment.getExternalStorageDirectory(); //Find the directory for the SD Card using the API
        File file = new File(sdcard, Filename); //Get the text file
        StringBuilder text = new StringBuilder(); //Read text from file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) { } //You'll need to add proper error handling here
        return text.toString();
    }

}
