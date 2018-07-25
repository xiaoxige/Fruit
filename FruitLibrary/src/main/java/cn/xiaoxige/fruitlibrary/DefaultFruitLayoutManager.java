package cn.xiaoxige.fruitlibrary;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author by zhuxiaoan on 2018/7/24 0024.
 */

public class DefaultFruitLayoutManager extends FruitView.LayoutManager {

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams(Context context) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return params;
    }

    private int x = 0;
    private int y = 0;

    @Override
    protected void layoutChildren(ViewGroup parent, View view) {
        view.layout(x, y, x + view.getMeasuredWidth(), y + view.getMeasuredHeight());
        x += view.getMeasuredWidth() + 1;
        y += view.getMeasuredHeight() + 1;
    }
}
