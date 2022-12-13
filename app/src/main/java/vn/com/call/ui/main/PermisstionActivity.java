package vn.com.call.ui.main;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.role.RoleManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.phone.thephone.call.dialer.R;

import vn.com.call.db.SharePref;

public class PermisstionActivity extends AppCompatActivity {
  Button bt_click;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permisstion);
        bt_click = findViewById(R.id.bt_click);
        bt_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogButtonClicked(v);
            }
        });
        if (SharePref.check(getApplicationContext(),SharePref.PERMISSTION)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isAlreadyDefaultDialer() {
        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        return getPackageName().equals(telecomManager.getDefaultDialerPackage());
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkDefaultHandler() {
        if (isAlreadyDefaultDialer()) {
            return;
        }
        Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
        else{
            throw new RuntimeException("Default phone functionality not found");
        }
    }

    private void setDefaultCallAppApi30() {
        RoleManager roleManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            roleManager = getApplicationContext().getSystemService(RoleManager.class);
            if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER)) {
                if (roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
//                    Toast.makeText(getApplicationContext(), "PrismApp set as default.", Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
//                    startActivity(i);
                } else {
                    Intent roleRequestIntent = roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER);
                    startActivityForResult(roleRequestIntent, 2);
                }
            }
        }
    }

    public void showAlertDialogButtonClicked(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setTitle("Important");

        builder.setMessage("Set The Phone as default phone handler. This is necessary to display call screen and recent calls. Information will not pass to the third parties");
        // add a button
        builder.setPositiveButton("Set default", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    setDefaultCallAppApi30();
                }else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkDefaultHandler();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        Window view1=((AlertDialog)dialog).getWindow();
        view1.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
// to get rounded corners and border for dialog window
        view1.setBackgroundDrawableResource(R.drawable.background_message_white);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            SharePref.putKey(getApplicationContext(),SharePref.PERMISSTION,SharePref.PERMISSTION);
        }
    }
}