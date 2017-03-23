package com.lnikkila.oidc.minsdkcompat;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * @author Camilo Montes
 */
public class CompatTextView extends AppCompatTextView {
    public CompatTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createFont();
    }

    public CompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createFont();
    }

    public CompatTextView(Context context) {
        super(context);
        createFont();
    }

    public void createFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "Roboto-BoldCondensed.ttf");
        setTypeface(font);
    }
}
