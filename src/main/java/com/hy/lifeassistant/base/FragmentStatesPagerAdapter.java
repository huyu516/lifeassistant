package com.hy.lifeassistant.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

public class FragmentStatesPagerAdapter extends FragmentStatePagerAdapter {

    private Fragment[] fragments;

    private String[] titles;

    private FragmentManager fragmentManager;

//    public FragmentStatesPagerAdapter(Fragment fragment, Fragment[] fragments, String[] titles) {
//        this(fragment.getFragmentManager(), fragments, titles);
//    }

    public FragmentStatesPagerAdapter(FragmentManager fragmentManager, Fragment[] fragments, String[] titles) {
        super(fragmentManager);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int arg0) {
        return fragments[arg0];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        return super.instantiateItem(arg0, arg1);
    }

}
