package cn.xiaoxige.fruit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.xiaoxige.fruitlibrary.FruitView;

/**
 * @author by zhuxiaoan on 2018/7/24 0024.
 */

public class FruitAdapter extends FruitView.Adapter {

    private Context mContext;

    public FruitAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        return 100;
    }

    @Override
    public FruitView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fruit, parent, false));
    }

    @Override
    public void onBindViewHolder(FruitView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bindData(position);
        ((ViewHolder) holder).registerListener(position);
    }


    class ViewHolder extends FruitView.ViewHolder {
        private TextView tvMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvMsg = (TextView) itemView.findViewById(R.id.tvMsg);
        }

        public void bindData(int position) {
            tvMsg.setText(position + "");
        }

        public void registerListener(final int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FruitAdapter.this.notifyItemRemoved(position);
                }
            });
        }
    }

}
