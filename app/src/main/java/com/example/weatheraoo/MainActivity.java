package com.example.weatheraoo;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    String key = "bd3781344d566d4bc40873a6423d9d99";
    EditText enterZip;
    Button button;
    String zip = "";
    TextView text;
    TextView longitude;
    TextView latitude;
    ImageView image;
    TextView temp;
    TextView time;
    TextView description;
    String lon = "";
    String lat = "";
    Spinner spinner;
    int hSelect = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterZip = findViewById(R.id.editZip);
        button = findViewById(R.id.butZip);
        text = findViewById(R.id.enterZip);
        longitude = findViewById(R.id.textViewLongitude);
        latitude = findViewById(R.id.textViewLatitude);
        image = findViewById(R.id.imageViewPicture);
        spinner = findViewById(R.id.spinnerSelectHour);
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<Integer>(this,R.layout.support_simple_spinner_dropdown_item,list);
        spinner.setAdapter(arrayAdapter);

        temp = findViewById(R.id.textViewTemp);
        time = findViewById(R.id.textViewTime);
        description = findViewById(R.id.textViewDescription);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Tag","Button Click");
                zip = enterZip.getText().toString();
                hSelect = (int) spinner.getSelectedItem();
                new async1().execute();
                new async2().execute();

            }
        });
    }
    private class async1 extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            String api = "https://api.openweathermap.org/geo/1.0/zip?zip="+zip+",US&appid=bd3781344d566d4bc40873a6423d9d99";
            Log.d("Tag",api);
            String result = "";
            JSONObject finalJson = new JSONObject();
            try {
                URL url = new URL(api);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream response = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                String line = br.readLine().toString();
                while(line!=null)
                {
                    result += line;
                    line = br.readLine();
                }
                JSONObject jsonObject = new JSONObject(result);
                finalJson = jsonObject;

            } catch (MalformedURLException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch1");
            } catch (IOException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("Tag",result);

            return finalJson;
        }
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {

                lon = jsonObject.get("lon").toString();
                lat = jsonObject.get("lat").toString();
                longitude.setText("Longitude: "+lon);
                latitude.setText("Latitude: "+lat);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class async2 extends AsyncTask<Void,Void,JSONObject>
    {
        int intialTime = 0;
        int timeOff = 0;

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray hourly = jsonObject.getJSONArray("hourly");
                if(hSelect!=4)
                {
                    temp.setText(hourly.getJSONObject(hSelect).get("temp").toString());
                    Log.d("Tag",temp.toString());
                    description.setText(hourly.getJSONObject(hSelect).getJSONArray("weather").get("description").toString());
                    Log.d("Tag",description.toString());
                    intialTime = (Integer)hourly.getJSONObject(hSelect).get("dt");
                    timeOff = (Integer)jsonObject.get("timezone_offset");
                    intialTime = intialTime - timeOff;
                    Date date = new java.util.Date(intialTime*1000L);
                    SimpleDateFormat d = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    String nDate = d.format(date);
                    time.setText(nDate);
                    Log.d("Tag",time.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag",e.toString());
            }
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            String exclude = "minutly,daily,current";
            Log.d("Tag","Lat: "+lat+" Lon: "+lon);
            String api = "https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude="+exclude+"&appid=bd3781344d566d4bc40873a6423d9d99&units=imperial";
            Log.d("Tag",api);
            String result = "";
            JSONObject myJson = new JSONObject();
            try {
                URL url = new URL(api);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream response = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                String line = br.readLine().toString();
                while(line!=null)
                {
                    result += line;
                    line = br.readLine();
                }
                JSONObject jsonObject = new JSONObject(result);
                myJson = jsonObject;

            } catch (MalformedURLException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch3");
            } catch (IOException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch4");
            } catch (JSONException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch5");
            }
            Log.d("Tag",result);
            return myJson;



        }
    }
}