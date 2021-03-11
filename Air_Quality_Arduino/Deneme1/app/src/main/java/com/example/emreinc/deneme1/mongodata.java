package com.example.emreinc.deneme1;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.PolygonOptions;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;
import static java.util.Collections.singletonList;

public class mongodata extends AsyncTask<List<PolygonOptions>, Void, List<Integer>> {


    private AsyncResponse delegate;
    private int year;
    private int month;
    private int day;
    private int daytime;

    private int average;
    private int count;

    private int bot;
    private int top;

    int lightGreen = 0x6600FF00;
    int darkGreen  = 0x66009900;
    int yellow     = 0x66FFC000;
    int orange     = 0x66FF6600;
    int red        = 0x66CC0000;
    int grey       = 0x44000000;

    public mongodata(AsyncResponse listener, int year, int month, int day, int daytime){
        this.delegate=listener;
        this.year = year;
        this.month = month;
        this.day = day;
        this.daytime = daytime;
    }

    List<Integer> colors = new ArrayList<Integer>();
    List<Integer> values = new ArrayList<Integer>();


    private Exception exception;
    private MongoClientURI uri;
    private MongoClient mongoClient;
    private MongoDatabase database2;
    private MongoCollection<Document> coll;



    public int addtoDatabase(String... stringler){

        Log.i("disco123", "addtoDatabase");
        String s = stringler[0];
        int val = 1;
        try {
            val = Integer.parseInt(stringler[1]);
            Log.i("disco123", "parsed");
        } catch(NumberFormatException nfe) {
            Log.i("disco123", "Damn hata varrrr");
            return 0;
        }
        Document canvas = new Document("name", stringler[0])
                .append("value", val);
        coll.insertOne(canvas);

        return 0;
    }

    @Override
    protected List<Integer> doInBackground(List<PolygonOptions>... optList) {

            MongoClientURI uri = new MongoClientURI(
                    "mongodb://emre:emre123@cluster0-shard-00-00-zmuwo.mongodb.net:27017,cluster0-shard-00-01-zmuwo.mongodb.net:27017,cluster0-shard-00-02-zmuwo.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");

            mongoClient = new MongoClient(uri);
            database2 = mongoClient.getDatabase("database1");
            coll = database2.getCollection("collection1");
            Log.e("disco123", "BAGLANDI");

            switch (daytime){
                case 1: bot = 8;    // morning
                        top = 13;
                        break;
                case 2: bot = 13;   // afternoon
                        top = 18;
                        break;
                case 3: bot = 18;   // night
                        top = 23;
                        break;
                default:bot = 0;    // none
                        top = 0;
                        break;
            }

            for(int i = 0; i<96; i++) {
                double l1 = optList[0].get(i).getPoints().get(0).latitude;
                double l2 = optList[0].get(i).getPoints().get(2).latitude;
                double l3 = optList[0].get(i).getPoints().get(0).longitude;
                double l4 = optList[0].get(i).getPoints().get(2).longitude;
                FindIterable<Document> findIterable = coll.find(and(eq("week", 3),
                        eq("month", 4),
                        eq("day", 14),
                        gte("hour", bot),   // daytime
                        lt("hour", top),
                        gte("lat", l1),     // latitude, longitude
                        lt("lat", l2),
                        gte("long", l3),
                        lt("long", l4)));

                average = 0;
                count = 0;

                Block<Document> airQuality = new Block<Document>() {
                    @Override
                    public void apply(final Document document) {
                        //Log.e("sonuc", document.toJson());
                        //JSONArray arr = new JSONArray(document);
                        try {
                            JSONObject jObj = new JSONObject(document.toJson());
                            String date = jObj.getString("value");
                            double val = Double.parseDouble(date);
                            count++;
                            average += val;
                            //Log.e("disco123", date);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Log.e("disco123", "Value couldnt be found.");
                        }
                    }
                };
                findIterable.forEach(airQuality);

                if(count!=0) average = average / count;

                int color = 0;

                if (average == 0) color = grey;
                else if (average < 800) color = lightGreen;
                else if (average < 1000) color = darkGreen;
                else if (average < 1250) color = yellow;
                else if (average < 1450) color = orange;
                else color = red;
                //Log.e("disco123", "Color: " + color);

                values.add(average);
                colors.add(color);

            }

            return colors;
    }

    @Override
    protected void onPostExecute(List<Integer> colors){

        //Log.i("disco123", "Mongo successfully added.");
        delegate.dateFinish(colors, daytime, values);

        //Log.i("disco123", "Mongo successfully added.2");
    }

}
