package edu.umb.cs443;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mFragment=((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mFragment.getMapAsync(this);

        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getWeatherInfo();
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getWeatherInfo() {
        boolean isZip;
        EditText searchBar = findViewById(R.id.search_bar);
        TextView tempText = findViewById(R.id.temperatureTxt);
        ImageView iconForecast = findViewById(R.id.forecastImage);

        String city = searchBar.getText().toString();

        //two input are 02045,us or hull,us so split on the comma
        String[] inputs = city.split("[,]", 0);

        //regex to match the zip
        String zipCodePattern = "\\d{5}(-\\d{4})?";
        isZip = inputs[0].matches(zipCodePattern);

        String urlQ;

        //change the query depending on the input
        if(isZip){
            urlQ = "https://api.openweathermap.org/data/2.5/weather?zip=" + inputs[0] + ","
                    + inputs[1] + "&APPID=cc4ae6e545dee0a295a471824c9fdbda";
        } else {
            urlQ = "https://api.openweathermap.org/data/2.5/weather?q=" + inputs[0] + "," +
                    inputs[1] + "&APPID=cc4ae6e545dee0a295a471824c9fdbda";
        }

        try {
            //connection to the weather API

            URL url = new URL(urlQ);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            //from slides best practice
            if (networkInfo != null && networkInfo.isConnected()) {
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.connect();

                InputStream in = new BufferedInputStream(connection.getInputStream());
                String res = readStream(in);

                //convert to json to parse later
                JSONObject json = new JSONObject(res);

                //get the temp for city
                double temp = json.getJSONObject("main").getDouble("temp");

                //convert to fahrenheit
                double F = (temp * (9.0 / 5.0)) - 459.67;

                //get longitude and latitude coordinates
                double lon = json.getJSONObject("coord").getDouble("lon");
                double lat = json.getJSONObject("coord").getDouble("lat");

                //information on weather is stored in a json array
                //"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]
                JSONArray weatherInfo = json.getJSONArray("weather");
                String iconId = weatherInfo.getJSONObject(0).getString("icon");

                //change temp on UI
                setText(tempText, (int) F + "\u2109");

                //set map on UI
                setMap(lat, lon);

                //load image from url
                String iconUrl = "https://openweathermap.org/img/wn/" + iconId + ".png";
                Drawable forecastIcon = LoadFromUrl(iconUrl);

                //load image on runnable
                setIcon(iconForecast, forecastIcon);
            }

            } catch(IOException | JSONException e){
                e.printStackTrace();
            }
    }

    //Runnable to update the weather icon on the UI
    private void setIcon(final ImageView iconForecast, final Drawable forecastIcon) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iconForecast.setBackground(forecastIcon);
            }
        });
    }

    //prep response to be outputted as an image
    public static Drawable LoadFromUrl(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            return Drawable.createFromStream(is, "src name");
        } catch (Exception e) {
            return null;
        }
    }


    //function to update temperature on main thread
    private void setText ( final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }

    //convert data from get request into string and read from it
    private String readStream (InputStream in) throws IOException {
        int len = 2000;

        Reader reader = new InputStreamReader(in, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    //function to center the chosen destination on the ui
    public void setMap ( final double lat, final double lon){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(lat, lon), 10));
            }});
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
    }
}
