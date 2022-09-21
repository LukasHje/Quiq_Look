package com.example.lab5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {

    DatabaseHelper myLittleDatabaseHelper;
    private int plateNumber;
    Button spotPlate;
    ImageButton loglist;
    TextView spottedPlate;
    Location network_loc, gps_loc, final_loc;
    double latitude, longitude;
    String userCountry, userAddress;
    String userLocation, currentDatetime; //these are passed to the database, together with the plateNumber, and are presented in the loglistActivity listView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spotPlate = findViewById(R.id.spot_plate);
        spottedPlate = findViewById(R.id.spotted_plate);
        loglist = findViewById(R.id.logButton);
        plateNumber = 0;
        myLittleDatabaseHelper = new DatabaseHelper(this);


        loadData();

        spotPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plateNumber ++;

                //update plate, get&set user location & set datetime
                updateSpottedPlate();
                plateSpottedLocation();
                plateSpottedDatetime();

                saveData();
                addDataToDB(plateNumber, currentDatetime, userLocation);
            }
        });

        loglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void addDataToDB(int platenumber, String datetime, String location) {
        boolean insertData = myLittleDatabaseHelper.addData(platenumber, datetime, location);

        if (insertData) {
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT);
        }
    }

    private void plateSpottedDatetime() {
        LocalDateTime dateObject = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateObject = LocalDateTime.now();
        }
        DateTimeFormatter dateTimeFormatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentDatetime = dateObject.format(dateTimeFormatter);
        }
    }

    //Crazy complex method to get userLocation
    private void plateSpottedLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {

            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longitude = 0.0;
        }


        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);

        try {

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
/*            if (addresses != null && addresses.size() > 0) {
                userCountry = addresses.get(0).getCountryName();
                userAddress = addresses.get(0).getAddressLine(0);
                userLocation = userCountry + ", " + userAddress;
            }
            else {
                userCountry = "Unknown";
                userLocation = userCountry;
            }*/

            userLocation = "[Österväg 3 C, 621 45, Visby]";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSpottedPlate() {
        String spottedNumber = Integer.toString(plateNumber);

        if(plateNumber < 10 && plateNumber > 0) {
            spottedPlate.setText("00" + spottedNumber);
        } else if(plateNumber < 100 && plateNumber > 9) {
            spottedPlate.setText("0" + spottedNumber);
        } else if(plateNumber < 1000 && plateNumber > 99) {
            spottedPlate.setText(spottedNumber);
        } else {
            spottedPlate.setText("000");
            plateNumber = 0;
        }
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("plate_number", plateNumber);
        editor.apply();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        int loadValue = sharedPreferences.getInt("plate_number", 0);
        plateNumber = loadValue;
        updateSpottedPlate();
    }

}