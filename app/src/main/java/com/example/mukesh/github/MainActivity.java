package com.example.mukesh.github;

import android.app.Fragment;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v("Main", "oncreate");
//        Fragment frag = (Fragment) getFragmentManager().findFragmentById(R.layout.fragment_home);
    }

    public void onsearch( View v ){
        Log.v("Main", "onclick");

        EditText ed= (EditText)findViewById(R.id.search);
        if(ed.getText().length() == 0 ){
            Toast.makeText(getApplicationContext(), "Please Enter User ID", Toast.LENGTH_SHORT ).show();
        }

        else {
            String value = ed.getText().toString();
            getRepo gt = new getRepo();
            gt.execute(value);
        }
    }

    public class getRepo extends AsyncTask<String, Void, String >{

        private final String LOG_CAT = getRepo.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            Log.v(LOG_CAT, "URL is " + "doInBackground");
            String error=null;
            if( params.length == 0 ){
                return "null_noInput";
            }

            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader = null;

            String base = "https://api.github.com/users";
            String repo= "repos";

            URL url = null;
            try {

                Uri uri = Uri.parse(base).buildUpon().appendPath(params[0]).appendPath(repo).build();

                //url = new URL("https://api.github.com/users/verma-ady/repos");
                Log.v(LOG_CAT, uri.toString());
                url= new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
                    return "null_inputstream";
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line ;

                while ( (line=bufferedReader.readLine())!=null ){
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    return "null_inputstream";
                }

                String stringJSON = new String();
                stringJSON = buffer.toString();
                Log.v(LOG_CAT, stringJSON );
                return stringJSON;
            } catch (ConnectException | ProtocolException | MalformedURLException e) {
                error= "null_file";
                e.printStackTrace();
            } catch (UnknownHostException e) {
                error = "null_internt" ;
                e.printStackTrace();
            } catch (IOException e ) {
                error= "null_file";
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            return error;
        }//doinbackground

        @Override
        protected void onPostExecute(String strJSON) {

            ArrayList<String> repoList = new ArrayList<>();
            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return;
            }

            if ( strJSON=="null_internet" ){
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Log.v("JSONARRAY", strJSON);
//                JSONObject JSON = new JSONObject(strJSON);
                JSONArray JSON = new JSONArray(strJSON);

                int num = JSON.length();

                for ( int i=0; i<num ; i++ ){
                    JSONObject repoJSON = JSON.getJSONObject(i);
                    String repoName, repoDesc;
                    repoName = repoJSON.getString("name");
                    repoDesc = repoJSON.getString("description");
                    repoList.add(repoName );
                    Log.v("JSONString", repoName + repoDesc );
                }
            } catch (JSONException e) {
//                Toast
                e.printStackTrace();
            }

            Fragment frag = getFragmentManager().findFragmentById(R.id.fragment);
            ListView lv = (ListView) findViewById(R.id.repolistView);
            ArrayAdapter<String> lvadapter = (ArrayAdapter<String>)lv.getAdapter();
            lvadapter.clear();
            lvadapter.addAll(repoList);
            lv.setAdapter(lvadapter);
        }
    }//getrepo




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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
