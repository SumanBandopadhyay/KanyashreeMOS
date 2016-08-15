package com.kanyashreemos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class dashboard extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final String authuser = "16288adbe9a7cb4baeb0f0d8df7ba4bb";
    private final String authpassword = "001b6d7734d59c236d8eef95842f254e";
    private final String userId = "19";
    private String urlAddress = null;

    private String flag = null;
    private String jsonStr = null;

    private SendPostReqAsyncTask sendPostReqAsyncTask;

    private Spinner spinner;
    private LinearLayout uniqueLinearLayout;
    private LinearLayout totalLinearLayout;
    private TextView totalUploadedK1;
    private TextView totalUploadedK2;
    private TextView totalSanctionedK1;
    private TextView totalSanctionedK2;
    private TextView uniqueEnrolled;
    private TextView uniqueSanctioned;

    private String totalUploadedK1Str = "";
    private String totalUploadedK2Str = "";
    private String totalSanctionedK1Str = "";
    private String totalSanctionedK2Str = "";
    private String uniqueUploadedStr = "";
    private String uniqueSanctionStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        uniqueLinearLayout = (LinearLayout) findViewById(R.id.unique);
        totalLinearLayout = (LinearLayout) findViewById(R.id.total);
        totalUploadedK1 = (TextView) findViewById(R.id.total_uploaded_k1);
        totalUploadedK2 = (TextView) findViewById(R.id.total_uploaded_k2);
        totalSanctionedK1 = (TextView) findViewById(R.id.total_sactioned_k1);
        totalSanctionedK2 = (TextView) findViewById(R.id.total_sactioned_k2);
        uniqueEnrolled = (TextView) findViewById(R.id.unique_enrolled);
        uniqueSanctioned = (TextView) findViewById(R.id.unique_sanctioned);

        spinner = (Spinner) findViewById(R.id.dashboard_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_list_array, android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        sendPostReqAsyncTask = new SendPostReqAsyncTask();
    }

    private String getResponse(String f) throws UnsupportedEncodingException {
        String data = URLEncoder.encode("authuser","UTF-8") + "=" + URLEncoder.encode(authuser,"UTF-8");
        data += "&" +  URLEncoder.encode("authpassword","UTF-8") + "=" + URLEncoder.encode(authpassword,"UTF-8");
        data += "&" +  URLEncoder.encode("userId","UTF-8") + "=" + URLEncoder.encode(userId,"UTF-8");
        data += "&" +  URLEncoder.encode("flag","UTF-8") + "=" + URLEncoder.encode(f,"UTF-8");

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                flag = "unique";
                urlAddress = "http://wbkanyashree.gov.in/kp_app/kp_total_appl_unique";
                totalLinearLayout.setVisibility(View.GONE);
                uniqueLinearLayout.setVisibility(View.VISIBLE);
                break;

            case 1:
                flag = "unique_application";
                urlAddress = "https://wbkanyashree.gov.in/kp_app/kp_total_unique_application";
                totalLinearLayout.setVisibility(View.GONE);
                uniqueLinearLayout.setVisibility(View.GONE);
                break;

            case 2:
                flag = "total";
                urlAddress = "http://wbkanyashree.gov.in/kp_app/kp_total_appl";
                totalLinearLayout.setVisibility(View.VISIBLE);
                uniqueLinearLayout.setVisibility(View.GONE);
                break;
        }
        sendPostReqAsyncTask.execute(flag);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class SendPostReqAsyncTask extends AsyncTask<String, Void, Void> {

        ProgressDialog dialog = new ProgressDialog(dashboard.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(dashboard.this, "", "Loading......", false);
        }

        @Override
        protected Void doInBackground(String... strings) {
            JSONObject jsonObject = null;
            try {
                jsonStr = getResponse(strings[0]);
                JSONArray jsonArray = new JSONArray(jsonStr);
                jsonObject = jsonArray.getJSONObject(0);

                switch (strings[0]) {
                    case "unique":
                        uniqueUploadedStr = jsonObject.getString("uploaded_k1");
                        uniqueSanctionStr = jsonObject.getString("sanctioned_k1");
                        break;

                    case "unique_application":
                        break;

                    case "total":
                        totalUploadedK1Str = jsonObject.getString("uploaded_k1");
                        totalSanctionedK1Str = jsonObject.getString("sanctioned_k1");
                        totalUploadedK2Str = jsonObject.getString("uploaded_k2");
                        totalSanctionedK2Str = jsonObject.getString("sanctioned_k2");
                        break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();
            /*try {
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(s);*/

            /*} catch (JSONException e) {
                e.printStackTrace();
            }*/
            //Toast.makeText(getApplicationContext(), uploadedK1+" "+sanctionedK1, Toast.LENGTH_LONG).show();
            totalUploadedK1.setText(totalUploadedK1Str);
            totalUploadedK2.setText(totalUploadedK2Str);
            totalSanctionedK1.setText(totalSanctionedK1Str);
            totalSanctionedK2.setText(totalSanctionedK2Str);
            uniqueEnrolled.setText(uniqueUploadedStr);
            uniqueSanctioned.setText(uniqueSanctionStr);
            sendPostReqAsyncTask = null;
            if (sendPostReqAsyncTask == null) {
                sendPostReqAsyncTask = new SendPostReqAsyncTask();
            }
        }
    }
}
