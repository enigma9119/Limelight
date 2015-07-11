package com.centerstage.limelight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by Smitesh on 7/8/2015.
 */
public class Utils {

    // Converts text to a bitmap drawable that can placed in an imageView
    public static BitmapDrawable textAsBitmapDrawable(Context context, String text, float textSize, int textColor, int imageWidth, int imageHeight) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setFakeBoldText(true);

        Bitmap image;
        if (imageWidth > 0 && imageHeight > 0) {
            image = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        } else {
            return null;
        }

        Canvas canvas = new Canvas(image);
        StaticLayout textLayout = new StaticLayout(text, paint, imageWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        canvas.save();
        canvas.translate(0, imageHeight/2.5f);
        textLayout.draw(canvas);
        canvas.restore();

        return new BitmapDrawable(context.getResources(), image);
    }
}
