package cn.xiaoxige.fruit.simple;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import cn.xiaoxige.fruit.R;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 */

public class SimpleFruitActivity extends Activity {

    private SimpleTestView simpleFruitView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        simpleFruitView = (SimpleTestView) findViewById(R.id.simpleFruitView);

        simpleFruitView.post(new Runnable() {
            @Override
            public void run() {
                List<SimpleEntity> simpleEntities = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    simpleEntities.add(new SimpleEntity(i, "小稀革" + i));
                }
                simpleFruitView.add(simpleEntities, false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        simpleFruitView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        simpleFruitView.stop();
    }

    @Override
    protected void onDestroy() {
        simpleFruitView.release();
        super.onDestroy();
    }
}
