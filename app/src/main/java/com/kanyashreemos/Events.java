package com.kanyashreemos;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Events extends AppCompatActivity {

    private final String authuser = "16288adbe9a7cb4baeb0f0d8df7ba4bb";
    private final String authpassword = "001b6d7734d59c236d8eef95842f254e";
    private final String urlAddress = "http://wbkanyashree.gov.in/kp_app/kp_image_upload_fetch";

    private String jsonStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    private String getResponse() throws UnsupportedEncodingException {
        String data = URLEncoder.encode("authuser","UTF-8") + "=" + URLEncoder.encode(authuser,"UTF-8");
        data += "&" +  URLEncoder.encode("authpassword","UTF-8") + "=" + URLEncoder.encode(authpassword,"UTF-8");

        BufferedReader br = null;

        try {
            URL url = new URL(urlAddress);

            //Send POST data
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(data);
            writer.flush();

            //Get Server Response
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private class SendPostReqAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                jsonStr = getResponse();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();
        }
    }

}
