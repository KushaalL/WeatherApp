package com.example.weatheraoo;

import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity
{
    String key = "bd3781344d566d4bc40873a6423d9d99";
    EditText enterZip;
    Button button;
    String zip = "";
    TextView text;
    TextView longitude;
    TextView latitude;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    TextView text1;
    TextView text2;
    TextView text3;
    TextView text4;
    String lon = "";
    String lat = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterZip = findViewById(R.id.editZip);
        button = findViewById(R.id.butZip);
        text = findViewById(R.id.enterZip);
        longitude = findViewById(R.id.textViewLongitude);
        latitude = findViewById(R.id.textViewLatitude);
        image1 = findViewById(R.id.imageViewH1);
        image2 = findViewById(R.id.imageViewH2);
        image3 = findViewById(R.id.imageViewH3);
        image4 = findViewById(R.id.imageViewH4);
        text1 = findViewById(R.id.textViewH1);
        text2 = findViewById(R.id.textViewH2);
        text3 = findViewById(R.id.textViewH3);
        text4 = findViewById(R.id.textViewH4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Tag","Button Click");
                zip = enterZip.getText().toString();
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
        @Override
        protected JSONObject doInBackground(Void... voids) {
            String api = "https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude={part}&appid=bd3781344d566d4bc40873a6423d9d99";
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
                JSONArray array = new JSONArray();
                array.put(myJson);
                Log.d("Tag", "Array: "+array);

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
            return null;
        }
    }
}