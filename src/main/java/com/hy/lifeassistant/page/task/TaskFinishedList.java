package com.hy.lifeassistant.page.task;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.base.CommonUtils;
import com.hy.lifeassistant.dao.TaskDao;
import com.hy.lifeassistant.domain.Task;
import com.hy.lifeassistant.page.Main;
import com.hy.lifeassistant.view.PageableListView;
import com.hy.lifeassistant.view.PageableListView.ListViewSupporter;

import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

// 已完成任务列表
public class TaskFinishedList extends Fragment implements View.OnClickListener, ListViewSupporter<Task> {

    private TaskDao taskDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        taskDao = new TaskDao();

        View view = inflater.inflate(R.layout.f_task_finsihed_list, container, false);

        PageableListView plvTasks = (PageableListView) view.findViewById(R.id.plvTasks);
        plvTasks.setSupporter(this);
        registerForContextMenu(plvTasks);

        FloatingActionButton fabDelTask = (FloatingActionButton) view.findViewById(R.id.fabDelAllFinishedTask);
        fabDelTask.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOperate:
                String id = v.getTag().toString();
                deleteTaskById(id);
                break;
            case R.id.fabDelAllFinishedTask:
                tryToDeleteAllFinishedTasks();
                break;
        }
    }

    private void deleteTaskById(String id) {
        Task task = taskDao.findById(id);
        taskDao.delete(task);

        refresh();
    }

    private void tryToDeleteAllFinishedTasks() {
        final MaterialDialog dialog = new MaterialDialog(getContext());
        dialog.setTitle("确认");
        dialog.setMessage("是否删除所以已完成的任务?");
        dialog.setPositiveButton("是", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                taskDao.deleteAllFinished();
                dialog.dismiss();

                refresh();
            }

        });
        dialog.setNegativeButton("否", new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "删除");
        menu.add(Menu.NONE, Menu.FIRST + 2, 1, "查看");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 解决menuItem触发两次点击事件的bug, 记得方法最后返回true
        if (!getUserVisibleHint()) {
            return false;
        }

        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        Task task = (Task) menuInfo.targetView.getTag();

        switch (item.getItemId()) {
            case Menu.FIRST + 1:
                taskDao.delete(task);

                Main main = (Main) getActivity();
                main.startFragment(new TaskTab());
                break;
            case Menu.FIRST + 2:
                Toast.makeText(getContext(), task.getName(), Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }


    @Override
    public View getView(int position, Task task, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.i_task, null);
        view.setTag(task);

        TextView tvTaskName = (TextView) view.findViewById(R.id.tvTaskName);
        tvTaskName.setText(task.getName());

        ImageView ivTaskLevel = (ImageView) view.findViewById(R.id.ivTaskLevel);
        switch (task.getLevel()) {
            case 0:
                ivTaskLevel.setImageResource(R.drawable.start_0);
                break;
            case 1:
                ivTaskLevel.setImageResource(R.drawable.start_1);
                break;
            case 2:
                ivTaskLevel.setImageResource(R.drawable.start_2);
                break;
        }

        TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
        String startTime = CommonUtils.formatDateTime(task.getStartTime());
        String finishTime = CommonUtils.formatDateTime(task.getStartTime());
        tvTime.setText(startTime + "~" + finishTime);

        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnOperate);
        btnDelete.setBackgroundResource(R.drawable.remove);
        btnDelete.setTag(task.getId());
        btnDelete.setOnClickListener(this);

        return view;
    }

    @Override
    public List<Task> fetchData(int pageNo) {
        return taskDao.findByFinishedAndPageNo(pageNo);
    }

    private void refresh() {
        TaskTab tab = (TaskTab) getParentFragment();
        tab.refresh();
    }

}
