package vn.com.call.editCall;

import static android.content.Context.TELECOM_SERVICE;

import android.app.Activity;
import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.telecom.Call;
import android.telecom.CallAudioState;
import android.telecom.InCallService;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

import vn.com.call.App;

public class CallerHelper {
    private InCallService inCallService;
    private static CallerHelper instance;

    public static final CallerHelper INSTANCE = new CallerHelper();

    public static void startPhoneAccountChooseActivity(Context context, String str) {
        if (context != null) {
            if (context.checkPermission( "android.permission.READ_PHONE_STATE", Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED) {
                context.startActivity(new Intent(context, PhoneAccountChooseActivity.class).putExtra("contactNumber", str).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                return;
            }
        }
    }
    public static final CallerHelper getInstances(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Looper.getMainLooper().isCurrentThread()) {
                if (CallerHelper.instance == null) {
                    CallerHelper.instance = new CallerHelper();
                }
                return CallerHelper.instance;
//            }
        }
        return null;
    }
    public static final boolean isMethodAvailable(String str, String str2, Class<?>... clsArr) {

        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return false;
        }
        if (!(str2 == null || str == null)) {
            try {
                Class.forName(str).getMethod(str2, (Class[]) Arrays.copyOf(clsArr, clsArr.length));
            } catch (ClassNotFoundException unused) {
                String simpleName = INSTANCE.getClass().getSimpleName();
                Log.v(simpleName, "Could not find method: " + ((Object) str) + '#' + ((Object) str2));
                return false;
            } catch (NoSuchMethodException unused2) {
                String simpleName2 = INSTANCE.getClass().getSimpleName();
                Log.v(simpleName2, "Could not find method: " + ((Object) str) + '#' + ((Object) str2));
                return false;
            } catch (Throwable th) {
                String simpleName3 = INSTANCE.getClass().getSimpleName();
                Log.e(simpleName3, "Unexpected exception when checking if method: " + ((Object) str) + '#' + ((Object) str2) + " exists at runtime", th);
                return false;
            }
        }
        return true;
    }
    public void setInCallService(InCallService inCallService) {
        this.inCallService = inCallService;
    }

    public void clearInCallService() {
        this.inCallService = null;
    }


    public final List<Call> getAllCalls() {
        InCallService inCallService = this.inCallService;
        if (inCallService == null || inCallService == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return inCallService.getCalls();
        }
        return null;
    }
    public final CallAudioState getAudioState() {
        InCallService inCallService = this.inCallService;
        if (inCallService == null || inCallService == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return inCallService.getCallAudioState();
        }
        return  null;
    }
    public final void holdCall(Call call) {
        if (call != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                call.hold();
            }
        }
    }
    public static boolean checkpermissiton(Activity context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                roleManager = App.getAppThis().getSystemService(RoleManager.class);
                if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                    if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
                        return true;
                    } else {
                        Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                        context.startActivityForResult(roleRequestIntent,2);
                        return false;
                    }
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isAlreadyDefaultDialer(context)) {
                    return true;
                }
                Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
                intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, context.getPackageName());
                if (intent.resolveActivity(context.getPackageManager()) != null) {

                    context.startActivityForResult(intent,2);
                    return false;
                } else {

                }
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private static boolean isAlreadyDefaultDialer(Context context) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(TELECOM_SERVICE);
        return context.getPackageName().equals(telecomManager.getDefaultDialerPackage());
    }
}
