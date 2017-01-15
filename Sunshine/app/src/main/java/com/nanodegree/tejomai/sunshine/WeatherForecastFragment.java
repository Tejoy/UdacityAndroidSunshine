package com.nanodegree.tejomai.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class WeatherForecastFragment extends Fragment {

    private String url_path = "http://api.openweathermap.org/data/2.5/forecast/daily?";
    private String TAG = "ForecastFragment";
    private String key_pincode = "q";
    private String key_appid = "appid";
    private String key_units = "units";
    private String key_cnt = "cnt";
    private String key_mode = "mode";
    private final int days = 7;
    private final String DEFAULT_APP_ID = "<ENTER_YOUR_APP_ID>";
    private final String APP_ID = "<ENTER_YOUR_APP_ID>";

    private String value_pincode;
    private String value_appid;
    private String value_units;
    private String value_cnt;
    private String value_mode;

    ArrayAdapter<String> adapter;

    public WeatherForecastFragment() {

        setUrlParams(APP_ID,"metric",days,"json");

    }

    private Uri buildURL(){
        Uri.Builder builder = Uri.parse(url_path).buildUpon();
        builder.appendQueryParameter(key_pincode, value_pincode);
        builder.appendQueryParameter(key_appid,value_appid);
        builder.appendQueryParameter(key_units,value_units);
        builder.appendQueryParameter(key_cnt,value_cnt);
        builder.appendQueryParameter(key_mode,value_mode);
        return builder.build();
    }

    private void setKey_pincode(String pincode){
        value_pincode = pincode;
    }
    private void setKey_units(String units){
        value_units = units;
    }

    private void setUrlParams(String appid,String units,int cnt,String mode){
        value_appid = appid;
        value_units = units;
        value_cnt = String.valueOf(cnt);
        value_mode = mode;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            refreshData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshData(){
        SharedPreferences pref = getActivity().getSharedPreferences(SunshineUtil.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        setKey_pincode(pref.getString(SunshineUtil.KEY_PREF_LOCATION,""));
        String units = pref.getString(SunshineUtil.KEY_PREF_UNITS,"");
        int index = (Arrays.asList(getResources().getStringArray(R.array.settings_units_entries))).indexOf(units);
        setKey_units((getResources().getStringArray(R.array.settings_units_entry_values))[index]);
        if(isPincodeValid(value_pincode)) {
            WeatherFetcherTask weatherFetcherTask = new WeatherFetcherTask();
            weatherFetcherTask.execute(value_pincode);
        }
    }

    private boolean isPincodeValid(String pincode){
        if(pincode == null || pincode.length()<=0 ){
            return false;
        }
        //add pincode validation here
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


            String[] data = {
            };

            adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<>(Arrays.asList(data)));

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ListView list = (ListView) rootView.findViewById(R.id.listview_forecast);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent();
                    intent.setClassName(getActivity().getBaseContext(), ListItemDetailActivity.class.getName());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(SunshineUtil.INTENT_EXTRA_DETAIL, adapter.getItem(i));
                    startActivity(intent);
                }
            });

        refreshData();
        return rootView;
    }

    private String[] parseParams(String forecastJsonStr){
        String arr[] = new String[days];
        JSONObject obj = null;
        String day;
        String result;
        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");

        try {
            obj = new JSONObject(forecastJsonStr);
            JSONArray list = obj.getJSONArray("list");
            for(int i=0;i<days;++i){
                result = new String("");

                long dateTime;
                dateTime = dayTime.setJulianDay(julianStartDay+i);

                day = shortenedDateFormat.format(dateTime);

                result = result.concat(day);
                result = result.concat(" - "+String.valueOf(list.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main")));
                result = result.concat(" - "+String.valueOf(Math.round(list.getJSONObject(i).getJSONObject("temp").getDouble("min"))));
                result = result.concat("/"+String.valueOf(Math.round(list.getJSONObject(i).getJSONObject("temp").getDouble("max"))));
                arr[i] = result;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;
    }

    class WeatherFetcherTask extends AsyncTask<String,Void,String[]> {
        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.

            if(params.length == 0){
                return null;
            }

            if(value_appid.equals(DEFAULT_APP_ID)){
                Log.e(TAG,"APP ID has not been set. Please set the APP ID!!");
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL(buildURL().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("ForecastFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("ForecastFragment", "Error closing stream", e);
                    }
                }
            }
            return parseParams(forecastJsonStr);
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if(strings!=null) {
                adapter.clear();
                for (String str : strings) {
                    adapter.add(str);
                }

                adapter.notifyDataSetChanged();
            }
        }
    };
}