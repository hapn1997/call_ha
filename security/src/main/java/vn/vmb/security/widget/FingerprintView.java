package vn.vmb.security.widget;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import vn.vmb.security.Action;
import vn.vmb.security.FingerprintFragment;
import vn.vmb.security.R;
import vn.vmb.security.Setting;
import vn.vmb.security.StepCreatePassword;
import vn.vmb.security.TypeLock;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by ngson on 07/11/2017.
 */

public class FingerprintView extends BaseSecurityView {
    private final String TAG = getClass().getSimpleName();

    private ImageView mIconFingerprint;
    private TextView mLabel;

    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    private Setting mSetting;

    public FingerprintView(Context context, String sessionId, Action action) {
        super(context, sessionId, action);

        Log.wtf(TAG, "FingerprintView");
    }

    @Override
    public void initViews() {
        Toast.makeText(getContext(), "initViews", Toast.LENGTH_SHORT).show();
        mSetting = getSettingForSessionById();
        keyguardManager = (KeyguardManager) getContext().getSystemService(Context.KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getContext().getSystemService(FINGERPRINT_SERVICE);

        inflate(getContext(), R.layout.fragment_fingerprint, this);

        mIconFingerprint = findViewById(R.id.icon_fingerprint);
        mLabel = findViewById(R.id.label);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (fingerprintManager.isHardwareDetected()) {
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    mLabel.setText(R.string.alert_verify);

                    startAuth();
                } else mLabel.setText(R.string.alert_no_fingerprint);
            } else mLabel.setText(R.string.alert_no_support);


        } else mLabel.setText(R.string.alert_no_support);


    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startAuth() {
        Toast.makeText(getContext(), "startAuth", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getContext(), "after initCipher", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(FingerprintFragment.KEY_NAME,
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

        Toast.makeText(getContext(), "generateKey", Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean initCipher() {
        Toast.makeText(getContext(), "initCipher", Toast.LENGTH_SHORT).show();
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
            SecretKey key = (SecretKey) keyStore.getKey(FingerprintFragment.KEY_NAME,
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
            Toast.makeText(getContext(), errorCode + " " + errString, Toast.LENGTH_SHORT).show();
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

            Toast.makeText(getContext(), "onAuthenticationFailed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            Log.wtf(TAG, helpCode + " " + helpString);
            Toast.makeText(getContext(), helpCode + " " + helpString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Toast.makeText(getContext(), "onAuthenticationSucceeded", Toast.LENGTH_SHORT).show();
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
