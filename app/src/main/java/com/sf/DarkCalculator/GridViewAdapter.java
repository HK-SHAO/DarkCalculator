package com.sf.DarkCalculator;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> text;
    private List<String> viceText;
    private GridView gridView;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private int layoutId;
    private int value = 4;

    public GridViewAdapter(Context context, GridView gridView, List<String> text, int layoutId,
                           View.OnClickListener onClickListener) {
        super();
        this.context = context;
        this.text = text;
        this.gridView = gridView;
        this.layoutId = layoutId;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int position) {

        return text.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        convertView.setOnClickListener(onClickListener);
        convertView.setOnLongClickListener(onLongClickListener);
        GridView.LayoutParams param = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                gridView.getHeight() / value);
        convertView.setLayoutParams(param);

        TextView textView = (TextView) convertView.findViewById(R.id.text_item);
        String text = this.text.get(position);
        if (text.equals("DEL"))
            textView.setTextSize(24);
        textView.setText(text);

        if (viceText != null) {
            TextView viceTextView = (TextView) convertView.findViewById(R.id.text_vice_item);
            String text2 = viceText.get(position);
            if (text2.equals("CLR"))
                viceTextView.setTextSize(12);
            viceTextView.setText(text2);
        }

        return convertView;
    }

    public void setViceText(List<String> list) {
        this.viceText = list;
    }

    public void setValue(int value) {
        this.value = value;
        notifyDataSetChanged();
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
