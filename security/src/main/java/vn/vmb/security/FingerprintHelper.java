package vn.vmb.security;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by ngson on 30/09/2017.
 */

public class FingerprintHelper {
    public static boolean isDeviceSupportFingerprint(Context context) {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

        return Build.VERSION.SDK_INT >= 23 && fingerprintManager.isHardwareDetected();
    }
}
