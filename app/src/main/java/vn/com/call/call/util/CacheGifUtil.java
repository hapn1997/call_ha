package vn.com.call.call.util;

import android.content.Context;

import java.io.File;
import java.security.MessageDigest;

import vn.com.call.call.model.CallFlashItem;

public class CacheGifUtil {

    public static File getCacheFileGif(Context context, CallFlashItem item) {
        File dir = new File(context.getCacheDir(), "TempFileFlash");
        if (!dir.exists()) {
            dir.mkdir();
        }
        String cachePath = dir.getPath();
        String nameFile = item.getId() + "_" + md5(item.getVideo());
        File fileGif = new File(cachePath, nameFile);
        return fileGif;
    }

    private static String md5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
        }
        return "";
    }

}
