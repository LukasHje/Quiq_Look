package com.example.lab5;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {

    private static final String TAG = "ListDataActivity";

    DatabaseHelper myLittleDatabaseHelper;
    private ListView spottedPlatesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datalist);
        myLittleDatabaseHelper = new DatabaseHelper(this);
        spottedPlatesList = findViewById(R.id.data_listview);

        fillListWithData();
    }

    private void fillListWithData() {
        Log.d(TAG, "fillListWithData: Displaying data in the ListView.");

        //get the data and append to a list
        Cursor data = myLittleDatabaseHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            String plateObservation = data.getString(0) + ", " + data.getString(1) + ", " + data.getString(2);

            listData.add(plateObservation);
        }
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        spottedPlatesList.setAdapter(adapter);
    }

    //Used too create and inflate the options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Used to direct the user to selected activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}