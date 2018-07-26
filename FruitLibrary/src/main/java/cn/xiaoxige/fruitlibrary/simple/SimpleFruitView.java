package cn.xiaoxige.fruitlibrary.simple;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 */

public class SimpleFruitView extends ViewGroup {

    private Context mContext;

    private int mWidth;
    private int mHeight;

    public SimpleFruitView(Context context) {
        this(context, null);
    }

    public SimpleFruitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleFruitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        throw new RuntimeException("The size of the fruit must be clear (the location of the child is random, and the size of the fruit can not be determined after the child).");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
