package cn.xiaoxige.fruit.simple;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import cn.xiaoxige.fruit.R;
import cn.xiaoxige.fruitlibrary.simple.SimpleFruitView;

/**
 * @author by zhuxiaoan on 2018/7/26 0026.
 */

public class SimpleFruitActivity extends Activity {

    private SimpleTestView simpleFruitView;

    private Button btnAdd;
    private Button btnChange;

    private int index = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        simpleFruitView = (SimpleTestView) findViewById(R.id.simpleFruitView);
        btnChange = (Button)findViewById(R.id.btnChange);

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

        simpleFruitView.setListener(new SimpleFruitView.OnItemClickListener<SimpleEntity>() {
            @Override
            public void onClick(View v, int position, SimpleEntity simpleEntity) {
//                simpleFruitView.remove(position);
                simpleFruitView.remove(simpleEntity);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SimpleEntity> datas = new ArrayList<>();
                datas.add(new SimpleEntity(index, "小稀革" + index));
                index++;
                simpleFruitView.add(datas, true);
            }
        });
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleFruitView.change(0, new SimpleEntity(++index, "朱肖安" + index));
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
