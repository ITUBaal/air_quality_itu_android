package com.example.emreinc.deneme1;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.common.util.Hex;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.annotation.IntegerRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//, OnMapReadyCallback
public class MainActivity extends AppCompatActivity implements NavigationMaster{

    private String database1 = "database1";
    private String collection1 = "collection1";

    String taken;

    //Date arrangements for sending to mongo
    private int year, month, day;

    TabLayout tabLayout;
    TabLayout tabDayTime;

    ProgressDialog builder123;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        context = this;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Log.i("disco123", "izinler verilmis");
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    1);
            Log.i("disco123", "izin yok");
        }
        /*
        builder123 = ProgressDialog.show(context, "Retrieving data...", "Please wait. This may take up to 20 seconds.");  //show a progress dialog
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        AppFragment fragment1 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment1.builder123(builder123, year, month, day);
        */
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0: // daily
                        resetGraphs();
                        AppFragment fragment = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment.morningColor();
                        fragment.setDaily();
                        TabLayout tabLayout32 = (TabLayout) findViewById(R.id.tab_layout2);
                        tabLayout32.setVisibility(View.VISIBLE);
                        tabLayout32.getTabAt(0).select();
                        return;
                    case 1: // weekly
                        resetGraphs();
                        AppFragment fragment1 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment1.setWeekly();
                        TabLayout tabLayout33 = (TabLayout) findViewById(R.id.tab_layout2);
                        tabLayout33.setVisibility(View.GONE);
                        return;
                    case 2: // monthly
                        resetGraphs();
                        AppFragment fragment2 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment2.setMonthly();
                        TabLayout tabLayout34 = (TabLayout) findViewById(R.id.tab_layout2);
                        tabLayout34.setVisibility(View.GONE);
                        return;
                    default:
                        Log.i("disco123", "default timezone tab");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("disco123", "onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("disco123", "onTabReselected: " + tab.getPosition());
            }
        });

        tabDayTime = (TabLayout) findViewById(R.id.tab_layout2);

        tabDayTime.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        Log.i("disco123", "morning selected");
                        AppFragment fragment = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment.morningColor();
                        return;
                    case 1:
                        Log.i("disco123", "afternoon selected");
                        AppFragment fragment1 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment1.afternoonColor();
                        return;
                    case 2:
                        Log.i("disco123", "night selected");
                        AppFragment fragment2 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment2.nightColor();
                        return;
                    default:
                        Log.i("disco123", "default daytime tab");
                        return;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("disco123", "2-onTabUnselected: " + tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("disco123", "2-onTabReselected: " + tab.getPosition());
            }
        });

        GraphView graph1 = (GraphView) findViewById(R.id.graph);
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "Morning", "Afternoon", "Night", ""});
        graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph1.removeAllSeries();
        series1.setTitle("PPM");
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph1.addSeries(series1);

        GraphView graph2 = (GraphView) findViewById(R.id.graph_second);
        graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph2.removeAllSeries();
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.getLegendRenderer().setSpacing(50);
        graph2.addSeries(series1);


        GraphView graph3 = (GraphView) findViewById(R.id.graph_third);
        graph3.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph3.removeAllSeries();
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph3.getLegendRenderer().setSpacing(100);
        graph3.addSeries(series1);

        Log.i("disco123", "Extra address");

    } // end of oncreate


    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Password?");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder1.setView(input);

                // Set up the buttons
                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        taken = input.getText().toString();
                        Log.i("disco123", taken);
                        if( taken.equals("a")) {
                            changeActivity();
                        }
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder1.show();

                return true;

            case R.id.action_calendar:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                DatePicker picker = new DatePicker(this);
                picker.setCalendarViewShown(false);
                Calendar calendar = Calendar.getInstance();
                picker.setMaxDate(calendar.getTimeInMillis());
                // tarih seçiminde üst limit "bugun"
                calendar.add(Calendar.DATE, -60);
                // tarih seçiminde alt limit bugunden 60 gun oncesi
                picker.setMinDate(calendar.getTimeInMillis());
                calendar.add(Calendar.DATE, 60);
                picker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int year1, int month1, int dayOfMonth1) {
                        Log.i("disco123", "Year=" + year1 + " Month=" + (month1 + 1) + " day=" + dayOfMonth1);
                        year = year1;
                        month = month1 + 1;
                        day = dayOfMonth1;
                    }
                });

                builder.setTitle("Set Monitoring Date");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        builder123 = ProgressDialog.show(context, "Retrieving data...", "Please wait. This may take up to 20 seconds.");  //show a progress dialog
                        Toolbar toolbarTop = (Toolbar) findViewById(R.id.my_toolbar);
                        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
                        String str = day + "/" + month + "/" + year;
                        mTitle.setText(str);
                        AppFragment fragment1 = (AppFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        fragment1.builder123(builder123, year, month, day);

                        //graphModifier();
                        Log.i("disco123", "Year=" + year + " Month=" + (month + 1) + " day=" + day);
                    }
                });

                builder.show();


                Log.i("disco123", "Date is " + (picker.getMonth()+1) + " " + picker.getYear());
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void changeActivity() {
        Intent intent = new Intent(this, CollectorActivity.class);
        Log.i("disco123", "geldiiikk");
        startActivity(intent);
    }

    public void dailyGraphModifier(List<Integer> morning, List<Integer> afternoon, List<Integer> night) {
        GraphView graph1 = (GraphView) findViewById(R.id.graph);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph1);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"", "Morning", "Afternoon", "Night", ""});
        graph1.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, morning.get(0)),
                new DataPoint(2, afternoon.get(0)),
                new DataPoint(3, night.get(0)),
                new DataPoint(4, 0),
        });
        graph1.removeAllSeries();
        series1.setSpacing(25);
        series1.setTitle("PPM(Today)");

        series1.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                if (data.getY() == 0) return 0x44000000; // grey
                else if (data.getY() < 800) return 0x6600FF00; // light green
                else if (data.getY() < 1000) return 0x66009900; // dark green
                else if (data.getY() < 1250) return 0x66FFC000; // yellow
                else if (data.getY() < 1450) return 0x66FF6600; // orange
                else return 0x66CC0000; // red
            }
        });

        graph1.getViewport().setYAxisBoundsManual(true);
        graph1.getViewport().setMinY(0);
        graph1.getViewport().setMaxY(1500);
        graph1.getLegendRenderer().setWidth(275);
        graph1.getLegendRenderer().setFixedPosition(630,0);
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
        graph1.addSeries(series1);

        // second graph
        GraphView graph2 = (GraphView) findViewById(R.id.graph_second);

        graph2.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, morning.get(1)),
                new DataPoint(2, afternoon.get(1)),
                new DataPoint(3, night.get(1)),
                new DataPoint(4, 0),
        });
        graph2.removeAllSeries();
        series2.setSpacing(25);
        series2.setTitle("PPM(Yesterday)");

        series2.setValueDependentColor(series1.getValueDependentColor());

        graph2.getViewport().setYAxisBoundsManual(true);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(1500);
        graph2.getLegendRenderer().setFixedPosition(570,0);
        graph2.getLegendRenderer().setSpacing(0);
        graph2.getLegendRenderer().setWidth(700);
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.addSeries(series2);

        GraphView graph3 = (GraphView) findViewById(R.id.graph_third);

        graph3.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        BarGraphSeries<DataPoint> series3 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, morning.get(2)),
                new DataPoint(2, afternoon.get(2)),
                new DataPoint(3, night.get(2)),
                new DataPoint(4, 0),
        });
        graph3.removeAllSeries();
        series3.setSpacing(25);
        series3.setTitle("PPM(2 days before)");

        series3.setValueDependentColor(series1.getValueDependentColor());

        graph3.getViewport().setYAxisBoundsManual(true);
        graph3.getViewport().setMinY(0);
        graph3.getViewport().setMaxY(1500);
        graph3.getLegendRenderer().setFixedPosition(520,5);
        graph3.getLegendRenderer().setSpacing(0);
        graph3.getLegendRenderer().setWidth(1000);
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph3.addSeries(series3);
    }


    public void weeklyGraphModifier(List<Integer> values){
        Log.i("disco123", "WEEKLY Graph");

        for(int i = 0; i<3; i++) {
            GraphView graph11;
            if(i==0) graph11 = (GraphView) findViewById(R.id.graph);
            else if(i==1) graph11 = (GraphView) findViewById(R.id.graph_second);
            else graph11 = (GraphView) findViewById(R.id.graph_third);

            int j = i*7;
            BarGraphSeries<DataPoint> series11 = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(-8, 0),
                    new DataPoint(-7, values.get(j)),
                    new DataPoint(-6, values.get(j+1)),
                    new DataPoint(-5, values.get(j+2)),
                    new DataPoint(-4, values.get(j+3)),
                    new DataPoint(-3, values.get(j+4)),
                    new DataPoint(-2, values.get(j+5)),
                    new DataPoint(-1, values.get(j+6)),
                    new DataPoint(0, 0),
            });
            graph11.removeAllSeries();
            series11.setSpacing(25);
            if(i==0){
                series11.setTitle("PPM(Morning)");
                graph11.getLegendRenderer().setFixedPosition(620,0);
            }
            else if(i==1){
                series11.setTitle("PPM(Afternoon)");
                graph11.getLegendRenderer().setFixedPosition(570,0);
            }
            else{
                series11.setTitle("PPM(Night)");
                graph11.getLegendRenderer().setFixedPosition(650,0);
            }

            series11.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    if (data.getY() == 0) return 0x44000000; // grey
                    else if (data.getY() < 800) return 0x6600FF00; // light green
                    else if (data.getY() < 1000) return 0x66009900; // dark green
                    else if (data.getY() < 1250) return 0x66FFC000; // yellow
                    else if (data.getY() < 1450) return 0x66FF6600; // orange
                    else return 0x66CC0000; // red
                }
            });
            graph11.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter());
            //graph11.getGridLabelRenderer().setNumHorizontalLabels(31);
            graph11.getLegendRenderer().setVisible(true);
            graph11.getGridLabelRenderer().setHorizontalAxisTitle("Days(Past)");
            graph11.getViewport().setXAxisBoundsManual(true);
            graph11.getViewport().setMinX(-8);
            graph11.getViewport().setMaxX(0);

            graph11.getViewport().setYAxisBoundsManual(true);
            graph11.getViewport().setMinY(0);
            graph11.getViewport().setMaxY(1500);
            graph11.getLegendRenderer().setVisible(true);
            graph11.getLegendRenderer().setSpacing(0);
            graph11.getLegendRenderer().setWidth(700);
            graph11.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            series11.setDrawValuesOnTop(true);
            series11.setValuesOnTopColor(Color.GRAY);
            series11.setValuesOnTopSize(30);
            graph11.addSeries(series11);
        }
    }

    public void monthlyGraphModifier(List<Integer> values){

        Log.i("disco123", "MONTHLY graph");

        for(int i = 0; i<3; i++) {
            Log.i("disco123", "GRAPH MONTHLY graph " + values.size());

            GraphView graph11;
            if(i==0) graph11 = (GraphView) findViewById(R.id.graph);
            else if(i==1) graph11 = (GraphView) findViewById(R.id.graph_second);
            else graph11 = (GraphView) findViewById(R.id.graph_third);

            /*
            StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph11);
            staticLabelsFormatter.setHorizontalLabels(new String[] {"10/05", "", "", "", "", "",
                                                                        "16/05", "", "", "", "", "",
                                                                        "22/05", "", "", "", "", "",
                                                                        "28/05", "", "", "", "", "",
                                                                        "3/05", "", "", "", "", "",
                                                                        "9/05"                      });
            graph11.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
            */

            int j = i*30;
            BarGraphSeries<DataPoint> series11 = new BarGraphSeries<>(new DataPoint[]{
                    new DataPoint(-31, 0),
                    new DataPoint(-30, values.get(j)),
            });
            for(int k = 29; k>0; k--){
                series11.appendData(new DataPoint(-k,values.get(j+30-k)), true, 92);
            }
            series11.appendData(new DataPoint(0,0), true, 92);
            graph11.removeAllSeries();
            series11.setSpacing(30);
            if(i==0){
                series11.setTitle("PPM(Morning)");
                graph11.getLegendRenderer().setFixedPosition(620,0);
            }
            else if(i==1){
                series11.setTitle("PPM(Afternoon)");
                graph11.getLegendRenderer().setFixedPosition(570,0);
            }
            else{
                series11.setTitle("PPM(Night)");
                graph11.getLegendRenderer().setFixedPosition(650,0);
            }

            series11.setValueDependentColor(new ValueDependentColor<DataPoint>() {
                @Override
                public int get(DataPoint data) {
                    if (data.getY() == 0) return 0x44000000; // grey
                    else if (data.getY() < 800) return 0x6600FF00; // light green
                    else if (data.getY() < 1000) return 0x66009900; // dark green
                    else if (data.getY() < 1250) return 0x66FFC000; // yellow
                    else if (data.getY() < 1450) return 0x66FF6600; // orange
                    else return 0x66CC0000; // red
                }
            });

            graph11.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter());
            //graph11.getGridLabelRenderer().setNumHorizontalLabels(31);
            graph11.getLegendRenderer().setVisible(true);
            graph11.getGridLabelRenderer().setHorizontalAxisTitle("Days(Past)");
            //graph11.getGridLabelRenderer().setHorizontalAxisTitleTextSize(34);
            graph11.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            series11.setDrawValuesOnTop(false);
            graph11.getViewport().setXAxisBoundsManual(true);
            graph11.getViewport().setMinX(-31);
            graph11.getViewport().setMaxX(0);

            graph11.getViewport().setYAxisBoundsManual(true);
            graph11.getViewport().setMinY(0);
            graph11.getViewport().setMaxY(1500);
            graph11.getLegendRenderer().setSpacing(0);
            graph11.getLegendRenderer().setWidth(700);
            graph11.getViewport().setScalable(false);
            //graph11.getGridLabelRenderer().setNumHorizontalLabels(32);
            graph11.addSeries(series11);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // secenekleri bara eklemek
        // Action Bar içinde kullanılacak menü öğelerini inflate edelim
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_first, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void mongoCaller(){
        Log.i("disco123", "MONGO COLLAR");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.map, AppFragment.newInstance(year, month, day));
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void resetGraphs(){
        GraphView graph1 = (GraphView) findViewById(R.id.graph);

        BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 0),
        });
        graph1.removeAllSeries();
        series1.setTitle("PPM");
        graph1.getLegendRenderer().setVisible(true);
        graph1.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph1.addSeries(series1);

        GraphView graph2 = (GraphView) findViewById(R.id.graph_second);

        graph2.removeAllSeries();
        graph2.getLegendRenderer().setVisible(true);
        graph2.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph2.addSeries(series1);

        GraphView graph3 = (GraphView) findViewById(R.id.graph_third);

        graph3.removeAllSeries();
        graph3.getLegendRenderer().setVisible(true);
        graph3.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph3.addSeries(series1);
    }
}
