package vn.vmb.security;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by ngson on 20/09/2017.
 */

@RuntimePermissions
public class FingerprintFragment extends BaseSecurityFragment {
    public static final String KEY_NAME = "yourKey";

    public static FingerprintFragment newInstance(String key, Action action) {
        Bundle args = new Bundle();
        args.putString(AppLock.EXTRA_SESSION_ID, key);
        args.putInt(AppLock.EXTRA_ACTION, action.ordinal());

        FingerprintFragment fragment = new FingerprintFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private Setting mSetting;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSetting = getSettingForSessionById();

        keyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getContext().getSystemService(FINGERPRINT_SERVICE);
    }

    //    @BindView(R2.id.icon_fingerprint)
    private ImageView mIconFingerprint;
    //    @BindView(R2.id.label)
    private TextView mLabel;

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState, View view) {
        mIconFingerprint = view.findViewById(R.id.icon_fingerprint);
        mLabel = view.findViewById(R.id.label);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManager.isHardwareDetected()) {
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    mLabel.setText(R.string.alert_verify);

                    FingerprintFragmentPermissionsDispatcher.startAuthWithCheck(this);
                } else mLabel.setText(R.string.alert_no_fingerprint);
            } else mLabel.setText(R.string.alert_no_support);


        } else mLabel.setText(R.string.alert_no_support);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        FingerprintFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.USE_FINGERPRINT)
    @TargetApi(Build.VERSION_CODES.M)
    public void startAuth() {
        if (!keyguardManager.isKeyguardSecure()) {
            mLabel.setText(R.string.alert_need_keyguard_secure);
        } else {
            try {
                generateKey();
            } catch (FingerprintException e) {
                e.printStackTrace();
            }

            if (initCipher()) {
                cryptoObject = new FingerprintManager.CryptoObject(cipher);
                FingerprintHandler helper = new FingerprintHandler(getContext());
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }

    @OnShowRationale(Manifest.permission.USE_FINGERPRINT)
    @TargetApi(Build.VERSION_CODES.M)
    public void onShowRationale(final PermissionRequest request) {
        request.proceed();
    }

    @OnPermissionDenied(Manifest.permission.USE_FINGERPRINT)
    @TargetApi(Build.VERSION_CODES.M)
    public void onDenied() {
        mLabel.setText(R.string.alert_no_permission);
    }

    @OnNeverAskAgain(Manifest.permission.USE_FINGERPRINT)
    @TargetApi(Build.VERSION_CODES.M)
    public void onNeverAskAgain() {
        mLabel.setText(R.string.alert_no_permission);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_fingerprint;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }


    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
        private final String TAG = getClass().getSimpleName();

        private CancellationSignal cancellationSignal;
        private Context context;

        private Handler mHandler = new Handler();
        private Runnable mChangeIconFingerprint = new Runnable() {
            @Override
            public void run() {
                mIconFingerprint.setImageResource(R.drawable.ic_fingerprint_black_48dp);
            }
        };

        public FingerprintHandler(Context mContext) {
            context = mContext;
        }

        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Log.wtf(TAG, errorCode + " " + errString);
        }

        @Override
        public void onAuthenticationFailed() {
            mIconFingerprint.setImageResource(R.drawable.ic_fingerprint_red_800_48dp);
            mHandler.postDelayed(mChangeIconFingerprint, 1500);
            vibrate();

            if (getAction() == Action.CREATE_PASSWORD) {

            } else {
                notifyStepUnlock(false);
            }
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            Log.wtf(TAG, helpCode + " " + helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            mIconFingerprint.setImageResource(R.drawable.ic_fingerprint_blue_600_48dp);

            if (getAction() == Action.CREATE_PASSWORD) {
                mSetting.setTypeLock(TypeLock.FINGERPRINT);
                notifyStepCreatePassword(StepCreatePassword.CREATE_PASSWORD_SUCCESS);
            } else notifyStepUnlock(true);
        }

        private void vibrate() {
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(500);
        }
    }
}
