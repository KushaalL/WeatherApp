package com.example.weatheraoo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.example.weatheraoo.databinding.ActivityMainBinding;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    String zip = "";
    String lon = "";
    String lat = "";
    int i = 0;
    String iconH1 = "";
    String iconH2 = "";
    String iconH3 = "";
    String iconH4 = "";
    ArrayList<String> iconList = new ArrayList<String>(Arrays.asList(iconH1,iconH2,iconH3,iconH4));
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        binding.butZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Tag","Button Click");
                zip = binding.editZip.getText().toString();
                i--;
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
                binding.textViewLongitude.setText("Longitude: "+lon);
                binding.textViewLatitude.setText("Latitude: "+lat);
                binding.textViewLocation.setText("Location "+jsonObject.get("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class async2 extends AsyncTask<Void,Void,JSONObject>
    {
        int epoch = 0;
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
                JSONArray hourly = jsonObject.getJSONArray("hourly");
                ArrayList<TextView> timeList = new ArrayList<TextView>(Arrays.asList(binding.TimeH1,binding.TimeH2,binding.TimeH3,binding.TimeH4));
                ArrayList<TextView> tempList = new ArrayList<TextView>(Arrays.asList(binding.TempH1,binding.TempH2,binding.TempH3,binding.TempH4));
                ArrayList<TextView> desList = new ArrayList<TextView>(Arrays.asList(binding.DesH1,binding.DesH2,binding.DesH3,binding.DesH4));
                for(int i = 0;i<4;i++)
                {
                    tempList.get(i).setText(hourly.getJSONObject(i).get("temp").toString()+"°F");
                    desList.get(i).setText(hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
                    epoch = (Integer)hourly.getJSONObject(i).get("dt");
                    Log.d("Tag", String.valueOf(epoch));
                    Date date = new java.util.Date(epoch*1000L);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
                    String formattedDate = sdf.format(date);
                    timeList.get(i).setText(formattedDate);
                    iconList.set(i, (hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag",e.toString());
            }
            new async3().execute();
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            String exclude = "";
            Log.d("Tag","Lat: "+lat+" Lon: "+lon);
            String api = "https://api.openweathermap.org/data/2.5/onecall?lat="+lat+"&lon="+lon+"&exclude="
                    +exclude+"&appid=bd3781344d566d4bc40873a6423d9d99&units=imperial";
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
    private class async3 extends AsyncTask<Void, Void, Bitmap>
    {
        ArrayList<ImageView> imgList = new ArrayList<ImageView>(Arrays.asList(binding.imageH1,binding.imageH2,binding.imageH3,binding.imageH4));
        Bitmap b1;
        Bitmap b2;
        Bitmap b3;
        Bitmap b4;
        ArrayList<Bitmap> bitMapList  = new ArrayList<Bitmap>(Arrays.asList(b1,b2,b3,b4));
        @Override
        protected Bitmap doInBackground(Void... voids) {
            InputStream response = null;
            try {
                for(int i = 0;i<4;i++)
                {
                    String icon = iconList.get(i);
                    String iconAPI = "https://openweathermap.org/img/wn/"+icon+".png";
                    System.out.println(iconAPI);
                    URL url = new URL(iconAPI);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    response = connection.getInputStream();
                    bitMapList.set(i,BitmapFactory.decodeStream(response));
                }
            } catch (MalformedURLException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch K");
            } catch (IOException e) {
                Log.d("Tag",e.toString());
                Log.d("Tag","Catch L");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            for(int i = 0;i<4;i++)
            {
                imgList.get(i).setImageBitmap(bitMapList.get(i));
            }

        }
    }


}