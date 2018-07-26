package cn.xiaoxige.fruitlibrary.complex;

import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author by zhuxiaoan on 2018/7/24 0024.
 */

public class DefaultFruitLayoutManager extends FruitView.LayoutManager {

    @Override
    protected void layoutChildren(ViewGroup parent, FruitView.ViewHolder viewHolder) {
        View view = viewHolder.itemView;
        Point position = viewHolder.position;
        if (view == null || position == null) {
            return;
        }
        view.layout(position.x, position.y, position.x + view.getMeasuredWidth(), position.y + view.getMeasuredHeight());
    }
}
