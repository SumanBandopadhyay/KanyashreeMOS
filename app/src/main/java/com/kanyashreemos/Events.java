package com.kanyashreemos;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import static org.springframework.util.FileCopyUtils.copy;

public class Events extends AppCompatActivity {

    private final String authuser = "16288adbe9a7cb4baeb0f0d8df7ba4bb";
    private final String authpassword = "001b6d7734d59c236d8eef95842f254e";
    private final String urlAddress = "http://wbkanyashree.gov.in/kp_app/kp_image_upload_fetch";

    private String jsonStr = null;
    private ArrayList<EventsKeep> eventsKeeps;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        recyclerView = (RecyclerView) findViewById(R.id.events_recylcer_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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

    private class SendPostReqAsyncTask extends AsyncTask<Object, Object, ArrayList<EventsKeep>> {

        ProgressDialog dialog = new ProgressDialog(Events.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(Events.this, "", "Loading......", false);
        }

        @Override
        protected ArrayList<EventsKeep> doInBackground(Object... strings) {
            eventsKeeps = new ArrayList<>();
            try {
                jsonStr = getResponse();
                JSONArray jsonArray = new JSONArray(jsonStr);

                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    EventsKeep eventsKeep = new EventsKeep();
                    String photoDescription = jsonObject.getString("photo_description");
                    eventsKeep.photoDescription = Events.getDrawableFromUrl(photoDescription);
                    //eventsKeep.photoDescription = Events.loadBitmap(photoDescription);
                    //URL url = new URL(photoDescription);
                    //eventsKeep.photoDescription = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    eventsKeep.description = jsonObject.getString("description");
                    eventsKeeps.add(eventsKeep);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eventsKeeps;
        }

        @Override
        protected void onPostExecute(ArrayList<EventsKeep> s) {
            super.onPostExecute(s);
            dialog.dismiss();
            //Toast.makeText(getApplicationContext(), jsonStr, Toast.LENGTH_LONG).show();
            adapter = new EventsAdapter(s);
            recyclerView.setAdapter(adapter);
        }
    }

    private class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.DataObjectHolder> {

        private ArrayList<EventsKeep> eventsKeepArrayList;

        @Override
        public DataObjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_items, parent, false);
            DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
            return dataObjectHolder;
        }

        @Override
        public void onBindViewHolder(DataObjectHolder holder, int position) {
            holder.textView.setText(eventsKeepArrayList.get(position).description);
            //holder.imageView.setImageBitmap(eventsKeepArrayList.get(position).photoDescription);
            //Events.downloadfile(eventsKeepArrayList.get(position).photoDescription,holder.imageView);
            //new DownloadImageTask(holder.imageView).execute(eventsKeepArrayList.get(position).photoDescription);
            holder.imageView.setImageDrawable(eventsKeepArrayList.get(position).photoDescription);
        }

        @Override
        public int getItemCount() {
            return eventsKeepArrayList.size();
        }

        public class DataObjectHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;

            public DataObjectHolder(View itemView) {
                super(itemView);

                imageView = (ImageView) itemView.findViewById(R.id.img_events_item);
                textView = (TextView) itemView.findViewById(R.id.txt_events_item);
            }
        }

        public EventsAdapter(ArrayList<EventsKeep> keeps) {
            eventsKeepArrayList = keeps;
        }
    }

    private static Drawable getDrawableFromUrl(final String url) throws IOException, MalformedURLException {
        return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), "name");
    }

    /*public Bitmap downloadfile(String fileurl)
    {
        Bitmap bmp = null;
        URL myfileurl = null;
        try
        {
            myfileurl= new URL(fileurl);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        try
        {
            HttpURLConnection conn= (HttpURLConnection)myfileurl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            int length = conn.getContentLength();
            if(length>0)
            {
                int[] bitmapData =new int[length];
                byte[] bitmapData2 =new byte[length];
                InputStream is = conn.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
            }
            else
            {

            }

        }
        catch(IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp;
    }*/

    /*public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(new URL(url).openStream(), 1000);

            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 1000);
            copy(in, out);
            out.flush();

            final byte[] data = dataStream.toByteArray();
            BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 1;

            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);
        } catch (IOException e) {
            //Log.e(TAG, "Could not load Bitmap from: " + url);
        } finally {
            //closeStream(in);
            //closeStream(out);
        }

        return bitmap;
    }*/

    /*private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        Bitmap bmp = null;
        URL myfileurl = null;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            try
            {
                myfileurl= new URL(urldisplay);
                HttpURLConnection conn= (HttpURLConnection)myfileurl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                int length = conn.getContentLength();
                if(length>0)
                {
                    int[] bitmapData =new int[length];
                    byte[] bitmapData2 =new byte[length];
                    InputStream is = conn.getInputStream();
                    bmp = BitmapFactory.decodeStream(is);
                }
                else
                {

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Bitmap mIcon11 = downloadfile(urldisplay);
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }*/
}
