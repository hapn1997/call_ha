package vn.com.call.ui.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import vn.com.call.R;
import vn.com.call.utils.PermissionUtils;
public class IntroSplashActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!PermissionUtils.isAllPermissionGrantedAtIntro(this)) {
//            addSlide(BaseIntroSlideFragment.newInstance(R.drawable.intro_1));
//            addSlide(BaseIntroSlideFragment.newInstance(R.drawable.intro_2));
//            addSlide(BaseIntroSlideFragment.newInstance(R.drawable.intro_3));
//            addSlide(BaseIntroSlideFragment.newInstance(R.drawable.intro_4));

            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.setStatusBarColor(Color.parseColor("#39c0c5"));
                }
            } catch (Exception e) {
            }
        } else {
            nextIntroActivity();
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        nextIntroActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        nextIntroActivity();
    }

    private void nextIntroActivity() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

}