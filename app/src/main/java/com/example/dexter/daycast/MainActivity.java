package com.example.dexter.daycast;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    Context context;
    TextView city ,condition, cur_Temp ,other_details , recommend;
    WeatherItem weatherItem ;
    ProgressBar progressBar;
    String city_selected ;
    ImageView icon;
    SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        city_selected = pref.getString("city","jaipur");
        context = MainActivity.this;
        findIds();
        getFromDb();
        if(weatherItem == null){
            loadFirstTime();
        }
        else{
            System.out.println(weatherItem.getCity_name());
            System.out.println(weatherItem.getHumiditylevel());
            System.out.println(weatherItem.getCity_temp());
            refreshData(weatherItem);
        }
        loadFirstTime();

    }

    private void loadFirstTime() {
        if (new CheckInternetConnection(context).isConnectedToInternet()) {
            progressBar.setVisibility(View.VISIBLE);
            loadDataFromServer();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(context, "Check Internet Connectivity!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFromDb() {
        DatabaseRetrieval dbInteraction = new DatabaseRetrieval(context,city_selected);
        SQLiteDatabase db = dbInteraction.getWritableDatabase();
        if(!dbInteraction.isTableExists(db,"Weather_app"))
        dbInteraction.onCreate(db);
        weatherItem = dbInteraction.getItem();
        dbInteraction.close();
    }

    public void findIds(){
        city = (TextView)findViewById(R.id.city_field);
        condition = (TextView)findViewById(R.id.updated_field);
        cur_Temp = (TextView)findViewById(R.id.current_temperature);
        other_details = (TextView)findViewById(R.id.details_field);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        icon = (ImageView)findViewById(R.id.weather_icon);
        recommend = (TextView)findViewById(R.id.tvrecommend);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(MainActivity.this);
    }
    public void refreshData(WeatherItem result) {
        city.setText("City : "+result.getCity_name());
        float kelvin = Float.parseFloat(result.getCity_temp());
        int celsius = (int) (kelvin - 273);
        cur_Temp.setText("Temperature : " + String.valueOf(celsius) + " C");
        other_details.setText("Humidity : "+ result.getHumiditylevel());
        condition.setText("Climate : "+result.getCity_id());
        String c = result.getCity_id();
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(c.equalsIgnoreCase("Clouds")){
            if(hour > 18){
                    icon.setImageResource(R.drawable.ic_cloudy_night);
            }else{
                icon.setImageResource(R.drawable.ic_cloudy_day);
            }
            recommend.setText(R.string.cloud);
        }
        else if(c.equals("Clear")){
            if(hour > 18){
                icon.setImageResource(R.drawable.ic_moon);
            }else{
                icon.setImageResource(R.drawable.ic_sun);
            }
            recommend.setText(R.string.clear_day);

        }
        else if(c.equalsIgnoreCase("Rain")){
                icon.setImageResource(R.drawable.ic_rain);
            recommend.setText(R.string.rain);
        }
        else{
            if(hour > 18){
                icon.setImageResource(R.drawable.ic_moon);
            }else{
                icon.setImageResource(R.drawable.ic_sun);
            }
            recommend.setText(R.string.clear_day);

        }
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }



    /**
     * Call REST API to fetch data
     */
    private void loadDataFromServer() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        city_selected = pref.getString("city","jaipur");
        Log.d("City selected",city_selected);
        //Tag to identify your request
        String tag_string_req = "fetch_item";

        //Request String with Request TYPE
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Config.BASIC_URL+city_selected+Config.API_ID, new Response.Listener<String>() {

            //When any response is received
            @Override
            public void onResponse(String response) {
                WeatherItem weatherItemLayout = new WeatherItem();
                try {
                    DatabaseRetrieval dbInteraction = new DatabaseRetrieval(context,city_selected);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonarray = jsonObject.getJSONArray("weather");
                    JSONObject jsonObject1 = jsonarray.getJSONObject(0);
                    String Climate = jsonObject1.getString("main");
                    String city = city_selected;
                    JSONObject jsonObject2 = jsonObject.getJSONObject("main");
                    String temp = jsonObject2.getString("temp");
                    String humidity = jsonObject2.getString("humidity");
                    Log.d("CITY",city);
                    Log.d("Temperature",temp);
                    Log.d("Humidity",humidity);
                    Log.d("Climate",Climate);
                    weatherItemLayout = new WeatherItem(Climate, city, temp, humidity);
                    refreshData(weatherItemLayout);
                    dbInteraction.insertItem(new WeatherItem(Climate, city, temp, humidity));
                    dbInteraction.close();
                    //If user pulled to refresh then set it to normal state
                    if (swipeRefreshLayout.isRefreshing()) {
                        Toast.makeText(context, "Data Updated!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressBar.setVisibility(View.GONE);

                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Exception", String.valueOf(e));
                    Toast.makeText(context, "Network error!", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;

                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    json = new String(response.data);
                    json = trimMessage(json, "message");
                    if (json != null) displayMessage(json);


                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters if necessary
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public String trimMessage(String json, String key) {
        String trimmedString = null;

        try {
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public void displayMessage(String toastString) {
        Toast.makeText(context, toastString, Toast.LENGTH_LONG).show();
    }

    /**
     * When pull to refresh is called
     */
    public void onRefresh() {
        if (new CheckInternetConnection(context).isConnectedToInternet())
            loadDataFromServer();
        else {
            Toast.makeText(context, "Check Internet Connectivity!", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }
    public void onClick(View v) {
        //set up dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.activity_city_select_dailog);
        ListView listview = (ListView) dialog.findViewById(R.id.listView);
        dialog.setTitle("Select City");
        String[] cityList = getResources().getStringArray(R.array.cityList);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,cityList);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                switch (position) {
                    case 0:
                        editor.putString("city", "jaipur");
                       Log.e("button", pref.getString("city",null));
                        break;
                    case 1:
                        editor.putString("city", "mumbai");
                        break;
                    case 2:
                        editor.putString("city", "delhi");
                        break;
                    case 3:
                        editor.putString("city", "banglore");
                        break;
                    case 4:
                        editor.putString("city", "chennai");
                        break;

                }
                editor.commit();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
