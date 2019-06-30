package com.example.mohammadehatesham.memorableplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Intent intent;

    static ArrayList<String> places = new ArrayList<String>();

    static ArrayList<LatLng> locations = new ArrayList<LatLng>();

    static ArrayAdapter arrayAdapter;

    SharedPreferences sharedPreferences;

    ArrayList<String> latitutes = new ArrayList<>();
    ArrayList<String> longitutes = new ArrayList<>();

    public void newPlace(int position){
        intent = new Intent(getApplicationContext(),MapsActivity.class);
        intent.putExtra("placeNumber",position);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.clear:


                if (places.size() == 1) {
                    Toast.makeText(this, "No place available to clear !", Toast.LENGTH_SHORT).show();
                } else {

                    places.clear();
                    latitutes.clear();
                    longitutes.clear();
                    locations.clear();
                    arrayAdapter.clear();
                }
                try {
                    sharedPreferences.edit().putString("places", ObjectSerializer.serialize(places)).apply();
                    sharedPreferences.edit().putString("lat", ObjectSerializer.serialize(latitutes)).apply();
                    sharedPreferences.edit().putString("long", ObjectSerializer.serialize(longitutes)).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;

            case R.id.exit:

                finish();

                System.exit(0);

                return true;

            case R.id.place:

                newPlace(places.size()+1);

                return true;

            default:

                return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.mohammadehatesham.memorableplaces", Context.MODE_PRIVATE);


        places.clear();
        latitutes.clear();
        longitutes.clear();
        locations.clear();

        try{
            places = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("places",ObjectSerializer.serialize(new ArrayList<>())));
            latitutes = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("lat",ObjectSerializer.serialize(new ArrayList<>())));
            longitutes = (ArrayList) ObjectSerializer.deserialize(sharedPreferences.getString("long",ObjectSerializer.serialize(new ArrayList<>())));
        }catch (Exception e){
            e.printStackTrace();
        }

        if(places.size()>0 && latitutes.size()>0 && longitutes.size()>0){
            if(places.size()==latitutes.size() && places.size()==longitutes.size()){
                for(int i=0;i<places.size();i++){
                    locations.add(new LatLng(Double.parseDouble(latitutes.get(i)), Double.parseDouble(longitutes.get(i))));
                }
            }
        }else{
            places.add("Add a new place...");

            locations.add(new LatLng(0,0));
        }

        ListView listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,places);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               newPlace(position);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                   new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Are your sure !")
                            .setMessage("Do you wanna delete this location ?")
                            .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    places.remove(position);
                                    locations.remove(position);
                                    arrayAdapter.notifyDataSetChanged();

                                    latitutes.clear();
                                    longitutes.clear();

                                    try{

                                        for(LatLng coords : locations){
                                            latitutes.add(Double.toString(coords.latitude));
                                            longitutes.add(Double.toString(coords.longitude));
                                        }
                                        sharedPreferences.edit().putString("places",ObjectSerializer.serialize(places)).apply();
                                        sharedPreferences.edit().putString("lat",ObjectSerializer.serialize(latitutes)).apply();
                                        sharedPreferences.edit().putString("long",ObjectSerializer.serialize(longitutes)).apply();
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton("no",null).show();
                return true;
            }
        });
            }
        }

