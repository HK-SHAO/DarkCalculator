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

    class ViewHolder {
        TextView title;
        TextView vice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            convertView.setOnClickListener(onClickListener);
            convertView.setOnLongClickListener(onLongClickListener);

            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.text_item);
            if (viceText != null)
                viewHolder.vice = (TextView) convertView.findViewById(R.id.text_vice_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String text = this.text.get(position);
        if (text.equals("DEL"))
            viewHolder.title.setTextSize(24);
        viewHolder.title.setText(text);

        if (viceText != null) {
            String text2 = viceText.get(position);
            if (text2.equals("CLR"))
                viewHolder.vice.setTextSize(12);
            viewHolder.vice.setText(text2);
        }

        GridView.LayoutParams param = new GridView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                gridView.getHeight() / value);
        convertView.setLayoutParams(param);

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
