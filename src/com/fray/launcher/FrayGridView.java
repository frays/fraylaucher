package com.fray.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.GridView;

/**
 * Created by ray on 03.08.2014.
 */
public class FrayGridView extends GridView {
    public FrayGridView(Context context) {
        super(context);
    }

    public FrayGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FrayGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
