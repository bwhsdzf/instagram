package com.mobile.instagram.activities.user_photo;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class FileStorage {
    private File cropDir;
    private File OriginDir;

    public FileStorage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File external = Environment.getExternalStorageDirectory();
            String rootDir = "/" + "demo";
            cropDir = new File(external, rootDir + "/crop");
            if (!cropDir.exists()) {
                cropDir.mkdirs();
            }
            OriginDir = new File(external, rootDir + "/icon");
            if (!OriginDir.exists()) {
                OriginDir.mkdirs();
            }
        }
    }

    public File createCropFile() {
        String fileName = "";
        if (cropDir != null) {
            fileName = UUID.randomUUID().toString() + ".png";
        }
        return new File(cropDir, fileName);
    }

    public File createIconFile() {
        String fileName = "";
        if (OriginDir != null) {
            fileName = UUID.randomUUID().toString() + ".png";
        }
        return new File(OriginDir, fileName);
    }

}