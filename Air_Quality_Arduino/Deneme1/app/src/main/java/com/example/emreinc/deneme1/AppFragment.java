package com.example.emreinc.deneme1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;

import com.google.android.gms.common.util.Hex;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.String.valueOf;

public class AppFragment extends Fragment implements OnMapReadyCallback, AsyncResponse {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    List<Polygon> polyList = new ArrayList<Polygon>();
    List<PolygonOptions> optList = new ArrayList<PolygonOptions>();

    // Date arrangements
    int year;
    int month;
    int day;

    List<Integer> morning = new ArrayList<Integer>();
    List<Integer> afternoon = new ArrayList<Integer>();
    List<Integer> night = new ArrayList<Integer>();

    List<Integer> morning_1 = new ArrayList<Integer>();
    List<Integer> afternoon_1 = new ArrayList<Integer>();
    List<Integer> night_1 = new ArrayList<Integer>();

    List<Integer> morning_2 = new ArrayList<Integer>();
    List<Integer> afternoon_2 = new ArrayList<Integer>();
    List<Integer> night_2 = new ArrayList<Integer>();

    List<Integer> morning_3 = new ArrayList<Integer>();
    List<Integer> afternoon_3 = new ArrayList<Integer>();
    List<Integer> night_3 = new ArrayList<Integer>();

    List<Integer> weekly_list = new ArrayList<Integer>();
    List<Integer> monthly_list = new ArrayList<Integer>();

    private ProgressDialog builder12;

    private int rectangle = 100;
    private int old_rectangle = 100;
    private Polygon demo_polygon;

    boolean daily = true;
    boolean weekly = false;
    boolean monthly = false;

    int dailyCount = 0;


    public static AppFragment newInstance(int year, int month, int day) {
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);

        AppFragment fragment = new AppFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
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
        mView = inflater.inflate(R.layout.app_fragment, container, false);
        Activity activ = getActivity();
        readBundle(getArguments());
        Log.i("disco123", "argumanlar alindi." + " Yil:" + year + " Ay:" + month + " Gun:" + day);

        for(int i = 0; i<96; i++){
            morning.add(0);
            morning_1.add(0);
            morning_2.add(0);
            morning_3.add(0);

            afternoon.add(0);
            afternoon_1.add(0);
            afternoon_2.add(0);
            afternoon_3.add(0);

            night.add(0);
            night_1.add(0);
            night_2.add(0);
            night_3.add(0);
        }

        for(int i=0; i<21; i++) weekly_list.add(0);
        for(int i=0; i<90; i++) monthly_list.add(0);

        return mView;
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            int year1 = Integer.valueOf(selectedYear);
            int month1 = Integer.valueOf(selectedMonth + 1);
            int day1 = Integer.valueOf(selectedDay);
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.getActivity());
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //googleMap.addMarker(new MarkerOptions().position(new LatLng(41.105036,29.022825)).title("ITU").snippet("CampusCenter"));
        CameraPosition emre = CameraPosition.builder().target(new LatLng(41.104696, 29.024295)).zoom(15).bearing(0).build();
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(emre));

        // Initial points for rectangles
        // The bottomleft corner point of the topleft rectangle.
        double x = 41.106967;
        double x1 = 41.106967;
        double y = 29.017990;
        double y1 = 29.017990;
        int k = 0;

        PolygonOptions opt99 = new PolygonOptions()
                .add(new LatLng(99, 99),
                        new LatLng(99+0.0009049, 99),
                        new LatLng(99 + 0.0009049, 99 + 0.001211));
        demo_polygon = mGoogleMap.addPolygon(opt99);
        // the polygon above is a demo polygon. It is used for storing the fillcolor of the rectangle which was clicked previously

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 12; j++) {
                // Instantiates a new Polygon object and adds points to define a rectangle
                PolygonOptions opt0 = new PolygonOptions()
                        .add(new LatLng(x, y),
                                new LatLng(x, y + 0.001211),
                                new LatLng(x + 0.0009049, y + 0.001211),
                                new LatLng(x + 0.0009049, y),
                                new LatLng(x, y));
                Polygon polygon0 = mGoogleMap.addPolygon(opt0);
                polygon0.setFillColor(0x44000000);
                polygon0.setStrokeWidth(6);
                polygon0.setStrokeColor(0x66000000);
                polyList.add(polygon0);
                optList.add(opt0);
                //x = x + 0.0007359;
                y = y + 0.001211;
                k++;
            }
            x = x1;
            x = x - 0.0009049;
            x1 = x;
            y = y1;
            y = y;
            y1 = y;
        }

        //double point1 = optList.get(99).getPoints().get(0).latitude;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {

                builder12 = ProgressDialog.show(getActivity(), "Updating graphs...", "Please wait.");  //show a progress dialog

                double a = 41.106967 - 7*0.0009049;
                double lat = arg0.latitude - a;
                double lon = arg0.longitude - 29.017990;
                double kat1 = lat / 0.0009049;
                int kat3 = (int) kat1;
                kat3 = 7 - kat3; // row of the clicked rectangle

                double kat2 = lon / 0.001211;
                int kat4 = (int) kat2; // related column

                if(!(kat3 > 7 || kat4 > 11 || kat3 < 0 || kat4 < 0)) {
                    if (rectangle != 100) {
                        old_rectangle = rectangle;
                        if(daily)polyList.get(old_rectangle).setFillColor(demo_polygon.getFillColor());
                        else polyList.get(old_rectangle).setFillColor(0x44000000);
                    }
                    rectangle = 12 * kat3 + kat4;
                    demo_polygon.setFillColor(polyList.get(rectangle).getFillColor());
                    if(daily) dailyRectangle(rectangle);
                    else if(weekly) weeklyRectangle(rectangle);
                    else monthlyRectangle(rectangle);
                    // graph fonksiyonu

                }
            }
        });

    }

    public void dailyRectangle(int rectNo){
        MongoRectangle asyncTask1 = new MongoRectangle(this, year, month, day, 1, rectNo, 1);
        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask2 = new MongoRectangle(this, year, month, day, 2, rectNo, 1);
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask3 = new MongoRectangle(this, year, month, day, 3, rectNo, 1);
        asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
    }

    public void weeklyRectangle(int rectNo){
        MongoRectangle asyncTask1 = new MongoRectangle(this, year, month, day, 1, rectNo, 2);
        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask2 = new MongoRectangle(this, year, month, day, 2, rectNo, 2);
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask3 = new MongoRectangle(this, year, month, day, 3, rectNo, 2);
        asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
    }

    public void monthlyRectangle(int rectNo){
        MongoRectangle asyncTask1 = new MongoRectangle(this, year, month, day, 1, rectNo, 3);
        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask2 = new MongoRectangle(this, year, month, day, 2, rectNo, 3);
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        MongoRectangle asyncTask3 = new MongoRectangle(this, year, month, day, 3, rectNo, 3);
        asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
    }



    public void morningColor() {
        Log.i("disco123", "MORNING");
            for (int i = 0; i < 96; i++)
                polyList.get(i).setFillColor(morning.get(i));
            if(rectangle != 100) polyList.get(rectangle).setFillColor(0xFFFFFFFF);
    }

    public void afternoonColor() {
        Log.i("disco123", "AFTERNOON");
            for (int i = 0; i < 96; i++)
                polyList.get(i).setFillColor(afternoon.get(i));
        if(rectangle != 100) polyList.get(rectangle).setFillColor(0xFFFFFFFF);
    }

    public void nightColor() {
        Log.i("disco123", "NIGHT");
            for (int i = 0; i < 96; i++)
                polyList.get(i).setFillColor(night.get(i));
        if(rectangle != 100) polyList.get(rectangle).setFillColor(0xFFFFFFFF);
    }

    public void builder123(ProgressDialog builder123, int year, int month, int day){
        this.year = year;
        this.month = month;
        this.day = day;
        builder12 = builder123;
        Log.i("disco123", "BUILDER: " + year + " " + month + " " + day);
        retrieveData();
    }

    public void retrieveData(){
        mongodata asyncTask1 = new mongodata(this, year, month, day, 1);
        asyncTask1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        mongodata asyncTask2 = new mongodata(this, year, month, day, 2);
        asyncTask2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
        mongodata asyncTask3 = new mongodata(this, year, month, day, 3);
        asyncTask3.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, optList);
    }

    public void setDaily(){
        daily = true;
        weekly = false;
        monthly = false;
    }
    public void setWeekly(){
        daily = false;
        weekly = true;
        monthly = false;
        for(int i=0; i<96; i++) polyList.get(i).setFillColor(0x44000000);
    }
    public void setMonthly(){
        daily = false;
        weekly = false;
        monthly = true;
        for(int i=0; i<96; i++) polyList.get(i).setFillColor(0x44000000);
    }

    @Override
    public void dateFinish(List<Integer> colors, int selection, List<Integer> values) {
        //Log.i("disco123", "integer-color : " + colors.get(0));
        boolean fin = false;
        switch (selection){
            case 1: for (int i = 0; i < 96; i++){ morning.set(i, colors.get(i)); morning_1.set(i, values.get(i));}
                    break;
            case 2: for (int i = 0; i < 96; i++){ afternoon.set(i, colors.get(i)); afternoon_1.set(i, values.get(i)); }
                    break;
            case 3: for (int i = 0; i < 96; i++){ night.set(i, colors.get(i)); night_1.set(i, values.get(i));
                    builder12.dismiss();}
                    fin = true;
                    break;
        }
        if(fin) {
            TabLayout tabLayout32 = getActivity().findViewById(R.id.tab_layout);
            int tabPos2 = tabLayout32.getSelectedTabPosition();
            if(tabPos2 == 0) {
                Log.i("disco123", "Tab 0 !");
                TabLayout tabLayout33 = getActivity().findViewById(R.id.tab_layout2);
                int tabPos3 = tabLayout33.getSelectedTabPosition();
                if (tabPos3 == 0)
                    for (int i = 0; i < 96; i++) polyList.get(i).setFillColor(morning.get(i));
                if (tabPos3 == 1)
                    for (int i = 0; i < 96; i++) polyList.get(i).setFillColor(afternoon.get(i));
                if (tabPos3 == 2)
                    for (int i = 0; i < 96; i++) polyList.get(i).setFillColor(night.get(i));
            }
            Log.i("disco123", "Tab 0 degil..");
        }
    }

    @Override
    public void dayFinish(List<Integer> values, int selection) {
        polyList.get(rectangle).setFillColor(0xFFFFFFFF);

        builder12.dismiss();

        boolean ret = false;
        switch (selection) {
            case 1:
                morning_2.set(rectangle, values.get(0));
                morning_3.set(rectangle, values.get(1));
                break;
            case 2:
                afternoon_2.set(rectangle, values.get(0));
                afternoon_3.set(rectangle, values.get(1));
                break;
            case 3:
                night_2.set(rectangle, values.get(0));
                night_3.set(rectangle, values.get(1));
                ret = true;
                break;
        }

        if(ret) {
            Log.i("disco123", "1. " + morning_1.get(rectangle) + " " + morning_2.get(rectangle) + " " + morning_3.get(rectangle) + "\n"
                    + "2. " + afternoon_1.get(rectangle) + " " + afternoon_2.get(rectangle) + " " + afternoon_3.get(rectangle) + "\n"
                    + "3. " + night_1.get(rectangle) + " " + night_2.get(rectangle) + " " + night_3.get(rectangle) + " ");

            List<Integer> morning_r = new ArrayList<Integer>();
            morning_r.add(morning_1.get(rectangle));
            morning_r.add(morning_2.get(rectangle));
            morning_r.add(morning_3.get(rectangle));

            List<Integer> afternoon_r = new ArrayList<Integer>();
            afternoon_r.add(afternoon_1.get(rectangle));
            afternoon_r.add(afternoon_2.get(rectangle));
            afternoon_r.add(afternoon_3.get(rectangle));

            List<Integer> night_r = new ArrayList<Integer>();
            night_r.add(night_1.get(rectangle));
            night_r.add(night_2.get(rectangle));
            night_r.add(night_3.get(rectangle));


            ((MainActivity) getActivity()).dailyGraphModifier(morning_r, afternoon_r, night_r);
        }
    }

    @Override
    public void weekFinish(List<Integer> values, int selection){

        boolean ret = false;

        switch (selection) {
            case 1:
                for(int i = 1; i<7; i++) weekly_list.set(i, values.get(i-1));
                break;
            case 2:
                for(int i = 8; i<14; i++) weekly_list.set(i, values.get(i-8));
                break;
            case 3:
                for(int i = 15; i<21; i++) weekly_list.set(i, values.get(i-15));
                ret = true;
                break;
        }
        if(ret){
            polyList.get(rectangle).setFillColor(0xFFFFFFFF);
            builder12.dismiss();
            Log.i("disco123", "BEFORE weekly graph");
            weekly_list.set(0, morning_1.get(rectangle));
            weekly_list.set(7, afternoon_1.get(rectangle));
            weekly_list.set(14, night_1.get(rectangle));

            ((MainActivity) getActivity()).weeklyGraphModifier(weekly_list);
        }
    }

    @Override
    public void monthFinish(List<Integer> values, int selection) {

        boolean ret = false;

        switch (selection) {
            case 1:
                for (int i = 1; i < 30; i++) monthly_list.set(i, values.get(i - 1));
                break;
            case 2:
                for (int i = 31; i < 60; i++) monthly_list.set(i, values.get(i - 31));
                break;
            case 3:
                for (int i = 61; i < 90; i++) monthly_list.set(i, values.get(i - 61));
                ret = true;
                break;
        }
        if (ret) {
            polyList.get(rectangle).setFillColor(0xFFFFFFFF);
            builder12.dismiss();
            Log.i("disco123", "BEFORE monthly graph");
            monthly_list.set(0, morning_1.get(rectangle));
            monthly_list.set(30, afternoon_1.get(rectangle));
            monthly_list.set(60, night_1.get(rectangle));

            ((MainActivity) getActivity()).monthlyGraphModifier(monthly_list);
        }
    }

}