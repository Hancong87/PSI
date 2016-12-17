package com.guanhan.psi_reader;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TextView timestamp;

    private OkHttpClient client;
    //private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timestamp = (TextView) findViewById(R.id.timestamp);

        client = new OkHttpClient();

        Button refreshBtn = (Button) findViewById(R.id.refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryForData();
            }
        });
    }
    private void queryForData(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
        String todayDate = format.format(new Date());
        Log.d(LOG_TAG, todayDate);
        new LoadTextTask().execute(todayDate);
    }
    @Override
    protected void onStart() {
        super.onStart();
        queryForData();
    }

    private class LoadTextTask extends AsyncTask<String, Void, PSIRecord> {

//        String url;
//
//        LoadTextTask(String url) {
//            this.url = url;
//        }
        @Override
        protected PSIRecord doInBackground(String... params) {

            final String PSI_URL = "https://api.data.gov.sg/v1/environment/psi";
            final String PARAM_DATE = "date";
            String url = Uri.parse(PSI_URL).buildUpon().appendQueryParameter(PARAM_DATE, params[0]).build().toString();

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("api-key", BuildConfig.PSI_API_KEY)
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                return parseResponse(response.body().string());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                return null;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        }

        private PSIRecord parseResponse(String responseString) {
            final String NODE_ITEMS = "items";
            final String NODE_READINGS = "readings";
            final String NODE_PSI24 = "psi_twenty_four_hourly";
            final String NODE_PSI3 = "psi_three_hourly";
            final String NODE_TIMESTAMP = "update_timestamp";
            PSIRecord psiRecord = null;
            try {
                JSONObject obj = new JSONObject(responseString);
                JSONArray items = obj.getJSONArray(NODE_ITEMS);
                JSONObject lastItem = items.getJSONObject(items.length() - 1);
                JSONObject readings = lastItem.getJSONObject(NODE_READINGS);
                String update_timestamp = lastItem.getString(NODE_TIMESTAMP);
                JSONObject psi24Object = readings.getJSONObject(NODE_PSI24);
                JSONObject psi3Object = readings.getJSONObject(NODE_PSI3);

                psiRecord = new PSIRecord(psi24Object, psi3Object, update_timestamp);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }
            return psiRecord;
        }

        @Override
        protected void onPostExecute(PSIRecord psiRecord) {
            if (psiRecord == null) {
                Log.d(LOG_TAG, "PSI Record is null?");
                return;
            }

            String[] regionList = getResources().getStringArray(R.array.region_names);

            TableLayout table = (TableLayout) findViewById(R.id.table);
            table.removeAllViews();
            try {
                for (String region : regionList) {
                    TableRow row = (TableRow) LayoutInflater.from(MainActivity.this).inflate(R.layout.attrib_row, null);

                    int psi24 = psiRecord.psi24.getInt(region);
                    int psi3 = psiRecord.psi3.getInt(region);

                    TextView regionTextView = (TextView) row.findViewById(R.id.region);
                    regionTextView.setText(region);
                    TextView psi24TextView = (TextView) row.findViewById(R.id.psi24);
                    psi24TextView.setText(psi24 + "");
                    TextView psi3TextView = (TextView) row.findViewById(R.id.psi3);
                    psi3TextView.setText(psi3 + "");

                    table.addView(row);
                }
                timestamp.setText(psiRecord.update_timestamp);
            } catch (JSONException e) {
                Log.d(LOG_TAG, e.getMessage());
            }
        }
    }

    class PSIRecord {
        JSONObject psi24;
        JSONObject psi3;
        String update_timestamp;

        PSIRecord(JSONObject psi24, JSONObject psi3, String update_timestamp) {
            this.psi3 = psi3;
            this.psi24 = psi24;
            this.update_timestamp = update_timestamp;
        }
    }
}
