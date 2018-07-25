package cn.xiaoxige.fruit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.xiaoxige.fruitlibrary.DefaultFruitAnimation;
import cn.xiaoxige.fruitlibrary.DefaultFruitLayoutManager;
import cn.xiaoxige.fruitlibrary.FruitView;

public class MainActivity extends Activity {

    private FruitView fruitView;
    private FruitAdapter mAdapter;

    private Button btnTest;
    private Button btnAddOne;
    private int newIndex = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fruitView = (FruitView) findViewById(R.id.fruitView);
        btnTest = (Button) findViewById(R.id.btnTest);
        btnAddOne = (Button) findViewById(R.id.btnAddOne);

        mAdapter = new FruitAdapter(this);


        fruitView.setLayoutManager(new DefaultFruitLayoutManager());
        fruitView.setFruitAnimation(new DefaultFruitAnimation(this));

        fruitView.setAdapter(mAdapter);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Object> objects = new ArrayList<>();
                for (int i = 0; i < 6; i++) {
                    objects.add(i);
                }

                mAdapter.setData(objects);
            }
        });

        btnAddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addData(newIndex++);
            }
        });
    }
}
