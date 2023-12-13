package com.example.findlocation;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 2;
    Button btnManager,btnClient;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.start();

        textView = findViewById(R.id.txt);
        btnManager = findViewById(R.id.manager);
        btnClient = findViewById(R.id.client);

        btnManager.setOnClickListener(view -> openManagerActivity());
        btnClient.setOnClickListener(view -> openClientActivity());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        }
    }



    public void start(){

        @SuppressLint("SetTextI18n") LocationListener locationListener = location -> {

            Double acc =(double) location.getAccuracy();
            Log.d("GPS Accuracy", String.valueOf(acc));

            if (location.getAccuracy() > 6 || location.getAccuracy()==0){
                btnClient.setVisibility(View.VISIBLE);
                textView.setText("Now You are Indoor");

            }else {
                btnClient.setVisibility(View.GONE);
                textView.setText("You are Outdoor");
            }
        };

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0, locationListener);

    }
    public void openManagerActivity(){
        Intent intent = new Intent(this, ManagerActivity.class);
        startActivity(intent);
    }
    public void openClientActivity(){
        Intent intent = new Intent(this,ClientActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission
                    (this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        }
    }
}

