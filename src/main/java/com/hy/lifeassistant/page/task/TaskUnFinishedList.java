package com.hy.lifeassistant.page.task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.base.CommonUtils;
import com.hy.lifeassistant.base.SimpleAdapterFactory;
import com.hy.lifeassistant.dao.TaskDao;
import com.hy.lifeassistant.domain.Task;
import com.hy.lifeassistant.page.Main;
import com.hy.lifeassistant.view.PageableListView;
import com.hy.lifeassistant.view.PageableListView.ListViewSupporter;

import java.util.Date;
import java.util.List;

// 未完成任务列表
public class TaskUnFinishedList extends Fragment implements OnClickListener, ListViewSupporter<Task> {

    private Dialog taskAddDialog;

    private TaskDao taskDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        taskAddDialog = createTaskAddDialog();

        taskDao = new TaskDao();

        View view = inflater.inflate(R.layout.f_task_unfinsihed_list, container, false);

        PageableListView plvTasks = (PageableListView) view.findViewById(R.id.plvTasks);
        plvTasks.setSupporter(this);
        registerForContextMenu(plvTasks);

        FloatingActionButton fabAddTask = (FloatingActionButton) view.findViewById(R.id.fabAddTask);
        fabAddTask.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOperate:
                finishTask(v);
                break;
            case R.id.fabAddTask:
                taskAddDialog.show();
                break;
            case R.id.ibtnClose:
                taskAddDialog.dismiss();
                break;
            case R.id.btnAddTask:
                addTask();
                break;
        }
    }

    private Dialog createTaskAddDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.d_task_add, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        Dialog result = builder.create();
        // 当焦点移动在对话框里的输入框里时自动打开输入法
        result.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Integer[] levelImgs = {R.drawable.start_0, R.drawable.start_1, R.drawable.start_2};
        String[] levelNames = {"轻微", "一般", "重要"};
        int[] resIds = {R.id.ivTaskLevel, R.id.tvTaskLevel};
        SimpleAdapter adapter = SimpleAdapterFactory.create(getContext(), R.layout.i_task_level, resIds, levelImgs, levelNames);

        Spinner spTaskLevel = (Spinner) view.findViewById(R.id.spTaskLevel);
        spTaskLevel.setAdapter(adapter);
        spTaskLevel.setSelection(1, true);

        ImageButton ibtnClose = (ImageButton) view.findViewById(R.id.ibtnClose);
        ibtnClose.setOnClickListener(this);

        Button btnAddTask = (Button) view.findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(this);

        Window window = result.getWindow();
        LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 700;
        lp.height = 800;
        window.setAttributes(lp);

        return result;
    }

    private void addTask() {
        TextView tvTaskName = (TextView) taskAddDialog.findViewById(R.id.tvTaskName);
        String taskName = tvTaskName.getText().toString();

        Spinner spTaskLevel = (Spinner) taskAddDialog.findViewById(R.id.spTaskLevel);
        int level = spTaskLevel.getSelectedItemPosition();

        taskAddDialog.dismiss();

        if (!taskName.isEmpty()) {
            Task task = new Task();
            task.setName(taskName);
            task.setLevel(level);
            task.setId(CommonUtils.getUUid());
            task.setIsFinished(false);
            task.setStartTime(new Date());

            taskDao.add(task);

            refresh();
        }
    }

    private void finishTask(View v) {
        String id = v.getTag().toString();
        Task task = taskDao.findById(id);
        taskDao.finish(task);

        refresh();
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
        String time = CommonUtils.formatDateTime(task.getStartTime());
        tvTime.setText(time);

        ImageButton btnFinish = (ImageButton) view.findViewById(R.id.btnOperate);
        btnFinish.setTag(task.getId());
        btnFinish.setOnClickListener(this);

        return view;
    }

    @Override
    public List<Task> fetchData(int pageNo) {
        return taskDao.findByUnFinishedAndPageNo(pageNo);
    }

    private void refresh() {
        TaskTab tab = (TaskTab) getParentFragment();
        tab.refresh();
    }

}
