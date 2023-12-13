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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends AppCompatActivity {

    EditText cordY, cordX, infotxt;
    TextView wifitxt;
    WifiManager wifiManager;
    DBManager helper;
    ArrayList<String> wifiList = new ArrayList<>();
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        cordX = findViewById(R.id.editX);
        cordY = findViewById(R.id.editY);
        infotxt = findViewById(R.id.locName);
        wifitxt = findViewById(R.id.wifitxt);
        helper = new DBManager(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
    }


    @SuppressLint("SetTextI18n")
    public void scanWiFi(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        }

        ArrayList<String> arrayList = new ArrayList<>();
        List<ScanResult> Results= wifiManager.getScanResults();
        if(Results.size()>0){
            for(ScanResult ScanResult : Results){
                arrayList.add(ScanResult.SSID);
                arrayList.add(ScanResult.BSSID);
                arrayList.add(ScanResult.level * -1 + "");
            }
            wifiList = arrayList;
            wifitxt.setText(arrayList.toString());
        }
        else{
            wifitxt.setText("Wifi not scanned check your wifi and location to be turned on");
        }
    }

    public void addData(View view) {
        String info = infotxt.getText().toString();
        String tx = cordX.getText().toString();
        String ty = cordY.getText().toString();
        if(info.isEmpty() || tx.isEmpty() || ty.isEmpty() || wifiList.isEmpty()) {
            Toast.makeText(ManagerActivity.this,"Scan WiFi and fill all inputs",Toast.LENGTH_LONG).show();
        } else {
            for (int i = 0; i < wifiList.size(); i = i + 3){
                long id = helper.insertData(info, tx, ty, wifiList.get(i), wifiList.get(i + 1), wifiList.get(i + 2));
                if(id<=0) {
                    Toast.makeText(ManagerActivity.this,"Insertion Unsuccessful",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ManagerActivity.this,"Insertion Successful",Toast.LENGTH_LONG).show();
                }
                cordX.setText("");
                cordY.setText("");
                infotxt.setText("");
            }
        }
    }

    public void showData(View view){
        Cursor res = helper.fetch();
        if(res.getCount() == 0){
            Toast.makeText(ManagerActivity.this,"No Entry Exist",Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuilder buffer = new StringBuilder();
        while (res.moveToNext()){
            buffer.append("ID :").append(res.getString(0)).append("\n");
            buffer.append("Coordinate Description :").append(res.getString(1)).append("\n");
            buffer.append("X Coordinate :").append(res.getString(2)).append("\n");
            buffer.append("Y Coordiante :").append(res.getString(3)).append("\n");
            buffer.append("Access Point Name :").append(res.getString(4)).append("\n");
            buffer.append("Router MAC :").append(res.getString(5)).append("\n");
            buffer.append("Received Signal Strength :").append(res.getString(6)).append("\n\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ManagerActivity.this);
        builder.setCancelable(true);
        builder.setTitle("FingerPrint Info");
        builder.setMessage(buffer.toString());
        builder.show();
    }


    public void onDelete(View view){
        String tx = cordX.getText().toString();
        String ty = cordY.getText().toString();

        if(tx.isEmpty() || ty.isEmpty()) {
            Toast.makeText(ManagerActivity.this,"Enter both Coordinates to Delete",Toast.LENGTH_LONG).show();
        } else {
            Boolean checkdeletedata = helper.deletedata(tx, ty);
            if (checkdeletedata)
                Toast.makeText(ManagerActivity.this, "Entry Deleted",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(ManagerActivity.this, "There is no such entry or con not be deleted",Toast.LENGTH_LONG).show();
        }
        cordX.setText("");
        cordY.setText("");
    }

}

