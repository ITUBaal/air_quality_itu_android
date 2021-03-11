package com.example.emreinc.deneme1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class CollectorActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    public String[] tomongo;

    public static String EXTRA_ADDRESS = "device_address";

    ArrayList bluelist = new ArrayList();

    Button button1, button2;
    EditText editText1;
    TextView textView1;
    LocationManager locationManager;

    double latitude = 0;
    double longitude = 0;

    String ppm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collector);

        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        button1 = (Button) findViewById(R.id.button_connect);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new ConnectBT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                Log.i("disco123", "Connect Clicked!");
            }
        });

        button2 = (Button) findViewById(R.id.button_upload);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // new ConnectBT().execute();
                Log.i("disco123", "Upload Clicked!");

                Log.i("disco123", latitude + "  " + longitude);
                if(ppm != null)
                    uploadAlert();
            }
        });

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        //textView1.fullScroll(View.FOCUS_DOWN);

        bluetoothHandler();
        locationCheck();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, mLocationListener);
        Location loc1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //longitude = loc1.getLongitude();
        //latitude = loc1.getLatitude();


    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i("disco1234", location.getLatitude() + "  " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void uploadAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Confirm the Upload");
        builder1.setMessage("Are you sure you want to upload the data to MongoDatabase?\nDo all data seem logical?" +
                "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\nPPM: " + ppm);
        // Set up the buttons
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mongo function
                Log.i("disco123", "Upload done!");
                mongoCaller();
            }
        });
        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder1.show();
    }

    private void mongoCaller() {
            // call mongo1.execute with arguments
        //new MongoAdder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, latitude,longitude,Double.parseDouble(ppm));

        new MongoAdder().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 100.0,1000.0,100.0);

    }

    private void bluetoothHandler() {   // opens the bluetooth

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        int REQUEST_ENABLE_BT = 1;

        if(myBluetooth == null)
        {
            //Show a mensag. that the device has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();

            //finish apk
            finish();
        }
        if (!myBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                //Log.i("disco123", "+" + deviceName + "+" + deviceHardwareAddress);
                bluelist.add(deviceName + "\n" + deviceHardwareAddress);
                if(deviceName.equals("HC-05")){
                    EXTRA_ADDRESS = deviceHardwareAddress;
                    Log.i("disco123", deviceHardwareAddress);
                }
            }
        }

        if (myBluetooth.isDiscovering()) {
            Log.i("disco123", "123123");
            myBluetooth.cancelDiscovery();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // secenekleri bara eklemek
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_second, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return true;
    }


    private class ConnectBT extends AsyncTask<Void, String, Integer>  // UI thread
    {
        private boolean ConnectSuccess = true;

        protected void onPreExecute()
        {
            progress = ProgressDialog.show(CollectorActivity.this, "Connecting...", "Please wait.");  //show a progress dialog
        }


        @Override
        protected Integer doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(EXTRA_ADDRESS);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    Log.i("disco123", "basarili baglanti");
                    progress.dismiss();

                    /*  Yazmak için
                    btSocket.getOutputStream().write("CD".toString().getBytes());
                    btSocket.getOutputStream().write("EF".toString().getBytes());
                    btSocket.getOutputStream().write("GH".toString().getBytes());
                    */

                    //start loop
                    while(true) {
                        btSocket.getOutputStream().write("CD".toString().getBytes());
                        try {
                            //set time in mili
                            Thread.sleep(5000);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        InputStream inputStream = btSocket.getInputStream();
                        byte[] buffer = new byte[4096];
                        int len = inputStream.read(buffer);
                        String readMessage = new String(buffer, 0, len);
                        Log.i("disco123", "Message :: " + readMessage);

                        tomongo = readMessage.split(":");
                        tomongo = tomongo[1].split(":"); //tomongo[0] has the ppm value

                        publishProgress(tomongo[0]);
                        Log.i("disco123", tomongo[0]);
                        textView1.append(tomongo[0] + " ppm\n");
                        //end loop
                    }

                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
                Log.i("disco123", "bok");
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(String... devices) //while the progress dialog is shown, the connection is done in background
        {
            ppm = devices[0];
        }

        @Override
        protected void onPostExecute(Integer devices) //while the progress dialog is shown, the connection is done in background
        {
            Log.i("disco123", "bluetooth ends here");
            if (myBluetooth.isDiscovering()) {
                Log.i("disco123", "123123");
                myBluetooth.cancelDiscovery();
            }
        }
    }

    public void locationCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
