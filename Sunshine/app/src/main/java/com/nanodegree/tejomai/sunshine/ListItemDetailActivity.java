package com.nanodegree.tejomai.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ActionProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ListItemDetailActivity extends AppCompatActivity {

    final String TAG = "ListItemDetailActivity";
    TextView tvDetail;
    ActionProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_detail);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent!=null){
            String detail = intent.getStringExtra(SunshineUtil.INTENT_EXTRA_DETAIL);
            tvDetail = (TextView) findViewById(R.id.item_detail_textview);
            tvDetail.setText(detail);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        /*MenuItem item = menu.findItem(R.id.action_share);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            provider = (ActionProvider)item.getActionProvider();
        }*/
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
        }else if(id == R.id.action_share){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,tvDetail.getText()+" #Sunshine App");
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                provider.setShareIntent(shareIntent);
            }else*/ if(shareIntent.resolveActivity(getPackageManager())!=null){
                startActivity(shareIntent);
            }else{
                Log.e(TAG,"No app found to share weather update");
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
