package com.lockscreenplayer.js.lockscreenplayer;

/**
 * Created by lenovo on 2017-04-11.
 */

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.tsengvn.typekit.Typekit;

import java.lang.reflect.Field;

public class FontApplication extends Application {  //메인 폰트변경
    @Override
    public void onCreate() {
        super.onCreate();

        setDefaultFont(this, "DEFAULT", "NanumBarunGothic.ttf");
        setDefaultFont(this, "SANS_SERIF", "NanumBarunGothic.ttf");
        setDefaultFont(this, "SERIF", "NanumBarunGothic.ttf");

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this,"NanumBarunGothic.ttf"))
                .addBold(Typekit.createFromAsset(this, "NanumBarunGothic.ttf"));

    }


    public static void setDefaultFont(Context ctx,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(),
                fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
