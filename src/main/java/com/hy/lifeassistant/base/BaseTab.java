package com.hy.lifeassistant.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.lifeassistant.R;
import com.viewpagerindicator.TabPageIndicator;

// 标签页基类
public abstract class BaseTab extends Fragment {

    private FragmentStatesPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.f_base_tab, container, false);

        adapter = new FragmentStatesPagerAdapter(getChildFragmentManager(), getFragments(), getTitles());

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp);
        viewPager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator) view.findViewById(R.id.tbi);
        indicator.setViewPager(viewPager);

        return view;
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    protected abstract String[] getTitles();

    protected abstract Fragment[] getFragments();

}
