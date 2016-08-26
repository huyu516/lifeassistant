package com.hy.lifeassistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hy.lifeassistant.R;

import java.util.ArrayList;
import java.util.List;

// 带有分页功能的ListView
public class PageableListView extends ListView {

    public PageableListView(Context context) {
        super(context);
    }

    public PageableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PageableListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View loadingIndicactor = LayoutInflater.from(context).inflate(R.layout.v_loadingindicator, null, false);
        addFooterView(loadingIndicactor);
    }

    public void setSupporter(ListViewSupporter supporter) {
        ArrayAdapter adapter = new MyArrayAdapter(supporter);
        setAdapter(adapter);
        setOnScrollListener(new MyOnScrollListener(supporter, adapter));
    }

    private void onLoadFinished() {
        ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbLoading.setVisibility(GONE);

        ImageView ivLoading = (ImageView) findViewById(R.id.ivLoading);
        ivLoading.setVisibility(VISIBLE);

        TextView tvLoading = (TextView) findViewById(R.id.tvLoading);
        tvLoading.setText("已加载完全部数据");
    }

    private class MyArrayAdapter extends ArrayAdapter {

        private ListViewSupporter supporter;

        public MyArrayAdapter(ListViewSupporter supporter) {
            super(PageableListView.this.getContext(), 0, new ArrayList());
            this.supporter = supporter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Object data = getItem(position);
            View view = supporter.getView(position, data, convertView, parent);
            return view;
        }

    }

    private class MyOnScrollListener implements OnScrollListener {

        private ListViewSupporter supporter;

        private ArrayAdapter adapter;

        private int pageNo = 1;

        private boolean hasData = true;

        public MyOnScrollListener(ListViewSupporter supporter, ArrayAdapter adapter) {
            this.supporter = supporter;
            this.adapter = adapter;

            // 加载第一页
            onScroll(null, 1, 1, 0);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (hasData && firstVisibleItem + visibleItemCount + 1 >= totalItemCount) {
                List datas = supporter.fetchData(pageNo++);
                if (datas != null && !datas.isEmpty()) {
                    adapter.addAll(datas);
                    adapter.notifyDataSetChanged();
                } else {
                    hasData = false;
                    onLoadFinished();
                }
            }

        }
    }

    public interface ListViewSupporter<T> {

        View getView(int position, T data, View convertView, ViewGroup parent);

        List<T> fetchData(int pageNo);

    }

}



