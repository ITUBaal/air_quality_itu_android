package com.example.emreinc.deneme1;

import android.os.AsyncTask;
import android.util.Log;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.Calendar;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoAdder extends AsyncTask<Double, Void, Void> {

    private Exception exception;
    private MongoClientURI uri;
    private MongoClient mongoClient;
    private MongoDatabase database2;
    private MongoCollection<Document> coll;

    private double latitude;
    private double longitude;
    private double ppm;

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
    protected Void doInBackground(Double... parameters) {

        Log.i("disco123", "mongo begins");
        latitude = parameters[0];
        longitude = parameters[1];
        ppm = parameters[2];


        Log.i("disco123", "mongo begins2");

        MongoClientURI uri = new MongoClientURI(
                "mongodb://emre:emre123@cluster0-shard-00-00-zmuwo.mongodb.net:27017,cluster0-shard-00-01-zmuwo.mongodb.net:27017,cluster0-shard-00-02-zmuwo.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");

        mongoClient = new MongoClient(uri);
        database2 = mongoClient.getDatabase("database1");
        coll = database2.getCollection("collection1");
        Log.e("deneme", "BACKGROUND YAPIYOR");
        Log.e("deneme", "POST EXEC ICI");

        // Dataya tarih bilgisi ekleniyor
        Calendar rightNow = Calendar.getInstance();
        rightNow.setLenient(false);
        //rightNow.get(Calendar.YEAR);
        int month  = rightNow.get(Calendar.MONTH);
        int week   = rightNow.get(Calendar.WEEK_OF_MONTH);
        int day    = rightNow.get(Calendar.DAY_OF_MONTH);
        int hour   = rightNow.get(Calendar.HOUR_OF_DAY);

        Document canvas = new Document("month", month)
                .append("week", week)
                .append("day", day)
                .append("hour", hour)
                .append("lat", latitude)
                .append("long", longitude)
                .append("value", ppm);

        coll.insertOne(canvas);


        FindIterable<Document> findIterable = coll.find(eq("gercek", true));

        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(final Document document) {
                Log.e("sonuc", document.toJson());
            }
        };
        findIterable.forEach(printBlock);

        try {
            //set time in mili
            Thread.sleep(1500);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void colors){
        Log.i("disco123", "Mongo successfully added.");
    }
}