package com.example.weatheraoo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
public class MainActivity extends AppCompatActivity {

    String key = "bd3781344d566d4bc40873a6423d9d99";
    EditText enterZip;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enterZip = findViewById(R.id.enterZip);
        button = findViewById(R.id.button);
        String zip = "";
        String api = "http://api.openweathermap.org/geo/1.0/zip?zip="+zip+",US&appid=bd3781344d566d4bc40873a6423d9d99";
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    URL url = new URL(api);
                    URLConnection connection = url.openConnection();
                    InputStream response = connection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(response));
                    String line = br.readLine();
                    String result = "";
                    while(line!=null)
                    {
                        result += line;
                        line = br.readLine();
                    }
                    System.out.println(result);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }
}