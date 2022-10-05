package com.example.weatheraoo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.example.weatheraoo.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
public class MainActivity extends AppCompatActivity
{
    String zip = "";
    String lon = "";
    String lat = "";
    String iconH1 = "";
    String iconH2 = "";
    String iconH3 = "";
    String iconH4 = "";
    String key = "";
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
                zip = binding.editZip.getText().toString();
                new async1().execute();
                new async2().execute();
            }
        });
    }
    private class async1 extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            Api key = new Api(zip);
            String api = key.getKey();
            String result = "";
            JSONObject finalJson = new JSONObject();
            try {
                URL url = new URL(api);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream response = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(response));
                String line = br.readLine();
                while(line!=null)
                {
                    result += line;
                    line = br.readLine();
                }
                JSONObject jsonObject = new JSONObject(result);
                finalJson = jsonObject;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("Tag","Catch1");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("Tag","Catch2");
                binding.textViewLatitude.setText("Invalid");
                binding.textViewLongitude.setText("ZipCode");
                binding.textViewLocation.setText("Use Valid ZipCode");
                binding.textViewLocation.setGravity(Gravity.CENTER_HORIZONTAL);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("Tag","Catch3");
            }
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
                binding.textViewLocation.setText("Location: "+jsonObject.get("name"));
                binding.textViewLocation.setGravity(Gravity.CENTER_HORIZONTAL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private class async2 extends AsyncTask<Void,Void,JSONObject>
    {
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
                    tempList.get(i).setText(hourly.getJSONObject(i).get("temp")+"°F");
                    tempList.get(i).setGravity(Gravity.CENTER);
                    desList.get(i).setText(hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("description"));
                    desList.get(i).setGravity(Gravity.CENTER);
                    int epoch = (Integer)hourly.getJSONObject(i).get("dt");
                    Date date = new java.util.Date(epoch*1000L);
                    SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
                    String formattedDate = sdf.format(date);
                    timeList.get(i).setText(formattedDate);
                    timeList.get(i).setGravity(Gravity.CENTER);
                    iconList.set(i, (hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new async3().execute();
        }
        @Override
        protected JSONObject doInBackground(Void... voids) {
            String exclude = "";
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
                String line = br.readLine();
                while(line!=null)
                {
                    result += line;
                    line = br.readLine();
                }
                JSONObject jsonObject = new JSONObject(result);
                myJson = jsonObject;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return myJson;
        }
    }
    private class async3 extends AsyncTask<Void, Void, Bitmap>
    {
        ArrayList<ImageView> imgList = new ArrayList<ImageView>(Arrays.asList(binding.imageH1,binding.imageH2,binding.imageH3,binding.imageH4));
        Bitmap b1,b2,b3,b4;
        ArrayList<Bitmap> bitMapList  = new ArrayList<Bitmap>(Arrays.asList(b1,b2,b3,b4));
        @Override
        protected Bitmap doInBackground(Void... voids) {
            InputStream response;
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
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            for(int i = 0;i<4;i++)
                imgList.get(i).setImageBitmap(bitMapList.get(i));
        }
    }
}