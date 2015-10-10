package com.example.mukesh.github;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

public class onListClick extends AppCompatActivity {

    public String error=new String();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_list_click);

        Intent intent = getIntent();

        if( (intent != null) && intent.hasExtra(Intent.EXTRA_TEXT) ){
            final String userid = intent.getStringExtra(Intent.EXTRA_TEXT);
            getuserinfo gi = new getuserinfo();
            gi.execute(userid);
        }
    }

    public class getuserinfo extends AsyncTask<String, Void, String >{

        private final String LOG_CAT = getuserinfo.class.getSimpleName();
        public String base = "https://api.github.com/users";
        public String JSONSTRING = new String();
        public String mail, name, company, location, text_data;
        public Long id, follower, following, repos;

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            URL url=null;

            Uri uri = Uri.parse(base).buildUpon().appendPath(params[0]).build();
            Log.v(LOG_CAT, uri.toString());
            try {
                url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = null;
                inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    error = "null_inputstream";
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                JSONSTRING = buffer.toString();
                return JSONSTRING;
            } catch (UnknownHostException e) {
                error = "null_internet" ;
                e.printStackTrace();
            } catch (IOException e) {
                error= "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//do in background

        @Override
        protected void onPostExecute(String strJSON) {
            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }

            try {
                JSONObject JSON = new JSONObject(strJSON);
                name = JSON.getString("name");
                id = JSON.getLong("id");
                follower = JSON.getLong("followers");
                following = JSON.getLong("following");
                mail=JSON.getString("email");
                company=JSON.getString("company");
                location=JSON.getString("location");
                repos=JSON.getLong("public_repos");
//                Bitmap bitmap =
                Log.v("Onclick", id.toString()+" "+follower.toString()+" "+following.toString()+" "+mail+" "+name+" "+company+" "+location );
            } catch ( JSONException e ){
                e.printStackTrace();
            }

            TextView head = (TextView) findViewById(R.id.head_on_list);
            head.setText(name);

            TextView data = (TextView) findViewById(R.id.data_on_list);
            StringBuffer strData=new StringBuffer();
            strData.append("User ID       : " + id.toString() + "\n");
            if (mail!="null"){
            strData.append("EMail ID      : " + mail + "\n");
            } else{
            strData.append("EMail ID      : " + "********" + "\n");
            }

            if (company!="null"){
            strData.append("Company       : " + company + "\n");
            }

            if(location!="null"){
            strData.append("Location      : " + location + "\n");
            }

            strData.append("Public Repos  : " + repos.toString()  + "\n");
            strData.append("Followers     : " + follower.toString() + "\n");
            strData.append("Following     : " + following.toString() + "\n");

            text_data= strData.toString();
            Log.v("Onpost", text_data );
            data.setText(text_data);

        }//onpost

    }//class getuserinfo

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_on_list_click, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
