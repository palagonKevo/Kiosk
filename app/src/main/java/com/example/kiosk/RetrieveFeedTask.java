package com.example.kiosk;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrieveFeedTask extends AsyncTask<String, Void, JSONArray> {
    private static URL url;

    static {
        try {
            url = new URL( "https://rasa.pruebapablo.kevo.sh/webhooks/rest/webhook");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected JSONArray doInBackground(String... strings) {
        JSONObject json = new JSONObject();
        try {
            json.put("sender","caca");
            json.put("message",strings[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                //Setting the Request Method header as POST
                con.setRequestMethod("POST");

                //Setting the Content Type Header as application/json
                con.setRequestProperty("Content-Type", "application/json");

                //Overriding the HTTP method as as mentioned in documentation
                con.setRequestProperty("X-HTTP-Method-Override", "POST");

                con.setDoOutput(true);

                OutputStream os = con.getOutputStream();
                os.write(json.toString().getBytes());

                os.flush();
                os.close();

                String json_response = "";
                InputStreamReader in = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(in);
                String text = "";
                while ((text = br.readLine()) != null) {
                    json_response += text;
                }
                //System.out.println(con.getResponseMessage());
                //System.out.println(con.getResponseCode());
                try {
                    JSONArray jsonArray = new JSONArray(json_response);
                    //System.out.println(jsonArray);
                    return jsonArray;
                }catch (Exception e){
                    System.out.println(e);
                    JSONArray error = new JSONArray();
                    return error;
                }
            } catch (Exception e){
                System.out.println("ERROR "+e);
            } finally {
                con.disconnect();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
