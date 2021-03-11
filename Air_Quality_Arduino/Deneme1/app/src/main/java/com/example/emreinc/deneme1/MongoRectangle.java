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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lt;

public class MongoRectangle extends AsyncTask<List<PolygonOptions>, Void, List<Integer>> {

    private AsyncResponse delegate;
    private int year;
    private int month;
    private int day;
    private int daytime;
    private int rectNo;
    private int choice;

    private int average;
    private int count;

    private int bot;
    private int top;


    public MongoRectangle(AsyncResponse listener, int year, int month, int day, int daytime, int rectNo, int choice){
        this.delegate=listener;
        this.year = year;
        this.month = month;
        this.day = day;
        this.daytime = daytime;
        this.rectNo = rectNo;
        this.choice = choice;
    }

    List<Integer> values = new ArrayList<Integer>();

    private Exception exception;
    private MongoClientURI uri;
    private MongoClient mongoClient;
    private MongoDatabase database2;
    private MongoCollection<Document> coll;


    @Override
    protected List<Integer> doInBackground(List<PolygonOptions>... optList) {

        MongoClientURI uri = new MongoClientURI(
                "mongodb://emre:emre123@cluster0-shard-00-00-zmuwo.mongodb.net:27017,cluster0-shard-00-01-zmuwo.mongodb.net:27017,cluster0-shard-00-02-zmuwo.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");

        mongoClient = new MongoClient(uri);
        database2 = mongoClient.getDatabase("database1");
        coll = database2.getCollection("collection1");

        switch (daytime) {
            case 1:
                bot = 8;    // morning
                top = 13;
                break;
            case 2:
                bot = 13;   // afternoon
                top = 18;
                break;
            case 3:
                bot = 18;   // night
                top = 23;
                break;
        }

        int k;
        if(choice == 1) k = 3;
        else if (choice == 2) k = 7;
        else k = 30;

        for(int i = 1; i<k; i++) {
            day = day - i;
            double l1 = optList[0].get(rectNo).getPoints().get(0).latitude;
            double l2 = optList[0].get(rectNo).getPoints().get(2).latitude;
            double l3 = optList[0].get(rectNo).getPoints().get(0).longitude;
            double l4 = optList[0].get(rectNo).getPoints().get(2).longitude;
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

            if (count != 0) average = average / count;

            values.add(average);


            if(daytime==3){
                try {
                    //set time in mili
                    Thread.sleep(500);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return values;
    }

    @Override
    protected void onPostExecute(List<Integer> values){

        //Log.i("disco123", "Mongo successfully added.");
        if(choice==1)delegate.dayFinish(values, daytime);
        else if(choice==2) delegate.weekFinish(values, daytime);
        else delegate.monthFinish(values, daytime);

        //Log.i("disco123", "Mongo successfully added.2");
    }
}
