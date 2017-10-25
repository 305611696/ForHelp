package pers.wtt.module_bluetooth;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;

import pers.wtt.module_bluetooth.bean.BlueTooth;

/**
 * Created by 王亭 on 2017/10/23.
 */

public class BTLstAdapter extends RecyclerArrayAdapter {

    public BTLstAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_btinfo, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        BlueTooth blueTooth = (BlueTooth) getItem(position);
        ViewHolder viewHolder = (ViewHolder)holder;
        viewHolder.tv_name.setText(blueTooth.getName());
        viewHolder.tv_address.setText(blueTooth.getAddress());
    }

    class ViewHolder extends BaseViewHolder{

        public TextView tv_name;
        public TextView tv_address;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_address = (TextView) itemView.findViewById(R.id.tv_address);
        }
    }

}
