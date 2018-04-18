package davidlima.watsonpi.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import davidlima.watsonpi.http.JsonParser;
import davidlima.watsonpi.R;
import davidlima.watsonpi.models.Trait;

public class SummaryFragment extends Fragment {
    private static final String ARG_JSON = "param1";
    public static final int[] CHART_COLORS = {
            Color.rgb(255, 140, 157),
            Color.rgb(255, 208, 140),
            Color.rgb(255, 247, 140),
            Color.rgb(140, 234, 255),
            Color.rgb(192, 255, 140)
    };

    private JsonParser jsonParser;

    private HorizontalBarChart personalityBarChart;
    private HorizontalBarChart needsBarChart;
    private HorizontalBarChart valuesBarChart;

    public SummaryFragment() { }

    public static SummaryFragment newInstance(String json) {
        SummaryFragment fragment = new SummaryFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadSummaryText(view);
        loadPersonalityChart(view);
        loadNeedsChart(view);
        loadValuesChart(view);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (personalityBarChart != null) personalityBarChart.animateY(1000);
            if (needsBarChart != null) needsBarChart.animateY(1000);
            if (valuesBarChart != null) valuesBarChart.animateY(1000);
        }
    }

    private void loadSummaryText(View view) {
        TextView wordCountTV = view.findViewById(R.id.word_count_tv);
        wordCountTV.setText(jsonParser.wordCount);

        TextView characteristic1TV = view.findViewById(R.id.characteristic_1_tv);
        TextView characteristic2TV = view.findViewById(R.id.characteristic_2_tv);
        TextView characteristic3TV = view.findViewById(R.id.characteristic_3_tv);
        TreeMap<Float, String> characteristics = (TreeMap<Float, String>) jsonParser.characteristicsTS.clone();
        characteristic1TV.setText(characteristics.pollLastEntry().getValue());
        characteristic2TV.setText(characteristics.pollLastEntry().getValue());
        characteristic3TV.setText(characteristics.pollLastEntry().getValue());

        TextView description1KeyTV = view.findViewById(R.id.description_1_key_tv);
        TextView description1TV = view.findViewById(R.id.description_1_tv);
        TextView description2KeyTV = view.findViewById(R.id.description_2_key_tv);
        TextView description2TV = view.findViewById(R.id.description_2_tv);
        TextView description3KeyTV = view.findViewById(R.id.description_3_key_tv);
        TextView description3TV = view.findViewById(R.id.description_3_tv);
        TreeSet<Trait> facetsTreeSet = (TreeSet<Trait>) jsonParser.facetsTS.clone();
        Trait facet1 = facetsTreeSet.pollLast();
        Trait facet2 = facetsTreeSet.pollLast();
        Trait facet3 = facetsTreeSet.pollFirst();
        description1KeyTV.setText(JsonParser.getFacetDescription(facet1).getTerm());
        description1TV.setText(JsonParser.getFacetDescription(facet1).getDescription());
        description2KeyTV.setText(JsonParser.getFacetDescription(facet2).getTerm());
        description2TV.setText(JsonParser.getFacetDescription(facet2).getDescription());
        description3KeyTV.setText(JsonParser.getFacetDescription(facet3).getTerm());
        description3TV.setText(JsonParser.getFacetDescription(facet3).getDescription());
    }

    private void loadPersonalityChart(View view) {
        personalityBarChart = view.findViewById(R.id.summary_personality_chart);

        TreeSet<Trait> traitTreeSet = (TreeSet<Trait>) jsonParser.facetsTS.clone();

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Trait[] temp = new Trait[5];
        for (int i = 0; i < 2 ; i++) temp[i] = traitTreeSet.pollFirst();
        for (int i = 4; i > 1 ; i--) temp[i] = traitTreeSet.pollLast();
        int i = 0;
        for (Trait trait : temp) {
            entries.add(new BarEntry(i, trait.getPercentile()));
            labels.add(trait.getName());
            i++;
        }

        styleChart(personalityBarChart, "Personality", entries, labels);
    }

    private void loadNeedsChart(View view) {
        needsBarChart = view.findViewById(R.id.summary_needs_chart);

        TreeSet<Trait> traitTreeSet = (TreeSet<Trait>) jsonParser.needsTS.clone();

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Trait[] temp = new Trait[5];
        for (int i = 0; i < 2 ; i++) temp[i] = traitTreeSet.pollFirst();
        for (int i = 4; i > 1 ; i--) temp[i] = traitTreeSet.pollLast();
        int i = 0;
        for (Trait trait : temp) {
            entries.add(new BarEntry(i, trait.getPercentile()));
            labels.add(trait.getName());
            i++;
        }

        styleChart(needsBarChart, "Needs", entries, labels);
    }

    private void loadValuesChart(View view) {
        valuesBarChart = view.findViewById(R.id.summary_values_chart);

        TreeSet<Trait> traitTreeSet = (TreeSet<Trait>) jsonParser.valuesTS.clone();

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        Trait[] temp = new Trait[5];
        for (int i = 0; i < 2 ; i++) temp[i] = traitTreeSet.pollFirst();
        for (int i = 4; i > 1 ; i--) temp[i] = traitTreeSet.pollLast();
        int i = 0;
        for (Trait trait : temp) {
            entries.add(new BarEntry(i, trait.getPercentile()));
            labels.add(trait.getName());
            i++;
        }

        styleChart(valuesBarChart, "Values", entries, labels);
    }

    private void styleChart(HorizontalBarChart chart, String title, ArrayList<BarEntry> entries, final ArrayList<String> labels) {
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        };
        XAxis xAxis = chart.getXAxis();
        xAxis.setLabelCount(labels.size());
        xAxis.setValueFormatter(formatter);

        BarDataSet dataSet = new BarDataSet(entries, "");
        BarData data = new BarData(dataSet);
        dataSet.setColors(CHART_COLORS);
        dataSet.setDrawValues(false);
        chart.setData(data);
        chart.getDescription().setText(title);
        chart.getDescription().setTextSize(18f);
        chart.getLegend().setEnabled(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(false);
        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);
    }
}
