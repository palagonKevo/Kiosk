package com.example.kiosk;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RetrieveNutritionalInfo extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL("https://world.openfoodfacts.org/api/v0/product/"+strings[0]+".json");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            try {
                //Setting the Request Method header as POST
                con.setRequestMethod("GET");

                //Setting the Content Type Header as application/json
                con.setRequestProperty("Content-Type", "application/json");

                int responseCode = con.getResponseCode();
                System.out.println("GET Response Code :: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // print result
                    System.out.println(response.toString());
                    return response.toString();
                }else {
                    System.out.println("GET request error");
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
