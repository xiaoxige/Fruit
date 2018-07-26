package cn.xiaoxige.fruitlibrary.simple;

import android.graphics.Point;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 *         Practical class of simple fruit operation
 */

public class SimpleViewHolder<T> {

    static final int POSITION_PATTERN_AUTO = 1;
    public static final int POSITION_PATTERN_CENTER = POSITION_PATTERN_AUTO << 1;
    public static final int POSITION_PATTERN_LEFT_TOP = POSITION_PATTERN_AUTO << 2;
    public static final int POSITION_PATTERN_LEFT_BOTTOM = POSITION_PATTERN_AUTO << 3;
    public static final int POSITION_PATTERN_RIGHT_TOP = POSITION_PATTERN_AUTO << 4;
    public static final int POSITION_PATTERN_RIGHT_BOTTOM = POSITION_PATTERN_AUTO << 5;

    public T t;
    public boolean isNeedLayout = true;
    public int pattern = POSITION_PATTERN_AUTO;
    public Point position;
    public float peakValue;
    public float stepProgress;
    public float step;

}
