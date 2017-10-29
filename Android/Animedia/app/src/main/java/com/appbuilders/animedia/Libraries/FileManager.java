package com.appbuilders.animedia.Libraries;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by saer6003 on 26/01/2017.
 */

public class FileManager {

    public FileManager() {

        String path = Environment.getExternalStorageDirectory().getPath();
        File f = new File(path);

        File[] files = f.listFiles();

        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                Log.d("AB_DEV", "Is directory: " + inFile.getName());

                File f2 = new File(inFile.getPath());
                File[] filesSub = f2.listFiles();
                for (File inFile2 : filesSub) {
                    Log.d("AB_DEV", "--" + inFile2.getName());
                    if ( inFile2.getName().equals("hummingbird.zip") ) {
                        Log.d("AB_DEV", "----> " + inFile2.getPath());
                        ///storage/emulated/0/media/hummingbird.zip
                    }
                }

            } else {
                Log.d("AB_DEV", "Is file: " + inFile.getName());
            }
        }
    }
}
