package com.huyanh.base.dao;


import android.content.Context;
import android.widget.TextView;

public class BaseTypeface {
    private android.graphics.Typeface Black, BlackItalic;
    private android.graphics.Typeface Bold, BoldItalic;
    private android.graphics.Typeface Italic;
    private android.graphics.Typeface Light, LightItalic;
    private android.graphics.Typeface Medium, MediumItalic;
    private android.graphics.Typeface Regular;
    private android.graphics.Typeface Thin, ThinItalic;

    private Context context;

    public BaseTypeface(Context context) {
        this.context = context;
    }

    public void setTypefaces(TextView... tvs) {
        for (TextView tv : tvs) {
            tv.setTypeface(getRegular());
        }
    }

    public android.graphics.Typeface getBlack() {
        if (Black == null)
            Black = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Black.ttf");
        return Black;
    }

    public android.graphics.Typeface getBlackItalic() {
        if (BlackItalic == null)
            BlackItalic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BlackItalic.ttf");
        return BlackItalic;
    }

    public android.graphics.Typeface getBold() {
        if (Bold == null)
            Bold = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
        return Bold;
    }

    public android.graphics.Typeface getBoldItalic() {
        if (BoldItalic == null)
            BoldItalic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldItalic.ttf");
        return BoldItalic;
    }

    public android.graphics.Typeface getItalic() {
        if (Italic == null)
            Italic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Italic.ttf");
        return Italic;
    }

    public android.graphics.Typeface getLight() {
        if (Light == null)
            Light = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
        return Light;
    }

    public android.graphics.Typeface getLightItalic() {
        if (LightItalic == null)
            LightItalic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-LightItalic.ttf");
        return LightItalic;
    }

    public android.graphics.Typeface getMedium() {
        if (Medium == null)
            Medium = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
        return Medium;

    }

    public android.graphics.Typeface getMediumItalic() {
        if (MediumItalic == null)
            MediumItalic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-MediumItalic.ttf");
        return MediumItalic;
    }

    public android.graphics.Typeface getRegular() {
        if (Regular == null)
            Regular = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        return Regular;
    }

    public android.graphics.Typeface getThin() {
        if (Thin == null)
            Thin = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
        return Thin;
    }

    public android.graphics.Typeface getThinItalic() {
        if (ThinItalic == null)
            ThinItalic = android.graphics.Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-ThinItalic");
        return ThinItalic;
    }
}
