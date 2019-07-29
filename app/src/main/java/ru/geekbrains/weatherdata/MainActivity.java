package ru.geekbrains.weatherdata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.weatherdata.model.WeatherRequest;

public class MainActivity extends AppCompatActivity {

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Moscow,RU&appid=240af58b6f095eb759a3ecd2d282d448";

    private EditText city;
    private EditText temp;
    private EditText press;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        city = findViewById(R.id.editCityName);
        temp = findViewById(R.id.editTemp);
        press = findViewById(R.id.editPress);

        Button refresh = findViewById(R.id.button);
        refresh.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                final URL uri = new URL(WEATHER_URL);
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            HttpsURLConnection urlConnection;
                            urlConnection = (HttpsURLConnection) uri.openConnection();
                            urlConnection.setRequestMethod("GET");
                            urlConnection.setReadTimeout(10000);
                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            final String result = in.lines().collect(Collectors.joining("\n"));
                            Gson gson = new Gson();
                            final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    city.setText(weatherRequest.getName());
                                    temp.setText(String.format("%.2f C°",weatherRequest.getMain().getTemp()));
                                    press.setText(((Integer)weatherRequest.getMain().getPressure()).toString());
                                }
                            });

                        } catch (ProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    };
}
