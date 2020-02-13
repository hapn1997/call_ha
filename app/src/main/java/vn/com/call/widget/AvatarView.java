package vn.com.call.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.com.call.R;
import vn.com.call.db.cache.ColorCache;
/**
 * Created by ngson on 03/07/2017.
 */

public class AvatarView extends CircleImageView {
    public AvatarView(Context context) {
        super(context);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void loadAvatar(String uri, String name, String number) {
        try {
            if (uri == null && name == null) {
                setImageBitmap(drawPlacaholderToBitmap(number, 100, 100, isGroup(number)));
            } else if (uri == null) setImageBitmap(drawTextToBitmap(number, name.substring(0, 1).toUpperCase(), 100, 100));
            else {

                Glide.with(getContext())
                        .load(Uri.parse(uri))
                        .crossFade()
                        .centerCrop()
                        .into(this);
            }
        } catch (Exception e) {
            setImageBitmap(drawPlacaholderToBitmap(number, 100, 100, isGroup(number)));
        }
    }

    public void loadAvatarBitmap(Bitmap photo, String name, String number) {
        try {

            if (photo == null && name == null) {
                setImageBitmap(drawPlacaholderToBitmap(number, 100, 100, isGroup(number)));
            } else if (photo == null) setImageBitmap(drawTextToBitmap(number, name.substring(0, 1).toUpperCase(), 100, 100));
            else {
                setImageBitmap(photo);
            }
        } catch (Exception e) {
            setImageBitmap(drawPlacaholderToBitmap(number, 100, 100, isGroup(number)));
        }
    }

    private Bitmap drawTextToBitmap(String number, String gText, int w, int h) {

        if (gText == null)
            gText = "T";

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        // set default bitmap config if none
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ColorCache.getColorFromNumber(number, getRandomDarkColor()));
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        paint.setTextSize(w / 2);
        paint.setTextAlign(Paint.Align.CENTER);


        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth()) / 2;
        int y = (int) ((bitmap.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(gText.toUpperCase(), x, y, paint);

        return bitmap;
    }

    private Bitmap drawPlacaholderToBitmap(String number, int w, int h, boolean isGroup) {
        Resources resources = getResources();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(ColorCache.getColorFromNumber(number, getRandomDarkColor()));

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap placeholder = BitmapFactory.decodeResource(resources, isGroup ? R.drawable.ic_people_white_18dp : R.drawable.ic_person_white_18dp);
        int wPlaceholder = placeholder.getWidth();
        int hPlaceholder = placeholder.getHeight();
        canvas.drawBitmap(placeholder, (w - wPlaceholder) / 2, (h - hPlaceholder) / 2, paint);

        return bitmap;
    }

    private boolean isGroup(String numbers) {
        Log.d("Dfdffff",numbers);

        return numbers.contains(",");
    }

    public  int getRandomDarkColor() {

        // Random r = new Random();

        return getContext().getResources().getColor(R.color.avatar);
        //  return Color.rgb(r.nextInt(100), r.nextInt(100), r.nextInt(100));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
