package cn.xiaoxige.fruit.simple;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.xiaoxige.fruit.R;
import cn.xiaoxige.fruitlibrary.simple.SimpleFruitView;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 */

public class SimpleTestView extends SimpleFruitView<SimpleEntity> {

    public SimpleTestView(Context context) {
        super(context);
    }

    public SimpleTestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleTestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void bindData(View view, int index, SimpleEntity data) {
        String massger = data.getMassger();
        ((TextView) view.findViewById(R.id.tvMsg)).setText(massger);
    }

    @Override
    protected View createFruitView(ViewGroup parent, SimpleEntity data) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_fruit, parent, false);
    }

    @Override
    protected boolean isEqual(SimpleEntity o, SimpleEntity tt) {
        return o.getId() == tt.getId();
    }


    @Override
    protected int getFruitPattern(int index, SimpleEntity data) {
        return super.getFruitPattern(index, data);
    }

    @Override
    protected float getStep(int index, SimpleEntity data) {
        return index + 1;
    }

    @Override
    protected float getPeakValue(int index, SimpleEntity data) {
        return super.getPeakValue(index, data);
    }

    @Override
    protected Animator getQuitAnimation(int position, SimpleEntity simpleEntity, View view) {
        ObjectAnimator quitAlphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        ObjectAnimator quitScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
        ObjectAnimator quitScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
        ObjectAnimator quitTranslateAnimator = ObjectAnimator.ofFloat(view, "translationY", 0f, -view.getTop() - view.getMeasuredHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.play(quitAlphaAnimator).with(quitScaleXAnimator).with(quitScaleYAnimator).with(quitTranslateAnimator);
        return animatorSet;
    }

    @Override
    protected Animator getAddAnimation(int index, SimpleEntity data, View view) {
        ObjectAnimator enterAlphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator enterScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator enterScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.play(enterAlphaAnimator).with(enterScaleXAnimator).with(enterScaleYAnimator);
        return animatorSet;
    }
}
