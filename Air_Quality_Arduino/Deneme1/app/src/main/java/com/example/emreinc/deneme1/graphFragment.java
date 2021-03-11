package com.example.emreinc.deneme1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class graphFragment extends Fragment {

    View mView;

    String range;
    int year;
    int month;
    int day;

    public static AppFragment newInstance(String range, int year, int month, int day) {
        Bundle bundle = new Bundle();
        bundle.putString("range", range);
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);

        AppFragment fragment = new AppFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            range = bundle.getString("range");
            year = bundle.getInt("year");
            month = bundle.getInt("month");
            day = bundle.getInt("day");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.graph_fragment, container, false);
        Activity activ = getActivity();

        readBundle(getArguments());

        GraphView graph1 = (GraphView) getActivity().findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph1.addSeries(series1);

        // styling
        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if(data.getX() == 1) return Color.RED;
                else if(1 < data.getX() && data.getX() <= 3) return Color.BLUE;
                else return Color.BLACK;
            }
        });
        series1.setSpacing(20);
        series1.setDrawValuesOnTop(true);
        series1.setValuesOnTopColor(Color.RED);

        /*
        GraphView graph2 = (GraphView) activ.findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph2.addSeries(series2);

        // styling
        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if(data.getX() == 1) return Color.RED;
                else if(1 < data.getX() && data.getX() <= 3) return Color.BLUE;
                else return Color.BLACK;
            }
        });
        series2.setSpacing(20);
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.RED);
        GraphView graph3 = (GraphView) activ.findViewById(R.id.graph3);
        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 2),
                new DataPoint(2, 3),
                new DataPoint(3, 4),
                new DataPoint(4, 5)
        });
        graph3.addSeries(series3);

        // styling
        series3.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if(data.getX() == 1) return Color.RED;
                else if(1 < data.getX() && data.getX() <= 3) return Color.BLUE;
                else return Color.BLACK;
            }
        });
        series3.setSpacing(20);
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.RED);
    */
        return mView;
    }
}
