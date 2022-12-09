package vn.com.call.editCall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ComponentActivity;

import java.util.ArrayList;

public class PhoneAccountChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (checkPermission("android.permission.CALL_PHONE", Process.myUid(), Process.myPid()) == PackageManager.PERMISSION_GRANTED
//                && checkPermission("android.permission.READ_PHONE_STATE", Process.myUid(), Process.myPid()) == PackageManager.PERMISSION_GRANTED) {
            callPhone();
//        }
    }

    public void callPhone() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        PhoneAccountHandle defaultOutgoingPhoneAccount = TelecomUtils.getDefaultOutgoingPhoneAccount(this, telecomManager, "tel");
        if (defaultOutgoingPhoneAccount == null) {
            ArrayList arrayList = new ArrayList();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                for (PhoneAccountHandle phoneAccountHandle : telecomManager.getCallCapablePhoneAccounts()) {
//                    if (phoneAccountHandle != null) {
//                        PhoneAccount phoneAccount = telecomManager.getPhoneAccount(phoneAccountHandle);
//                        arrayList.add(phoneAccount);
//                    }
//                }
//            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!defaultOutgoingPhoneAccount.getId().equals("E")){
                Uri fromParts = Uri.fromParts("tel", getIntent().getStringExtra("contactNumber"), null);
                Bundle bundle = new Bundle();
                bundle.putParcelable("android.telecom.extra.PHONE_ACCOUNT_HANDLE", defaultOutgoingPhoneAccount);
                telecomManager.placeCall(fromParts, bundle);
                finish();

//            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
