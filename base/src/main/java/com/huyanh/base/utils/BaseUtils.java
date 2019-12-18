package com.huyanh.base.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.TypedValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;

public class BaseUtils {

    public static String getCountry(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(BaseConstant.KEY_COUNTRY_REQUEST, "VN");
    }

    public static void setCountry(Context context, String country) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (!pref.getString(BaseConstant.KEY_COUNTRY_REQUEST, "").equals("")) return;
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BaseConstant.KEY_COUNTRY_REQUEST, country);
        editor.apply();
    }

    public static void setDateInstall(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getString(BaseConstant.KEY_INSTALL_DATE, null) == null) {
            SharedPreferences.Editor editor = pref.edit();
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            String date = year + "-" + standardNumber(month + 1) + "-"
                    + standardNumber(day);
            editor.putString(BaseConstant.KEY_INSTALL_DATE, date);
            editor.apply();
        }
    }

    private static String standardNumber(int number) {
        if (number <= 9) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    public static String getDateInstall(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String date = pref.getString(BaseConstant.KEY_INSTALL_DATE, "");
        if (!date.equals("")) {
            return date;
        }
        setDateInstall(context);
        return pref.getString(BaseConstant.KEY_INSTALL_DATE, "");
    }

    public static String getDeviceID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

//    public static String getFirstEmail(Context context) {
//        try {
//            Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
//            Account[] accounts = AccountManager.get(context).getAccounts();
//            for (Account account : accounts) {
//                if (emailPattern.matcher(account.name).matches()) {
//                    return account.name;
//                }
//            }
//        } catch (Exception e) {
//        }
//        return "";
//    }
//
//    public static String getOs_version() {
//        return new StringBuilder(String.valueOf(Build.VERSION.SDK_INT)).toString();
//    }
//
//    public static String getNetwork(Context context) {
//        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperatorName().replace(" ", "%20");
//    }
//
//    public static String getCh_play(Context context) {
//        if (isInstalled(context, "com.android.vending")) {
//            return "1";
//        }
//        return "0";
//    }

    public static boolean isInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public static void copyFilefromAsset(Context conetxt, String filename, String filePath) {
//        try {
//            InputStream in = conetxt.getAssets().open(filename);
//            OutputStream out = new FileOutputStream(filePath);
//            try {
//                byte[] buffer = new byte[1024];
//                while (true) {
//                    int read = in.read(buffer);
//                    if (read == -1) {
//                        in.close();
//                        out.flush();
//                        out.close();
//                        return;
//                    }
//                    out.write(buffer, 0, read);
//                }
//            } catch (Exception e2) {
//                OutputStream outputStream = out;
//                Log.e("error copy file from assets to sd: " + e2.getMessage());
//            }
//        } catch (Exception e3) {
//            Log.e("error copy file from assets to sd: " + e3.getMessage());
//        }
//    }
//
//
//    public static String getlanguage() {
//        return Locale.getDefault().getLanguage();
//    }
//
//    public static String getDeviceName() {
//        return Build.MANUFACTURER + " - " + Build.MODEL;
//    }
//
//    //facebook hash key
//    public static String printKeyHash(android.app.Activity r13) {
//        PackageInfo info;
//        try {
//            info = r13.getPackageManager().getPackageInfo(r13.getPackageName(), PackageManager.GET_SIGNATURES);
//            for (android.content.pm.Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
////                md.update(signature.sign());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.d("hashkey: " + something);
//            }
//        } catch (Exception e) {
//            Log.e("error get facebook hash key: " + e.toString());
//        }
//        return "";
//    }

    public static void gotoUrl(Context context, String url) {
        Intent i = new Intent("android.intent.action.VIEW");
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
//
//    public static void shareText(Context context, String text, String title, String choosen_app) {
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType("text/plain");
//        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//
//        // Add data to the intent, the receiving app will decide
//        // what to do with it.
//        share.putExtra(Intent.EXTRA_SUBJECT, title);
//        share.putExtra(Intent.EXTRA_TEXT, text);
//
//        context.startActivity(Intent.createChooser(share, choosen_app));
//    }
//
//    public static boolean copyToClipboard(Context context, String title, String content) {
//        try {
//            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText(title, content);
//            clipboard.setPrimaryClip(clip);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static String convertTimeStoString(int second) {
//        if (second <= 60) {
//            return second + " giây";
//        } else {
//            int timeM = second / 60;
//            if (timeM <= 60) return timeM + " phút";
//            else {
//                int timeH = timeM / 60;
//                if (timeH <= 24) return timeH + " giờ";
//                else {
//                    int timeD = timeH / 24;
//                    if (timeD <= 7) return timeD + " ngày";
//                    else {
//                        int timeW = timeD / 7;
//                        if (timeW < 5) return timeW + " tuần";
//                        else return "vài tuần";
//                    }
//                }
//            }
//        }
//    }
//
//    public static String convertTimeS(int millisecond) {
//        int second = millisecond / 1000;
//        int minutes = second / 60;
//        second = second - minutes * 60;
//        if (minutes < 10) {
//            if (second < 10) return "0" + minutes + ":0" + second;
//            else
//                return "0" + minutes + ":" + second;
//        } else {
//            if (second < 10) return minutes + ":0" + second;
//            else
//                return minutes + ":" + second;
//        }
//    }

    public static String readFileFromAsset(Context context, String fileName) {
        try {
            InputStream stream = context.getAssets().open(fileName);
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            Log.d("doc file asset: " + fileName);
            return new String(buffer);
        } catch (IOException e) {
            Log.e("error read file asset: " + fileName + " msg: " + e.getMessage());
        }
        return "";

//        BufferedReader reader = null;
//        String result = "";
//        try {
//            reader = new BufferedReader(
//                    new InputStreamReader(context.getAssets().open(fileName), "UTF-8"));
//            String mLine;
//            while ((mLine = reader.readLine()) != null) {
//                //process line
//                result += mLine;
//            }
//        } catch (IOException e) {
//            //log the exception
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    //log the exception
//                }
//            }
//        }
//        return result;
    }

    public static String readTxtFile(File file) {
//        try {
//            FileInputStream fIn = new FileInputStream(file);
//            BufferedReader myReader = new BufferedReader(
//                    new InputStreamReader(fIn));
//            String aDataRow = "";
//            String aBuffer = "";
//            while ((aDataRow = myReader.readLine()) != null) {
//                aBuffer += aDataRow + "\n";
//            }
//            myReader.close();
//            Log.d("read file " + file.getName());
//            return aBuffer;
//        } catch (Exception e) {
//            Log.e("error read txt file sdcard: " + e.getMessage());
//        }
//        return "";
        try {
            InputStream input = new FileInputStream(file);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            Log.d("doc file " + file.getName());
            return new String(buffer);
        } catch (Exception e) {
            Log.e("error read file: " + file.getName() + " msg: " + e.getMessage());
            return "";
        }
    }

    public static boolean writeTxtFile(File file, String fileContents) {
//        try {
//            FileOutputStream outputStream = context.openFileOutput(file.getName(), Context.MODE_PRIVATE);
//            outputStream.write(fileContents.getBytes());
//            outputStream.close();
//            Log.d("write file " + file.getName());
//            return true;
//        } catch (Exception e) {
//            Log.e("error write file: " + file.getName() + " msg: " + e.getMessage());
//            return false;
//        }
        try {
            FileWriter out = new FileWriter(file);
            out.write(fileContents);
            out.close();
            Log.d("write file " + file.getName());
            return true;
        } catch (IOException e) {
            Log.e("error write file: " + file.getName() + " msg: " + e.getMessage());
            return false;
        }
    }

    public static int genpx(Context context, int dp) {
        int pxTemp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());
        if (pxTemp != 0)
            return pxTemp;
        else return dp * 4;
    }

    public static float genpx(Context context, float dp) {
        float pxTemp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
                .getDisplayMetrics());
        if (pxTemp != 0)
            return pxTemp;
        else return dp * 4.0f;
    }


    private static char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
            'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
            'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
            'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
            'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
            'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
            'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
            'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
            'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
            'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
            'ữ', 'Ự', 'ự'};

    // Mang cac ky tu thay the khong dau
    private static char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
            'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
            'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
            'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
            'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'U', 'u'};

    // Bo dau 1 ky tu
    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    //Bo dau 1 chuoi
    public static String removeAccent(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return removeSpecial(sb.toString());
    }

    public static String removeSpecial(String s) {
        return s.replaceAll("\\`", "").replaceAll("\\~", "").replaceAll("\\!", "").replaceAll("\\@", "").replaceAll("\\#", "").replaceAll("\\$", "").replaceAll("\\%", "").replaceAll("\\^", "").replaceAll("\\&", "").replaceAll("\\*", "").replaceAll("\\(", "").replaceAll("\\)", "")
                .replaceAll("\\-", "").replaceAll("\\_", "").replaceAll("\\=", "").replaceAll("\\+", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\{", "").replaceAll("\\}", "")
                .replaceAll("\\;", "").replaceAll("\\:", "").replaceAll("\\,", "").replaceAll("\\<", "").replaceAll("\\.", "").replaceAll("\\>", "").replaceAll("\\/", "").replaceAll("\\?", "");
    }

    public static final String md5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

//    public static void gotoStore(Context context) {
//        final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
//        try {
//            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//        } catch (android.content.ActivityNotFoundException anfe) {
//            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//        }
//    }
}
