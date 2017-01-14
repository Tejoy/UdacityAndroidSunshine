package com.nanodegree.tejomai.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClassName(getBaseContext(),SettingsActivity.class.getName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }else if(id == R.id.location_on_map){
            String postCode = getSharedPreferences(SunshineUtil.SHARED_PREF_NAME,MODE_PRIVATE).getString(SunshineUtil.KEY_PREF_LOCATION,"");
            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", postCode)
                    .build();
            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(geoLocation);

            if(mapIntent.resolveActivity(getPackageManager())!=null) {
                startActivity(mapIntent);
            }else{
                Log.e(TAG,"Activity for maps could not be found");
            }
        }

        return super.onOptionsItemSelected(item);
    }


}
