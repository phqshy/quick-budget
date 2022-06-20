package me.fishy.testapp.app.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import me.fishy.testapp.R;

public class BoxBackgroundView extends View {
    private int mExampleColor = 0xFF0000;
    private RectF rectangle;
    private Paint rectColor;



    public BoxBackgroundView(Context context) {
        super(context);
        init(null, 0);
    }

    public BoxBackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public BoxBackgroundView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.BoxBackgroundView, defStyle, 0);

        mExampleColor = a.getColor(
                R.styleable.BoxBackgroundView_color,
                mExampleColor);

        rectColor = new Paint();
        rectColor.setColor(mExampleColor);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (rectangle == null){
            rectangle = new RectF(0, 0, getWidth(), getHeight());
        }
        canvas.drawRoundRect(rectangle, 20.f, 20.f, rectColor);
    }

    public int getExampleColor() {
        return mExampleColor;
    }

    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
    }

}