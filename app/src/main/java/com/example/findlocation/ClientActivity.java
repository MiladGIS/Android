package com.example.findlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClientActivity extends AppCompatActivity {

    WifiManager wifiManager;
    DBManager helper;
    TextView infotext;
    ArrayList<String> yourLocation = new ArrayList<>();
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        infotext = findViewById(R.id.txt);
        helper = new DBManager(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        }

        // We need to use this Handler package

        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                // Do something here on the main thread
                ArrayList<String> arrayList;
                arrayList = new ArrayList<>();
                List<ScanResult> Results= wifiManager.getScanResults();
                if(Results.size()>0){
                    for(ScanResult ScanResult : Results){
                        arrayList.add(ScanResult.SSID);
                        arrayList.add(ScanResult.BSSID);
                        arrayList.add(ScanResult.level * -1 + "");
                    }
                }
                else{
                    infotext.setText("No WiFi scanned");
                }

                ArrayList<String> mArrayList = new ArrayList<>();
                ArrayList<ArrayList<String>> wifiList = new ArrayList<>(); //[[]]
                for (int i = 0; i < arrayList.size(); i = i + 3){
                    Cursor res = helper.fetchData(arrayList.get(i + 1));
                    if (res.getCount() == 0){
                        infotext.setText("No Result");


                    } else {
                        res.moveToFirst();
                        while (!res.isAfterLast()) {
                            mArrayList.add(res.getString(res.getColumnIndex("_id")));
                            mArrayList.add(res.getString(res.getColumnIndex("Info")));
                            mArrayList.add(res.getString(res.getColumnIndex("Xc")));
                            mArrayList.add(res.getString(res.getColumnIndex("Yc")));
                            mArrayList.add(res.getString(res.getColumnIndex("SSID")));
                            mArrayList.add(res.getString(res.getColumnIndex("BSSID")));
                            mArrayList.add(res.getString(res.getColumnIndex("RSS")));//add the item
                            res.moveToNext();
                        }
                    }
                }

                if (mArrayList.size() > 0){
                    for (int i = 0; i < arrayList.size(); i = i + 3){
                        for (int j = 0; j < mArrayList.size(); j = j + 7) {
                            if ((arrayList.get(i + 1).equals(mArrayList.get(j + 5))) &&
                                    (Integer.parseInt(mArrayList.get(j + 6)) - 10) < Integer.parseInt(arrayList.get(i + 2)) &&
                                    Integer.parseInt(arrayList.get(i + 2)) < (Integer.parseInt(mArrayList.get(j + 6)) + 10)){
                                ArrayList<String> near = new ArrayList<>();//[[]] middle bracket
                                near.add(mArrayList.get(j + 2));
                                near.add(mArrayList.get(j + 3));
                                wifiList.add(near);//[[]]
                            }
                        }
                    }

                    countFrequencies(wifiList);
                    for (int i = 0; i < mArrayList.size(); i = i + 7){
                        if(mArrayList.get(i + 2).equals(yourLocation.get(0))
                                && mArrayList.get(i + 3).equals(yourLocation.get(1))){
                            infotext.setText("Description of Your Location: " + "\n" + mArrayList.get(i + 1) +"\n"
                                    +"X Coordinate of your position: " + yourLocation.get(0) + "\n"
                                    +"Y Coordinate of your position: " + yourLocation.get(1));
                        }
                    }
                }else{
                    infotext.setText("No result check your wifi and location to be turned on");
                }
                Log.d("Handlers", "Called on main thread");
                // Repeat this the same runnable code block again another 2 seconds
                // 'this' is referencing the Runnable object
                handler.postDelayed(this, 300);
            }
        };
// Start the initial runnable task by posting through the handler
        handler.post(runnableCode);
     }


    public void countFrequencies(ArrayList<ArrayList<String>> list){
        ArrayList<Integer> occur = new ArrayList<>();
        Map<ArrayList<String>, Integer> hm = new HashMap<>();
        for (ArrayList<String> i : list) {
            Integer j = hm.get(i);
            hm.put(i, (j == null) ? 1 : j + 1);
        }
        for (Map.Entry<ArrayList<String>, Integer> val : hm.entrySet()) {
            occur.add(val.getValue());
        }
        yourLocation = getKey(hm, Collections.max(occur));
    }

    public <V, K> K getKey(Map<K, V> map, V value){
        for (Map.Entry<K, V> entry: map.entrySet()){
            if (value.equals(entry.getValue())){
                return entry.getKey();
            }
        }
        return null;
    }

}
