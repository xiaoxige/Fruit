package cn.xiaoxige.fruit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.xiaoxige.fruitlibrary.FruitView;

/**
 * @author by zhuxiaoan on 2018/7/24 0024.
 */

public class FruitAdapter extends FruitView.Adapter {

    private Context mContext;

    private List<Object> objects = new ArrayList<>();

    public FruitAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<Object> data) {
        this.objects = (data);
        notifyDataSetChanged();
    }

    public void addData(Object o) {
        int size = this.objects.size();
        this.objects.add(o);
        notifyItemInserted(size);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public FruitView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fruit, parent, false));
    }

    @Override
    public void onBindViewHolder(FruitView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(position);
        ((ViewHolder) holder).registerListener(position);
//        Log.e("TAG", "position = " + position);
    }

    public List<Object> getObjects() {
        return objects;
    }

    class ViewHolder extends FruitView.ViewHolder {
        private TextView tvMsg;

        @Override
        protected int positionPattern() {
            return ViewHolder.POSITION_PATTERN_LEFT_TOP;
        }

        @Override
        protected boolean isCanAutoResetPositionPattern() {
            return true;
        }

        public ViewHolder(View itemView) {
            super(itemView);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
        }

        public void bindData(int position) {
            TestEntity testEntity = (TestEntity) objects.get(position);
            tvMsg.setText(testEntity.getName());
        }

        public void registerListener(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, ((TestEntity) objects.get(position)).getName(), Toast.LENGTH_SHORT).show();
                    objects.remove(position);
                    FruitAdapter.this.notifyItemRemoved(position);
                }
            });
        }
    }

}
