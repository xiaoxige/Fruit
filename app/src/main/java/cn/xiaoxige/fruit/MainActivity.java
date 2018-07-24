package cn.xiaoxige.fruit;

import android.app.Activity;
import android.os.Bundle;

import cn.xiaoxige.fruitlibrary.DefaultFruitAnimation;
import cn.xiaoxige.fruitlibrary.DefaultFruitLayoutManager;
import cn.xiaoxige.fruitlibrary.FruitView;

public class MainActivity extends Activity {

    private FruitView fruitView;
    private FruitAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fruitView = (FruitView) findViewById(R.id.fruitView);
        mAdapter = new FruitAdapter(this);

        fruitView.setLayoutManager(new DefaultFruitLayoutManager());
        fruitView.setFruitAnimation(new DefaultFruitAnimation(this));
        fruitView.setAdapter(mAdapter);
    }
}
