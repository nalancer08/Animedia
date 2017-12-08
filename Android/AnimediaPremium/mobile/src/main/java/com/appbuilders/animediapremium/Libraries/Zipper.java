package com.appbuilders.animediapremium.Libraries;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by saer6003 on 26/01/2017.
 */

public class Zipper {

    private String path;

    public Zipper() {

        String path = "/storage/emulated/0/media/hummingbird.zip";

        ZipInputStream zipIs = null;

        try {
            zipIs = new ZipInputStream( new FileInputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ZipEntry entry = null;

        try {
            while ( ( entry = zipIs.getNextEntry() ) != null ){

                if ( entry.isDirectory() ) {
                    Log.d("AB_DEV", "Directory: ");
                } else {
                    System.out.print("File: ");
                }
                Log.d("AB_DEV", entry.getName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            zipIs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void unZip() {

        String path = "/storage/emulated/0/media/hummingbird.zip";
        String out = "/storage/emulated/0/media/hummingbird_auto";

        // Create Output folder if it does not exists
        File folder = new File(out);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Create buffer
        byte[] buffer = new byte[1024];

        ZipInputStream zipIs = null;
        try {

            // Create ZipInputStream read a file from path.
            zipIs = new ZipInputStream(new FileInputStream(path));

            ZipEntry entry = null;

            // Read ever Entry (From top to bottom until the end)
            while ((entry = zipIs.getNextEntry()) != null) {
                String entryName = entry.getName();
                String outFileName = out + File.separator + entryName;
                System.out.println("Unzip: " + outFileName);

                if (entry.isDirectory()) {

                    // Make directories
                    new File(outFileName).mkdirs();
                } else {

                    // Create Stream to write file.
                    FileOutputStream fos = new FileOutputStream(outFileName);

                    int len;

                    // Read the data on the current entry.
                    while ((len = zipIs.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipIs.close();
            } catch (Exception e) {
            }
        }


    }

}
