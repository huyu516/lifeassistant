package com.hy.lifeassistant.page.expense;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hy.lifeassistant.R;
import com.hy.lifeassistant.dao.ExpenseDao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

// 开支记录图表
public class ExpenseChart extends Fragment {

    private ExpenseDao expenseDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        expenseDao = new ExpenseDao();

        View view = inflater.inflate(R.layout.f_expense_chart, container, false);

        ColumnChartView ccv = (ColumnChartView) view.findViewById(R.id.ccv);
        setDate(ccv);

        PieChartView pcv = (PieChartView) view.findViewById(R.id.pcv);
        setData(pcv);

        return view;
    }

    private void setDate(ColumnChartView ccv) {
        ccv.setValueSelectionEnabled(false);

        float[][] grid = expenseDao.findForAnalysisByLast7Days();

        List<Column> columns = new ArrayList<>();

        for (int day = 0; day < grid.length; day++) {
            List<SubcolumnValue> values = new ArrayList<>();

            for (int type = 0; type < grid[0].length; type++) {
                values.add(new SubcolumnValue(grid[day][type], ChartUtils.COLORS[type]));
            }

            Column column = new Column(values);
            column.setHasLabels(true);
            column.setHasLabelsOnlyForSelected(false);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);
        data.setStacked(true);

        Axis axisY = new Axis().setHasLines(true);
        axisY.setName(" ");
        data.setAxisYLeft(axisY);

        Axis axisX = new Axis();
        List<AxisValue> axisValues = new ArrayList<>();
        List<Integer> days = getLast7Days();
        for (int i = 0; i < 7; i++) {
            AxisValue axisValue = new AxisValue(i, String.valueOf(days.get(i)).toCharArray());
            axisValues.add(axisValue);
        }
        axisX.setValues(axisValues);
        data.setAxisXBottom(axisX);

        ccv.setColumnChartData(data);
    }

    private void setData(PieChartView pcv) {
        pcv.setValueSelectionEnabled(true);
        pcv.setCircleFillRatio(0.8f); // 图形控制大小

        List<SliceValue> values = new ArrayList<>();

        float[] summary = expenseDao.findSummaryByLast7Days();

        for (int i = 0; i < summary.length; i++) {
            SliceValue value = new SliceValue(summary[i], ChartUtils.COLORS[i]);
            values.add(value);
        }

        PieChartData pieChartData = new PieChartData(values);
        pieChartData.setHasLabels(true);
        pieChartData.setHasLabelsOutside(true);

        pcv.setPieChartData(pieChartData);
    }

    private List<Integer> getLast7Days() {
        List<Integer> result = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);

        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, 1);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            result.add(day);
        }

        return result;
    }

}
