package com.guanhan.psi_reader;

import android.net.Uri;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by guanhan on 12/14/2016.
 */
public class PSI {
    private static final String TAG = "PSI API";

//    private static <T> T parseResponse(Response response,
//                                       TypeToken<T> typeToken) {
//        String responseString = null;
//        try {
//            responseString = response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //Log.d(TAG, responseString);
//
//        try {
//            return ModelUtils.toObject(responseString, typeToken);
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public static JSONObject makeRequest() throws IOException{
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.data.gov.sg/v1/environment/psi?date=2016-12-12")
                .addHeader("api-key", "ZQogaw8c5NXRAU94Gqb4X4BdOABfEHWZ")
                .build();
        Response response = client.newCall(request).execute();
        Log.d(TAG, response.toString());
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        String responseString = response.body().string();

        JSONObject psi24Object = null;
        JSONObject psi3Object = null;
        try {
            JSONObject obj = new JSONObject(responseString);
            System.out.println("5555555555555555555555555");
            //System.out.println(obj.getString("items"));
            //System.out.println(obj.getJSONObject("items").getJSONObject("readings").getJSONObject("psi_twenty_four_hourly").getString("national"));
            //System.out.println(((JSONObject) ((JSONObject) (((JSONArray) obj.get("items")).get(0))).get("readings")).getString("psi_twenty_four_hourly"));
            psi24Object = (JSONObject) ((JSONObject) ((JSONObject) (((JSONArray) obj.get("items")).get(0))).get("readings")).get("psi_twenty_four_hourly");
            //System.out.println(((JSONObject)((JSONArray) obj.get("items")).get("readings")).getString("psi_twenty_four_hourly"));
            psi3Object = (JSONObject) ((JSONObject) ((JSONObject) (((JSONArray) obj.get("items")).get(0))).get("readings")).get("psi_three_hourly");
            System.out.println("psi24: " + psi24Object);
            System.out.println("psi3: " + psi3Object);

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return psi24Object;
        }


/*
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        //will contain the raw JSON response as a string
        String psiJsonStr = null;
        try {
            final String PSI_URL = "https://api.data.gov.sg/v1/environment/psi";
            Uri builtUri = Uri.parse(PSI_URL).buildUpon().appendQueryParameter("date", "2016-11-11").build();
            URL url = new URL(builtUri.toString());
            //create the request to the server, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("api-key","ZQogaw8c5NXRAU94Gqb4X4BdOABfEHWZ");
            urlConnection.connect();
            System.out.println("11111111111111111111");
            System.out.println("333333333333333333333   " + urlConnection.getResponseCode());
            Log.d(TAG, "Response Code :" + urlConnection.getResponseCode());
            System.out.println("222222222222222222222222");
            //Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return;
            }
            psiJsonStr = buffer.toString();
            Log.d(TAG, psiJsonStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }
            */


    }

}
