package cn.xiaoxige.fruitlibrary.simple;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Random;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 *         Simple implementation (without any caching)
 */

public abstract class SimpleFruitView<T> extends ViewGroup {

    /**
     * Maximum disjoint search times
     */
    private static final int TRY_MAX_FIND_DISJOINT_FREQUENCY = 500;

    private static final int NUM_POLL_TIME = 50;

    private static final float EACH_STEP = (float) (Math.PI / 180);

    protected Context mContext;

    private int mWidth;
    private int mHeight;

    private OnItemClickListener<T> mListener;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handlerFrame();
            sendEmptyMessageDelayed(0, NUM_POLL_TIME);
        }
    };

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
            return;
        }

        throw new RuntimeException("The size of the fruit must be clear (the location of the child is random, and the size of the fruit can not be determined after the child).");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            SimpleViewHolder viewHolder = (SimpleViewHolder) child.getTag();
            if (viewHolder.isNeedLayout) {
                Point position = viewHolder.position;
                measureChild(child, 0, 0);
                child.layout(position.x, position.y, position.x + child.getMeasuredWidth(), position.y + child.getMeasuredHeight());
                viewHolder.isNeedLayout ^= true;
            }
        }
    }

    public void add(List<T> datas, boolean isAppend) {
        if (!isAppend) {
            removeAllViews();
        }
        SimpleViewHolder<T> viewHolder;
        View view;
        int index = getChildCount();
        for (T data : datas) {
            viewHolder = new SimpleViewHolder<>();
            view = createFruitView(this, data);
            viewHolder.t = data;
            viewHolder.isNeedLayout = true;
            viewHolder.pattern = getFruitPattern(index, data);
            viewHolder.peakValue = getPeakValue(index, data);
            viewHolder.stepProgress = 0;
            viewHolder.step = getStep(index, data);

            handlerAdd(index, data, view);
            view.setTag(viewHolder);
            bindData(view, index, data);
            viewHolder.position = disjointPosint(viewHolder.pattern, view);

            registerListener(index, view);

            ++index;
        }

        requestLayout();
    }

    public void change(T t) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        View view;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            //noinspection unchecked
            SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
            if (isEqual(t, viewHolder.t)) {
                viewHolder.t = t;
                viewHolder.isNeedLayout = true;
                bindData(view, i, t);
                view.requestLayout();
            }
        }
    }

    public void change(int position, T t) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        position = Math.min(childCount - 1, Math.max(0, position));
        View view = getChildAt(position);
        //noinspection unchecked
        SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
        viewHolder.t = t;
        viewHolder.isNeedLayout = true;
        bindData(view, position, t);
        view.requestLayout();
    }

    public void remove(T t) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        View view;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            //noinspection unchecked
            SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
            if (isEqual(t, viewHolder.t)) {
                handlerRemove(i, view);
            }
        }
    }

    public void remove(int position) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        position = Math.min(childCount - 1, Math.max(0, position));
        handlerRemove(position, getChildAt(position));
    }

    private void handlerFrame() {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return;
        }
        View view;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            //noinspection unchecked
            SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
            float peakValue = viewHolder.peakValue;
            float step = viewHolder.step;
            float stepProgress = viewHolder.stepProgress;
            view.setTranslationY((float) (peakValue * Math.sin(peakValue * stepProgress * step)));
            stepProgress += EACH_STEP;
            if (stepProgress > 2 * Math.PI) {
                stepProgress = 0;
            }
            viewHolder.stepProgress = stepProgress;
        }
    }

    private void handlerAdd(int index, T data, View view) {
        addView(view);
        Animator addAnimation = getAddAnimation(index, data, view);
        if (addAnimation != null) {
            addAnimation.start();
        }
    }

    private void handlerRemove(final int position, final View view) {
        //noinspection unchecked
        SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
        Animator quitAnimation = getQuitAnimation(position, viewHolder.t, view);
        if (quitAnimation != null) {
            quitAnimation.start();
            quitAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    removeView(view);
                    int childCount = getChildCount();
                    for (int i = position; i < childCount - 1; i++) {
                        registerListener(i, getChildAt(i));
                    }
                }
            });
        } else {
            removeView(view);
            int childCount = getChildCount();
            for (int i = position; i < childCount - 1; i++) {
                registerListener(i, getChildAt(i));
            }
        }
    }

    private void registerListener(final int index, View view) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    //noinspection unchecked
                    SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) v.getTag();
                    mListener.onClick(v, index, viewHolder.t);
                }
            }
        });
    }

    private Point disjointPosint(int pattern, View view) {
        if (mWidth == 0 || mHeight == 0) {
            return new Point(0, 0);
        }
        Point point;
        measureChild(view, 0, 0);
        int viewWidth = view.getMeasuredWidth();
        int viewHeight = view.getMeasuredHeight();
        if (pattern == SimpleViewHolder.POSITION_PATTERN_CENTER) {
            point = new Point((mWidth - viewWidth) / 2, (mHeight - viewHeight) / 2);
        } else if (pattern == SimpleViewHolder.POSITION_PATTERN_LEFT_TOP) {
            point = new Point(10, 10);
        } else if (pattern == SimpleViewHolder.POSITION_PATTERN_LEFT_BOTTOM) {
            point = new Point(10, mHeight - viewHeight - 10);
        } else if (pattern == SimpleViewHolder.POSITION_PATTERN_RIGHT_TOP) {
            point = new Point(mWidth - viewWidth - 10, 10);
        } else if (pattern == SimpleViewHolder.POSITION_PATTERN_RIGHT_BOTTOM) {
            point = new Point(mWidth - viewWidth - 10, mHeight - viewHeight - 10);
        } else {
            point = autoDisjointPosint(view, viewWidth, viewHeight);
        }

        return point;
    }

    private Point autoDisjointPosint(View view, int viewWidth, int viewHeight) {
        Point point = null;

        // Try to find TRY_MAX_FIND_DISJOINT_FREQUENCY times
        for (int i = 0; i < TRY_MAX_FIND_DISJOINT_FREQUENCY; i++) {
            int x = new Random().nextInt(mWidth);
            int y = new Random().nextInt(mHeight);
            Rect rect = new Rect(x, y, x + viewWidth, y + viewHeight);
            if (isDisjoint(view, rect)) {
                point = createFruitPoint(x, y, viewWidth, viewHeight);
            }
        }

        // Standard of reduction
        if (point == null) {
            for (int i = 0; i < TRY_MAX_FIND_DISJOINT_FREQUENCY; i++) {
                int x = new Random().nextInt(mWidth);
                int y = new Random().nextInt(mHeight);
                if (isReduceTheCoincidence(x, y)) {
                    point = createFruitPoint(x, y, viewWidth, viewHeight);
                }
            }
        }

        // forehead
        if (point == null) {
            int x = new Random().nextInt(mWidth);
            int y = new Random().nextInt(mWidth);
            point = createFruitPoint(x, y, viewWidth, viewHeight);
        }

        return point;
    }

    private boolean isDisjoint(View targetView, Rect rect) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return true;
        }
        View view;
        Rect viewRect = new Rect();
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            if (targetView == view) {
                continue;
            }
            //noinspection unchecked
            SimpleViewHolder<T> viewHolder = (SimpleViewHolder<T>) view.getTag();
            Point position = viewHolder.position;

            viewRect.set(position.x, position.y, position.x + view.getMeasuredWidth(), position.y + view.getMeasuredHeight());
            if (isOverlapping(rect, viewRect)) {
                return false;
            }
        }
        return true;
    }

    private boolean isReduceTheCoincidence(int x, int y) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return true;
        }
        View view;
        for (int i = 0; i < childCount; i++) {
            view = getChildAt(i);
            SimpleViewHolder holder = (SimpleViewHolder) view.getTag();
            Point position = holder.position;
            if (position.x == x && position.y == y) {
                return false;
            }
        }
        return true;
    }

    private Point createFruitPoint(int x, int y, int viewWidthSpace, int viewHeightSpace) {
        return new Point(Math.min(mWidth - viewWidthSpace - 10, Math.max(10, x)),
                Math.min(mHeight - viewHeightSpace - 10, Math.max(10, y)));
    }

    /**
     * Two regional crossover
     *
     * @param rect1
     * @param rect2
     * @return
     */
    private boolean isOverlapping(Rect rect1, Rect rect2) {
        PointF c1 = new PointF(rect1.left + rect1.width() / 2.0f, rect1.top + rect1.height() / 2.0f);
        PointF c2 = new PointF(rect2.left + rect2.width() / 2.0f, rect2.top + rect2.height() / 2.0f);
        return ((Math.abs(c1.x - c2.x) <= rect1.width() / 2.0 + rect2.width() / 2.0 && Math.abs(c2.y - c1.y) <= rect1.height() / 2.0 + rect2.height() / 2.0));
    }

    public void stop() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void start() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendEmptyMessageDelayed(0, NUM_POLL_TIME);
        }
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    protected abstract void bindData(View view, int index, T data);

    protected abstract View createFruitView(ViewGroup NestedScrollingParent, T data);

    protected int getFruitPattern(int index, T data) {
        return SimpleViewHolder.POSITION_PATTERN_AUTO;
    }

    protected float getStep(int index, T data) {
        return 2;
    }

    protected float getPeakValue(int index, T data) {
        return 10;
    }

    protected abstract boolean isEqual(T t, T tt);

    protected Animator getQuitAnimation(int position, T t, View view) {
        return null;
    }

    protected Animator getAddAnimation(int index, T data, View view) {
        return null;
    }

    public void setListener(OnItemClickListener<T> listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener<T> {

        void onClick(View v, int position, T t);
    }

}