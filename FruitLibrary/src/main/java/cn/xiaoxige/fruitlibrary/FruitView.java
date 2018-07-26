package cn.xiaoxige.fruitlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.database.Observable;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author by zhuxiaoan on 2018/7/23 0023.
 *         Fruit layout
 */

public class FruitView extends ViewGroup {

    /**
     * Maximum disjoint search times
     */
    private static final int TRY_MAX_FIND_DISJOINT_FREQUENCY = 500;

    private Context mContext;

    private Fruit mFruit;
    private Animation mAnimation;
    private LayoutManager mLayoutManager;
    private FruitAnimator mFruitAnimator;

    private int mFruitViewWidth;
    private int mFruitViewHeight;

    Adapter mAdapter;

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
            mFruitViewWidth = MeasureSpec.getSize(widthMeasureSpec);
            mFruitViewHeight = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        throw new RuntimeException("The size of the fruit must be clear (the location of the child is random, and the size of the fruit can not be determined after the child).");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mAdapter == null || mLayoutManager == null) {
            return;
        }

        int itemCount = mAdapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            ViewHolder viewHolder = mFruit.findViewHolderByPosition(i);
            if (viewHolder == null || !viewHolder.isEffective) {
                continue;
            }
            View itemView = viewHolder.itemView;
            if (itemView == null) {
                continue;
            }
            if (!viewHolder.isAreadyMeasure) {
                measureChild(itemView, 0, 0);
                viewHolder.isAreadyMeasure ^= true;
            }
            mLayoutManager.layoutChildren(this, mFruit.findViewHolderByPosition(i));
        }

    }


    public void setAdapter(Adapter adapter) {
        if (adapter == null) {
            return;
        }
        mAdapter = adapter;
        mAdapter.registerAdapterDataObserver(new DataObservable());

        post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setLayoutManager(LayoutManager layoutManager) {
        if (layoutManager == null) {
            return;
        }
        this.mLayoutManager = layoutManager;
        this.mLayoutManager.setFruitView(this);
    }

    public void setFruitAnimation(FruitAnimator animation) {
        if (animation == null) {
            return;
        }
        this.mFruitAnimator = animation;
    }


    /**
     * Caching and operation related
     */
    public final class Fruit {
        private List<ViewHolder> mAttachedScrap = new ArrayList<>();
        private Map<Integer, List<ViewHolder>> mCacheScrap = new ArrayMap<>();
        private int mMaxCachce = 5;

        void init() {
        }

        ViewHolder findViewHolderByPosition(int position) {
            position = Math.max(0, Math.min(position, mAttachedScrap.size() - 1));
            if (mAttachedScrap.isEmpty()) {
                return null;
            }
            return mAttachedScrap.get(position);
        }

        void onInvalidAllData() {

            int size = mAttachedScrap.size();
            for (int i = size - 1; i >= 0; i--) {
                onDelData(i, false);
            }
            removeAllViews();

            mAttachedScrap.clear();
            int itemCount = mAdapter.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                insertItem(i);
            }
        }

        void onInvalidData(int position) {
            ViewHolder viewHolder = mAttachedScrap.get(position);
            viewHolder.isAreadyMeasure = false;
            //noinspection unchecked
            mAdapter.onBindViewHolder(viewHolder, position);
        }

        void insertItem(int position) {
            int itemCount = mAdapter.getItemCount();
            position = Math.min(itemCount, Math.max(position, 0));
            int itemViewType = mAdapter.getItemViewType(position);

            List<ViewHolder> viewHolders = mCacheScrap.get(itemViewType);
            ViewHolder viewHolder = null;
            if (viewHolders != null && !viewHolders.isEmpty()) {
                viewHolder = viewHolders.remove(0);
            }
            if (viewHolder == null) {
                viewHolder = mAdapter.onCreateViewHolder(FruitView.this, itemViewType);
                viewHolder.itemViewType = itemViewType;
            }
            viewHolder.isEffective = true;

            int itemViewPattern = viewHolder.itemViewPattern;

            if (itemViewPattern == ViewHolder.POSITION_PATTERN_AUTO) {
                viewHolder.position = autoDistributionPosition(position, viewHolder);
            } else {
                viewHolder.position = appointDistributionPosition(position, viewHolder);
            }

            mAttachedScrap.add(position, viewHolder);
            //noinspection unchecked
            mAdapter.onBindViewHolder(viewHolder, position);
        }

        void onDelData(int position, boolean isNeedReBindData) {
//            ViewHolder viewHolder = mAttachedScrap.get(position);
//            if (!viewHolder.isEffective) {
//                viewHolder.itemView.setOnClickListener(null);
//                return;
//            }

            ViewHolder holder = mAttachedScrap.remove(position);
            int itemType = holder.itemViewType;

            List<ViewHolder> viewHolders = mCacheScrap.get(itemType);
            if (viewHolders == null) {
                viewHolders = new ArrayList<>();
                mCacheScrap.put(itemType, viewHolders);
            }
            if (viewHolders.size() <= mMaxCachce) {
                viewHolders.add(holder);
                holder.reset();
            }

            if (!isNeedReBindData) {
                return;
            }
            int size = mAttachedScrap.size();
            position = Math.max(0, position - 1);
            for (int i = position; i < size; i++) {
                //noinspection unchecked
                mAdapter.onBindViewHolder(mAttachedScrap.get(i), i);
            }

        }

        private Point autoDistributionPosition(int position, ViewHolder viewHolder) {
            Point point;
            point = tryDisjointByUser(position, viewHolder);

            if (point == null) {
                View itemView = viewHolder.itemView;
                if (!viewHolder.isAreadyMeasure) {
                    measureChild(itemView, 0, 0);
                }

                int effectiveWidth = mFruitViewWidth - itemView.getMeasuredWidth() - itemView.getPaddingLeft() - itemView.getPaddingRight();
                int effectiveHeight = mFruitViewHeight - itemView.getMeasuredHeight() - itemView.getPaddingTop() - itemView.getPaddingBottom();

                point = tryCreateDisjointPosint(itemView, effectiveWidth, effectiveHeight);
            }


            return point;
        }

        private Point appointDistributionPosition(int position, ViewHolder viewHolder) {
            Point point;
            point = tryDisjointByUser(position, viewHolder);

            if (point == null) {

                View itemView = viewHolder.itemView;
                if (!viewHolder.isAreadyMeasure) {
                    measureChild(itemView, 0, 0);
                }
                int effectiveWidth = mFruitViewWidth - itemView.getMeasuredWidth() - itemView.getPaddingLeft() - itemView.getPaddingRight();
                int effectiveHeight = mFruitViewHeight - itemView.getMeasuredHeight() - itemView.getPaddingTop() - itemView.getPaddingBottom();
                int itemViewPattern = viewHolder.itemViewPattern;

                if (itemViewPattern == ViewHolder.POSITION_PATTERN_CENTER) {
                    point = new Point((effectiveWidth + itemView.getMeasuredWidth()) / 2 - itemView.getMeasuredWidth() / 2, (effectiveHeight + itemView.getMeasuredHeight()) / 2 - itemView.getMeasuredHeight() / 2);
                } else if (itemViewPattern == ViewHolder.POSITION_PATTERN_LEFT_TOP) {
                    point = new Point(10, 10);
                } else if (itemViewPattern == ViewHolder.POSITION_PATTERN_LEFT_BOTTOM) {
                    point = new Point(10, effectiveHeight - itemView.getMeasuredHeight() - 10);
                } else if (itemViewPattern == ViewHolder.POSITION_PATTERN_RIGHT_TOP) {
                    point = new Point(effectiveWidth - itemView.getMeasuredWidth() - 10, 10);
                } else if (itemViewPattern == ViewHolder.POSITION_PATTERN_RIGHT_BOTTOM) {
                    point = new Point(effectiveWidth - itemView.getMeasuredWidth() - 10, effectiveHeight - itemView.getMeasuredHeight() - 10);
                } else {
                    point = autoDistributionPosition(position, viewHolder);
                }
            }
            return point;
        }

        private Point tryDisjointByUser(int position, ViewHolder viewHolder) {
            Point point = null;
            if (mLayoutManager != null) {
                point = mLayoutManager.getChildrenPosition(position, viewHolder);
            }
            return point;
        }

        private Point tryCreateDisjointPosint(View itemView, int effectiveWidth, int effectiveHeight) {

            Point point = null;
            // Try to find five times
            for (int i = 0; i < TRY_MAX_FIND_DISJOINT_FREQUENCY; i++) {
                int x = new Random().nextInt(effectiveWidth);
                int y = new Random().nextInt(effectiveHeight);
                Rect itemViewRect = new Rect(x, y, x + itemView.getMeasuredWidth(), y + itemView.getMeasuredHeight());
                if (isDisjoint(itemViewRect)) {
                    point = new Point(x, y);
                    break;
                }
            }

            // Standard of reduction
            if (point == null) {
                for (int i = 0; i < TRY_MAX_FIND_DISJOINT_FREQUENCY; i++) {
                    int x = new Random().nextInt(effectiveWidth);
                    int y = new Random().nextInt(effectiveHeight);
                    if (isReduceTheCoincidence(x, y)) {
                        point = new Point(x, y);
                        break;
                    }
                }
            }

            // forehead
            if (point == null) {
                int x = new Random().nextInt(effectiveWidth);
                int y = new Random().nextInt(effectiveHeight);
                point = new Point(x, y);
            }

            return point;
        }

        private boolean isDisjoint(Rect rect) {
            int size = mAttachedScrap.size();
            if (size <= 0) {
                return true;
            }
            Rect childRect = new Rect();
            for (int i = 0; i < size; i++) {
                ViewHolder viewHolder = mAttachedScrap.get(i);
                View itemView = viewHolder.itemView;

                childRect.set(viewHolder.position.x, viewHolder.position.y,
                        viewHolder.position.x + itemView.getMeasuredWidth(),
                        viewHolder.position.y + itemView.getMeasuredHeight());

                PointF c1 = new PointF(rect.left + rect.width() / 2.0f, rect.top + rect.height() / 2.0f);
                PointF c2 = new PointF(childRect.left + childRect.width() / 2.0f, childRect.top + childRect.height() / 2.0f);
                if ((Math.abs(c1.x - c2.x) <= rect.width() / 2.0 + childRect.width() / 2.0 && Math.abs(c2.y - c1.y) <= rect.height() / 2.0 + childRect.height() / 2.0)) {
                    return false;
                }

            }
            return true;
        }

        private boolean isReduceTheCoincidence(int x, int y) {
            int size = mAttachedScrap.size();
            if (size <= 0) {
                return true;
            }
            for (int i = 0; i < size; i++) {
                ViewHolder viewHolder = mAttachedScrap.get(i);
                if (x == viewHolder.position.x && y == viewHolder.position.y) {
                    return false;
                }
            }
            return true;
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
    class Animation {

        void init() {
        }

        void onInvalidAllData() {

            int itemCount = mAdapter.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                onInserted(i);
            }
        }

        void onInserted(int position) {
            ViewHolder viewHolder = mFruit.findViewHolderByPosition(position);
            if (!viewHolder.isEffective) {
                return;
            }

            View itemView = viewHolder.itemView;
            if (itemView == null) {
                return;
            }

            addView(itemView, position);

            if (mFruitAnimator != null) {
                Animator enter = mFruitAnimator.enter(itemView);
                if (enter != null) {
                    enter.start();
                }

                Animator hover = mFruitAnimator.hover(itemView);
                if (hover != null) {
                    hover.start();
                }
            }
        }

        void onDelData(int position) {
            ViewHolder viewHolder = mFruit.findViewHolderByPosition(position);
//            if (!viewHolder.isEffective) {
//                return;
//            }

            viewHolder.isEffective = false;

            final View itemView = viewHolder.itemView;
            if (itemView == null) {
                return;
            }


            if (mFruitAnimator != null) {
                Animator quit = mFruitAnimator.quit(itemView);
                if (quit != null) {
                    quit.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            removeView(itemView);
                        }
                    });
                    quit.start();
                } else {
                    removeView(itemView);
                }
            }
        }
    }

    public static class ViewHolder {

        static final int POSITION_PATTERN_AUTO = 1;
        public static final int POSITION_PATTERN_CENTER = POSITION_PATTERN_AUTO << 1;
        public static final int POSITION_PATTERN_LEFT_TOP = POSITION_PATTERN_AUTO << 2;
        public static final int POSITION_PATTERN_LEFT_BOTTOM = POSITION_PATTERN_AUTO << 3;
        public static final int POSITION_PATTERN_RIGHT_TOP = POSITION_PATTERN_AUTO << 4;
        public static final int POSITION_PATTERN_RIGHT_BOTTOM = POSITION_PATTERN_AUTO << 5;


        public final View itemView;
        private int itemViewType;
        private int itemViewPattern;
        private boolean isEffective;
        private boolean isAreadyMeasure;
        Point position;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            this.isEffective = true;
            this.isAreadyMeasure = false;
            itemViewPattern = positionPattern();
        }

        public void reset() {
            this.isAreadyMeasure = false;
            this.isEffective = false;
            if (isCanAutoResetPositionPattern()) {
                itemViewPattern = POSITION_PATTERN_AUTO;
            }
        }

        protected boolean isCanAutoResetPositionPattern() {
            return true;
        }

        protected int positionPattern() {
            return POSITION_PATTERN_AUTO;
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

    }

    static abstract class AdapterDataObserver {

        void onAllChanged() {
            // Do Nothing
        }

        void onItemChanged(int position) {
            // Do Nothing
        }

        void onItemInsert(int position) {
            // Do Nothing
        }

        void onItemRemoved(int position) {
            // Do Nothing
        }
    }

    static class AdapterDataObservable extends Observable<AdapterDataObserver> {

        void notifyAllChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onAllChanged();
            }
        }

        void notifyChange(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemChanged(position);
            }
        }

        void onInserted(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemInsert(position);
            }
        }

        void itemRemoved(int position) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRemoved(position);
            }
        }
    }

    /**
     * Children's position and style operation
     */
    public static abstract class LayoutManager {

        FruitView mFruitView;

        protected abstract void layoutChildren(ViewGroup parent, ViewHolder viewHolder);

        Point getChildrenPosition(int position, ViewHolder viewHolder) {
            return null;
        }

        void setFruitView(FruitView fruitView) {
            this.mFruitView = fruitView;
        }
    }

    class DataObservable extends AdapterDataObserver {

        @Override
        void onAllChanged() {
            mFruit.onInvalidAllData();
            mAnimation.onInvalidAllData();
        }

        @Override
        void onItemChanged(int position) {
            mFruit.onInvalidData(position);
        }

        @Override
        void onItemInsert(int position) {
            mFruit.insertItem(position);
            mAnimation.onInserted(position);
        }

        @Override
        void onItemRemoved(int position) {
            mAnimation.onDelData(position);
            mFruit.onDelData(position, true);
        }
    }

}