package com.sf.DarkCalculator;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by user on 2017/7/19.
 */

public class MainPagerAdapter extends PagerAdapter {
    private List<View> pageList;

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public MainPagerAdapter(List<View> pageList) {
        super();
        this.pageList = pageList;
    }

    @Override
    public int getCount() {
        return pageList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = pageList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(pageList.get(position));
    }
}
