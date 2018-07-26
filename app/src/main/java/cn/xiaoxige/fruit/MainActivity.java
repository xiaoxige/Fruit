package cn.xiaoxige.fruit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.xiaoxige.fruitlibrary.complex.DefaultFruitAnimation;
import cn.xiaoxige.fruitlibrary.complex.DefaultFruitLayoutManager;
import cn.xiaoxige.fruitlibrary.complex.FruitView;

public class MainActivity extends Activity {

    private FruitView fruitView;
    private FruitAdapter mAdapter;
    private Button btnTest;
    private Button btnAdd;
    private Button btnNew5;
    private Button btnChange;

    private int index = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fruitView = (FruitView) findViewById(R.id.fruitView);
        btnTest = (Button) findViewById(R.id.btnTest);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnNew5 = (Button) findViewById(R.id.btnNew5);
        btnChange = (Button) findViewById(R.id.btnChange);

        mAdapter = new FruitAdapter(this);


        fruitView.setLayoutManager(new DefaultFruitLayoutManager());
        fruitView.setFruitAnimation(new DefaultFruitAnimation(this));

        fruitView.setAdapter(mAdapter);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Object> objects = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    objects.add(new TestEntity("小稀革" + i));
                }

                mAdapter.setData(objects);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.addData(new TestEntity("小稀革" + (index++)));
            }
        });

        btnNew5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Object> objects = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    objects.add(new TestEntity("小稀革" + (index++)));
                }

                mAdapter.getObjects().addAll(objects);

                mAdapter.notifyDataSetChanged();
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemCount = mAdapter.getItemCount();
                if (itemCount <= 0) {
                    Toast.makeText(MainActivity.this, "没有任何数据可以修改", Toast.LENGTH_SHORT).show();
                    return;
                }

                int i = new Random().nextInt(itemCount);
                List<Object> objects = mAdapter.getObjects();
                Object o = objects.get(i);

                TestEntity testEntity = (TestEntity) o;
                String a = testEntity.getName();

                testEntity.setName("小稀革" + (index++));

                mAdapter.notifyItemChanged(i);

                Toast.makeText(MainActivity.this, "修改前为：" + a + "，修改后为：" + testEntity.getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
