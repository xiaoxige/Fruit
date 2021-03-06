package cn.xiaoxige.fruitlibrary.complex;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Random;

/**
 * @author by zhuxiaoan on 2018/7/24 0024.
 */

public class DefaultFruitAnimation implements FruitView.FruitAnimator {

    private Context mContext;

    public DefaultFruitAnimation(Context context) {
        this.mContext = context;
    }

    @Override
    public Animator enter(View view) {
        ObjectAnimator enterAlphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator enterScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f);
        ObjectAnimator enterScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.play(enterAlphaAnimator).with(enterScaleXAnimator).with(enterScaleYAnimator);
        return animatorSet;
    }

    @Override
    public Animator quit(View view) {
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
    public Animator hover(View view) {
        ObjectAnimator viewTranslateAnimator = ObjectAnimator.ofFloat(view, "translationY",
                0, -10, 0, 10, 0);
        viewTranslateAnimator.setDuration(new Random().nextInt(100) + 2000);
        viewTranslateAnimator.setRepeatCount(-1);
        viewTranslateAnimator.setInterpolator(new LinearInterpolator());
        return viewTranslateAnimator;
    }

}
