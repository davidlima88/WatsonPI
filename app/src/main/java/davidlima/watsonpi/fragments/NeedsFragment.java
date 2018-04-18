package davidlima.watsonpi.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.TreeSet;

import davidlima.watsonpi.http.JsonParser;
import davidlima.watsonpi.R;
import davidlima.watsonpi.models.Trait;

public class NeedsFragment extends Fragment {
    private static final String ARG_JSON = "param1";

    private JsonParser jsonParser;

    private HorizontalBarChart barChart;

    public NeedsFragment() {
    }

    public static NeedsFragment newInstance(String json) {
        NeedsFragment fragment = new NeedsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_JSON, json);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jsonParser = JsonParser.getInstance(getArguments().getString(ARG_JSON));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_needs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadNeedsText(view);
        loadNeedsChart(view);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (barChart != null) barChart.animateY(1000);
        }
    }

    private void loadNeedsText(View view) {
        TextView needs1KeyTV = view.findViewById(R.id.needs_1_key_tv);
        TextView needs1TV = view.findViewById(R.id.needs_1_tv);
        TextView needs2KeyTV = view.findViewById(R.id.needs_2_key_tv);
        TextView needs2TV = view.findViewById(R.id.needs_2_tv);
        TextView needs3KeyTV = view.findViewById(R.id.needs_3_key_tv);
        TextView needs3TV = view.findViewById(R.id.needs_3_tv);
        TreeSet<Trait> needsTreeSet = (TreeSet<Trait>) jsonParser.needsTS.clone();
        Trait trait1 = needsTreeSet.pollLast();
        Trait trait2 = needsTreeSet.pollLast();
        Trait trait3 = needsTreeSet.pollFirst();
        needs1KeyTV.setText(trait1.getName());
        needs1TV.setText(jsonParser.getTraitDescription(trait1));
        needs2KeyTV.setText(trait2.getName());
        needs2TV.setText(jsonParser.getTraitDescription(trait2));
        needs3KeyTV.setText(trait3.getName());
        needs3TV.setText(jsonParser.getTraitDescription(trait3));
    }

    private void loadNeedsChart(View view) {
        barChart = view.findViewById(R.id.needs_bar_chart);

        TreeSet<Trait> needsTS = jsonParser.needsTS;

        ArrayList<BarEntry> needsEntries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        int i = 0;
        for (Trait trait : needsTS) {
            needsEntries.add(new BarEntry(i, trait.getPercentile()));
            labels.add(trait.getName());
            i++;
        }

        styleChart(needsEntries, labels);
    }

    private void styleChart(ArrayList<BarEntry> entries, final ArrayList<String> labels) {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        };
        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(formatter);

        BarDataSet dataSet = new BarDataSet(entries, "");
        BarData data = new BarData(dataSet);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setDrawValues(false);
        barChart.setData(data);
        barChart.getDescription().setText("Needs");
        barChart.getDescription().setTextSize(18f);
        barChart.getLegend().setEnabled(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setDrawAxisLine(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1000);
    }
}
