package cn.xiaoxige.fruitlibrary;

import android.animation.Animator;
import android.content.Context;
import android.database.Observable;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author by zhuxiaoan on 2018/7/23 0023.
 *         Fruit layout
 */

public class FruitView extends ViewGroup {

    private Context mContext;


    public FruitView(Context context) {
        this(context, null);
    }

    public FruitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FruitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widhtMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((widhtMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }


    public void setAdapter(Adapter adapter) {
    }

    public void setLayoutManager(LayoutManager layoutManager) {
    }

    public void setFruitAnimation(FruitAnimator animation) {
    }


    /**
     * Caching and operation related
     */
    public final class Fruit extends AdapterDataObserver {
        private List<ViewHolder> mAttachedScrap = new ArrayList<>();
        private Map<Integer, List<ViewHolder>> mChangedScrap = new ArrayMap<>();
    }

    public interface FruitAnimator {
        /**
         * Go into animation
         *
         * @return
         */
        Animator enter(View view);

        /**
         * Exit animation
         *
         * @return
         */
        Animator quit(View view);

        /**
         * Hovering animation
         *
         * @return
         */
        Animator hover(View view);
    }

    /**
     * Animation and operation related
     */
    class Animation extends AdapterDataObserver {

    }

    public static class ViewHolder {

        public final View itemView;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

    }

    public static abstract class Adapter<VH extends ViewHolder> {

        private AdapterDataObservable mObservable = new AdapterDataObservable();

        public int getItemViewType(int position) {
            return 0;
        }

        public abstract int getItemCount();

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

    }

    static abstract class AdapterDataObserver {

    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {
    }

    /**
     * Children's position and style operation
     */
    public static abstract class LayoutManager {

        protected FruitView mFruitView;

        protected abstract LayoutParams generateDefaultLayoutParams(Context context);

        protected abstract void layoutChildren(ViewGroup parent, View view);

        public void setFruitView(FruitView fruitView) {
            this.mFruitView = fruitView;
        }
    }


}