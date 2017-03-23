package com.lnikkila.oidc.minsdkcompat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 *
 * @author Camilo Montes
 */
public class CompatEditText extends AppCompatEditText {

    public CompatEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public CompatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public CompatEditText(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Roboto-BoldCondensed.ttf");
        setTypeface(font);
    }
}
