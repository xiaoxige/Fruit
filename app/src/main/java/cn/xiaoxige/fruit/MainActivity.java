package cn.xiaoxige.fruit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.xiaoxige.fruitlibrary.DefaultFruitAnimation;
import cn.xiaoxige.fruitlibrary.DefaultFruitLayoutManager;
import cn.xiaoxige.fruitlibrary.FruitView;

public class MainActivity extends Activity {

    private FruitView fruitView;
    private FruitAdapter mAdapter;
    private Button btnTest;
    private LinearLayout llContainer;
    private TestLayout testLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fruitView = (FruitView) findViewById(R.id.fruitView);
        btnTest = (Button) findViewById(R.id.btnTest);
        llContainer = (LinearLayout) findViewById(R.id.llContainer);
        testLayout = (TestLayout) findViewById(R.id.testLayout);

        mAdapter = new FruitAdapter(this);


        fruitView.setLayoutManager(new DefaultFruitLayoutManager());
        fruitView.setFruitAnimation(new DefaultFruitAnimation(this));
        fruitView.setAdapter(mAdapter);

//        for (int i = 0; i < 10; i++) {
//            TextView textView = new TextView(this);
//            textView.setText("测试View index = " + i);
//            llContainer.addView(textView);
//        }


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final View view = llContainer.getChildAt(0);
//                ObjectAnimator enterScaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f);
//                ObjectAnimator enterScaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f);
//                AnimatorSet animatorSet = new AnimatorSet();
//                animatorSet.setDuration(1000);
//                animatorSet.play(enterScaleXAnimator).with(enterScaleYAnimator);
//                animatorSet.start();
//
//                animatorSet.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        llContainer.removeView(view);
//                    }
//                });
//                testLayout.removeTestView(testLayout.getChildAt(0));
            }
        });
    }
}
