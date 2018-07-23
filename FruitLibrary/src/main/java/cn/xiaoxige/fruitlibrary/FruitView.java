package cn.xiaoxige.fruitlibrary;

import android.content.Context;
import android.database.Observable;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

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
    private Adapter mAdaper;

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
        mFruit.init();
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
                LayoutParams layoutParams = mLayoutManager.generateDefaultLayoutParams();
                setMeasuredDimension(layoutParams.width, layoutParams.height);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if (mAdaper == null || mLayoutManager == null) {
            return;
        }

        int itemCount = mAdaper.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            mLayoutManager.layoutChildren(null);
        }
    }


    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        this.mAdaper = adapter;
        this.mAdaper.registerAdapterDataObserver(mFruit);
        mFruit.resert();
        if (mAnimation != null) {
            this.mAdaper.registerAdapterDataObserver(mAnimation);
            this.mAnimation.resert();
        }
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

    public void setAnimation(Animation animation) {
        this.mAnimation = animation;
        if (mAdaper != null) {
            if (mAnimation != null) {
                this.mAnimation.init();
                this.mAdaper.registerAdapterDataObserver(this.mAnimation);
            }
        }
    }

    public Adapter getAdapter() {
        return mAdaper;
    }

    /**
     * Caching and operation related
     */
    static class Fruit extends AdapterDataObserver {

        private Map<Integer, List<ViewHolder>> mAttachedScrap = new ArrayMap<>();
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
            View view = null;

            return view;
        }


        @Override
        void onInvalidAllData() {
            super.onInvalidAllData();

        }

        @Override
        void onInvalidData(int position) {
            super.onInvalidData(position);

        }

        @Override
        void onInserted(int position) {
            super.onInserted(position);

        }

        @Override
        void onDelData(int position) {
            super.onDelData(position);

        }

    }

    /**
     * Animation and operation related
     */
    static class Animation extends AdapterDataObserver {

        public void init() {
        }

        public void resert() {
        }

        @Override
        void onInserted(int position) {
            super.onInserted(position);

        }

        @Override
        void onDelData(int position) {
            super.onDelData(position);

        }
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

    }

    /**
     * Children's position and style operation
     */
    public static abstract class LayoutManager {

        protected FruitView mFruitView;

        protected abstract LayoutParams generateDefaultLayoutParams();

        protected abstract void layoutChildren(View view);

        public void setFruitView(FruitView fruitView) {
            this.mFruitView = fruitView;
        }
    }


}