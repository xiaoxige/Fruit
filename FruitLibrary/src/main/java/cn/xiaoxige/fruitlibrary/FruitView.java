package cn.xiaoxige.fruitlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

    private Fruit mFruit;
    private Animation mAnimation;
    private LayoutManager mLayoutManager;
    private FruitAnimator mFruitAnimator;
    Adapter mAdaper;

    public FruitView(Context context) {
        this(context, null);
    }

    public FruitView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FruitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mFruit = new Fruit();
        mAnimation = new Animation();
        mFruit.init();
        mAnimation.init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widhtMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if ((widhtMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        } else {
            if (mLayoutManager == null) {
                setMeasuredDimension(0, 0);
            } else {
                LayoutParams layoutParams = mLayoutManager.generateDefaultLayoutParams(mContext);
                setMeasuredDimension(layoutParams.width, layoutParams.height);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (mAdaper == null || mLayoutManager == null) {
            return;
        }

        int itemCount = getChildCount();
        for (int i = 0; i < itemCount; i++) {
            View view = mFruit.findViewByPosition(i);
            if (view == null) {
                continue;
            }
            measureChild(view, 0, 0);
            mLayoutManager.layoutChildren(this, view);
        }
    }


    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        this.mAdaper = adapter;
        this.mAdaper.registerAdapterDataObserver(mAnimation);
        this.mAdaper.registerAdapterDataObserver(mFruit);
        mFruit.resert();
        this.mAnimation.resert();
        this.mAdaper.notifyDataSetChanged();
        requestLayout();
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager == null) {
            return;
        }
        this.mLayoutManager = layoutManager;
        this.mLayoutManager.setFruitView(this);
        requestLayout();
    }

    public void setFruitAnimation(FruitAnimator animation) {
        this.mFruitAnimator = animation;
    }

    public Adapter getAdapter() {
        return mAdaper;
    }

    public void setMaxCache(int maxCache) {
        mFruit.setMaxCache(maxCache);
    }

    /**
     * Caching and operation related
     */
    public final class Fruit extends AdapterDataObserver {

        private List<ViewHolder> mAttachedScrap = new ArrayList<>();
        private Map<Integer, List<ViewHolder>> mChangedScrap = new ArrayMap<>();

        /**
         * Maximum cache number
         */
        private int mMaxCache = 4;

        public void init() {
        }

        public void resert() {
            mAttachedScrap.clear();
            mChangedScrap.clear();
        }

        public View findViewByPosition(int position) {
            if (mAttachedScrap.size() <= 0) {
                return null;
            }
            ViewHolder viewHolder = mAttachedScrap.get(position);
            return viewHolder.itemView;
        }

        public void setMaxCache(int maxcache) {
            this.mMaxCache = maxcache;
        }

        @Override
        void onInvalidAllData() {
            super.onInvalidAllData();
            int size = mAttachedScrap.size();
            int itemCount = mAdaper.getItemCount();
            for (int i = 0; i < size && i < itemCount; i++) {
                ViewHolder viewHolder = mAttachedScrap.get(i);
                //noinspection unchecked
                mAdaper.onBindViewHolder(viewHolder, i);
            }

            for (int i = size; i < itemCount; i++) {
                onInserted(i);
            }

        }

        @Override
        void onInvalidData(int position) {
            super.onInvalidData(position);
            //noinspection unchecked
            mAdaper.onBindViewHolder(mAttachedScrap.get(position), position);
        }

        @Override
        void onInserted(int position) {
            super.onInserted(position);
            int itemCount = mAdaper.getItemCount();
            position = Math.min(itemCount, Math.max(position, 0));
            int itemViewType = mAdaper.getItemViewType(position);
            List<ViewHolder> viewHolders = mChangedScrap.get(itemViewType);
            ViewHolder viewHolder = null;
            if (viewHolders != null && !viewHolders.isEmpty()) {
                viewHolder = viewHolders.remove(0);
            }
            if (viewHolder == null) {
                viewHolder = mAdaper.onCreateViewHolder(FruitView.this, itemViewType);
                viewHolder.itemType = itemViewType;
            }

            mAttachedScrap.add(position, viewHolder);
            //noinspection unchecked
            mAdaper.onBindViewHolder(viewHolder, position);
        }

        @Override
        void onDelData(int position) {
            super.onDelData(position);
            ViewHolder holder = mAttachedScrap.remove(position);
            int itemType = holder.itemType;
            List<ViewHolder> viewHolders = mChangedScrap.get(itemType);
            if (viewHolders == null) {
                viewHolders = new ArrayList<>();
            }
            if (viewHolders.size() > mMaxCache) {
                holder.reset();
                viewHolders.add(holder);
            }
        }

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

        public void init() {
        }

        public void resert() {
        }

        @Override
        void onInvalidAllData() {
            super.onInvalidAllData();
            int itemCount = mAdaper.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                onInserted(i);
            }
        }

        @Override
        void onInserted(int position) {
            super.onInserted(position);
            View view = mFruit.findViewByPosition(position);
            if (view == null) {
                return;
            }
            addView(view);

            if (mFruitAnimator != null) {
                Animator enter = mFruitAnimator.enter(view);
                if (enter != null) {
                    enter.start();
                }

                Animator hover = mFruitAnimator.hover(view);
                if (hover != null) {
                    hover.start();
                }
            }
        }

        @Override
        void onDelData(int position) {
            super.onDelData(position);
            final View view = mFruit.findViewByPosition(position);
            if (view == null) {
                return;
            }
            if (mFruitAnimator != null) {
                Animator quit = mFruitAnimator.quit(view);
                if (quit != null) {
                    quit.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeView(view);
                        }
                    });
                } else {
                    removeView(view);
                }
            }
        }
    }

    public static class ViewHolder {

        public final View itemView;
        private int itemType;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
        }

        public void reset() {
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

        public final void notifyDataSetChanged() {
            mObservable.notifyAllChanged();
        }

        public final void notifyItemChanged(int position) {
            mObservable.notifyChange(position);
        }

        public final void notifyItemInserted(int position) {
            mObservable.onInserted(position);
        }

        public final void notifyItemRemoved(int position) {
            mObservable.itemRemoved(position);
        }

        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.registerObserver(observer);
        }

        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            mObservable.unregisterObserver(observer);
        }

    }

    static abstract class AdapterDataObserver {

        void onInvalidAllData() {
            // Do nothing
        }

        void onInvalidData(int position) {
            // Do nothing
        }

        void onInserted(int position) {
            // Do nothing
        }

        void onDelData(int position) {
            // Do nothing
        }

        void onAttachedToWindow(int position) {
            // Do nothing
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {

        public final void notifyAllChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInvalidAllData();
            }
        }

        public final void notifyChange(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInvalidData(position);
            }
        }

        public final void onInserted(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onInserted(position);
            }
        }

        public final void itemRemoved(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onDelData(position);
            }
        }

        public final void onAttachedToWindow(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onAttachedToWindow(position);
            }
        }
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