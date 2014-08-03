package com.fray.launcher;

import android.webkit.MimeTypeMap;

/**
 * Created by ray on 03.08.2014.
 */
public class FrayTools {
    public static String get_mime_by_filename(String filename){
        String ext;
        String type;

        int lastdot = filename.lastIndexOf(".");
        if(lastdot > 0){
            ext = filename.substring(lastdot + 1);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(ext);
            if(type != null) {
                return type;
            }
            if (ext.equals("fb2"))
                return "application/x-fictionbook";
        }
        return "*/*";
    }
}
