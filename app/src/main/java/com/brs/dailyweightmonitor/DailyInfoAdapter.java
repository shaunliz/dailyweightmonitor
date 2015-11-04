package com.brs.dailyweightmonitor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ikban on 2015-10-26.
 */
public class DailyInfoAdapter extends BaseAdapter {

    LayoutInflater mInflater;
    ArrayList<DailyInfo> arrayDailyInfo;

    public DailyInfoAdapter(Context context, ArrayList<DailyInfo> arrayItem){
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrayDailyInfo = arrayItem;
    }

    @Override
    public int getCount() {
        return arrayDailyInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayDailyInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.weight_list_layout, parent, false);
        }

        // date.
        TextView tvDate = (TextView)convertView.findViewById(R.id.date);
        tvDate.setText(arrayDailyInfo.get(position).getStrDate());

        // weight.
        TextView tvWeight = (TextView)convertView.findViewById(R.id.weight);
        tvWeight.setText(arrayDailyInfo.get(position).getStrWeight());

        // exercise.
        TextView tvExercise = (TextView)convertView.findViewById(R.id.exercise);
        if(arrayDailyInfo.get(position).isbExercise())
            tvExercise.setText("o");
        else
            tvExercise.setText("~");

        // drink.
        TextView tvDrink = (TextView)convertView.findViewById(R.id.drink);
        if(arrayDailyInfo.get(position).isbDrink())
            tvDrink.setText("o");
        else
            tvDrink.setText("~");

        // memo.
        TextView tvMemo = (TextView)convertView.findViewById(R.id.memo);
        tvMemo.setText(arrayDailyInfo.get(position).getStrMemo());

        return convertView;
    }
}
