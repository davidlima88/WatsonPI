package davidlima.watsonpi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeSet;

import davidlima.watsonpi.http.JsonParser;
import davidlima.watsonpi.R;
import davidlima.watsonpi.models.Trait;

public class PersonalityFragment extends Fragment {
    private static final String ARG_JSON = "param1";

    private JsonParser jsonParser;

    private RadarChart radarChart;

    public PersonalityFragment() {
    }

    public static PersonalityFragment newInstance(String json) {
        PersonalityFragment fragment = new PersonalityFragment();
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
        return inflater.inflate(R.layout.fragment_personality, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        loadPersonalityText(view);
        loadPersonalityChart(view);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (radarChart != null) {
                radarChart.animateXY(
                        1400, 1400,
                        Easing.EasingOption.EaseInOutQuad,
                        Easing.EasingOption.EaseInOutQuad);
            }
        }
    }

    private void loadPersonalityText(View view) {
        TextView personality1KeyTV = view.findViewById(R.id.personality_1_key_tv);
        TextView personality1TV = view.findViewById(R.id.personality_1_tv);
        TextView personality2KeyTV = view.findViewById(R.id.personality_2_key_tv);
        TextView personality2TV = view.findViewById(R.id.personality_2_tv);
        TextView personality3KeyTV = view.findViewById(R.id.personality_3_key_tv);
        TextView personality3TV = view.findViewById(R.id.personality_3_tv);
        TreeSet<Trait> big5TreeSet = (TreeSet<Trait>) jsonParser.big5TS.clone();
        Trait trait1 = big5TreeSet.pollLast();
        Trait trait2 = big5TreeSet.pollLast();
        Trait trait3 = big5TreeSet.pollFirst();
        personality1KeyTV.setText(trait1.getName());
        personality1TV.setText(JsonParser.getTraitDescription(trait1));
        personality2KeyTV.setText(trait2.getName());
        personality2TV.setText(JsonParser.getTraitDescription(trait2));
        personality3KeyTV.setText(trait3.getName());
        personality3TV.setText(JsonParser.getTraitDescription(trait3));
    }

    private void loadPersonalityChart(View view) {
        radarChart = view.findViewById(R.id.personality_radar_chart);

        TreeSet<Trait> big5TS = jsonParser.big5TS;
        ArrayList<RadarEntry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        for (Trait trait : big5TS) {
            entries.add(new RadarEntry(trait.getPercentile()));
            labels.add(trait.getName());
        }

        styleChart(entries, labels);
    }

    private void styleChart(ArrayList<RadarEntry> entries, final ArrayList<String> labels) {
        ArrayList<IRadarDataSet> dataSets = new ArrayList<>();

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setXOffset(0f);
        xAxis.setYOffset(0f);
        xAxis.setTextSize(8f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        });

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(1f);
        yAxis.setTextSize(9f);
        yAxis.setLabelCount(5, false);
        yAxis.setDrawLabels(false);

        radarChart.getLegend().setEnabled(false);
        radarChart.getDescription().setEnabled(false);
        radarChart.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        RadarDataSet dataSet = new RadarDataSet(entries, "");
        dataSet.setColor(R.color.colorPrimary);
        dataSet.setDrawFilled(true);

        dataSets.add(dataSet);

        RadarData data = new RadarData(dataSets);
        data.setValueTextSize(8f);

        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.format(Locale.CANADA, "%.2f", value * 100) + "%";
            }

        });

        radarChart.setData(data);
        radarChart.invalidate();
    }
}
