package cn.xiaoxige.fruit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.xiaoxige.fruitlibrary.DefaultFruitAnimation;
import cn.xiaoxige.fruitlibrary.DefaultFruitLayoutManager;
import cn.xiaoxige.fruitlibrary.FruitView;

public class MainActivity extends Activity {

    private FruitView fruitView;
    private FruitAdapter mAdapter;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fruitView = (FruitView) findViewById(R.id.fruitView);
        btnTest = (Button)findViewById(R.id.btnTest);
        mAdapter = new FruitAdapter(this);



        fruitView.setLayoutManager(new DefaultFruitLayoutManager());
        fruitView.setFruitAnimation(new DefaultFruitAnimation(this));
        fruitView.setAdapter(mAdapter);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "测试", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
