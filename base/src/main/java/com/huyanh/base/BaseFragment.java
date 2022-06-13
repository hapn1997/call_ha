package com.huyanh.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.huyanh.base.custominterface.PopupListener;
import com.huyanh.base.utils.BaseConstant;
import com.huyanh.base.utils.Log;

public class BaseFragment extends Fragment implements PopupListener {
    public BaseApplication baseApplication;
    public BaseFragment baseFragment;
    public Handler handler = new Handler();
    private Runnable runnableAddPopupListener = new Runnable() {
        @Override
        public void run() {
            if (baseApplication.getPopup() != null) {
                baseApplication.getPopup().addPopupListtener(baseFragment);
                return;
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApplication = (BaseApplication) getActivity().getApplication();
        baseFragment = this;
    }

    @Override
    public void onDoneQuerryInappBilling() {
    }

    @Override
    public void onClose(Object object) {
        Log.d("onClosePopup: " + object);
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.post(runnableAddPopupListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (baseApplication.getPopup() != null)
            baseApplication.getPopup().removePopupListener(this);
    }

    public boolean showPopup(Object object, boolean withOutCondition) {
        if (baseApplication.getPopup() != null)
            return baseApplication.getPopup().showPopup(this, object, withOutCondition);
        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BaseConstant.REQUEST_SHOW_POPUP_CUSTOM) {
            onClose(baseApplication.getPopup().getTempObject());
        }
    }
}
