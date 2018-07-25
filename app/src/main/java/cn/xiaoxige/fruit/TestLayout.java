package cn.xiaoxige.fruit;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author by zhuxiaoan on 2018/7/25 0025.
 */

public class TestLayout extends ViewGroup {

    public TestLayout(Context context) {
        this(context, null);
    }

    public TestLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundColor(Color.YELLOW);
        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(context);
            textView.setText("测试TextView index = " + i);
            addView(textView);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view =
                    getChildAt(i);
            measureChild(view, 0, 0);
            height += view.getMeasuredHeight();
        }
        setMeasuredDimension(widthMeasureSpec, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int top = 0;
        for (int i = 0; i < childCount; i++) {
            View view =
                    getChildAt(i);
            view.layout(0, top, view.getMeasuredWidth(), top + view.getMeasuredHeight());

            top += view.getMeasuredHeight();
        }

    }


    public void removeTestView(View view) {
        removeView(view);
    }
}
