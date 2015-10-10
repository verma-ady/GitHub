package com.example.mukesh.github;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    ArrayList<String> repoList = new ArrayList<>();
    database d;
    String isvalid= "Wait";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        d=new database(getApplicationContext());
        Intent intent = getIntent();

        if( intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String strJSON = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v("Main2Activity", strJSON );
            try {
                Log.v("JSONARRAY", strJSON);
//                JSONObject JSON = new JSONObject(strJSON);
                JSONArray JSON = new JSONArray(strJSON);

                int num = JSON.length();
                repoList.clear();
                for ( int i=0; i<num ; i++ ){
                    JSONObject repoJSON = JSON.getJSONObject(i);
                    String repoName, repoDesc;
                    isvalid=repoJSON.getJSONObject("owner").getString("login");
                    repoName = repoJSON.getString("name");
                    repoDesc = repoJSON.getString("description");
                    repoList.add(repoName );
                    Log.v("JSONString", repoName + repoDesc );
                }
            } catch (JSONException e) {
//                Toast
                e.printStackTrace();
            }
        }

        ListView lv = (ListView) findViewById(R.id.repolistView);
        ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>)  lv.getAdapter();
        arrayAdapter.clear();
        arrayAdapter.addAll(repoList);
        lv.setAdapter((ArrayAdapter<String>)arrayAdapter);

    }


    public void onsearch2( View v ){
        Log.v("Main2", "onearch");
        EditText ed2= (EditText)findViewById(R.id.search2);
        if(ed2.getText().length() == 0 ){
            Toast.makeText(getApplicationContext(), "Please Enter User ID", Toast.LENGTH_SHORT).show();
        }

        else {
            String value = ed2.getText().toString();
            getRepo gt = new getRepo();
            gt.execute(value);

        }
    }

    @Override
    protected void onResume() {
        d=new database(getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onPause() {
        d.close();
        super.onPause();
    }

    public void addtodb( View v ){

        EditText ed = (EditText)findViewById(R.id.search2);
        String input = ed.getText().toString();
        try {
            Log.v("Follow_func", isvalid + "" + input);
            if (isvalid.equals(input)){
                if (d.onAdd(isvalid)) {
                    Toast.makeText(getApplicationContext(), isvalid + " successfully followed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), " Failed to follow " + isvalid, Toast.LENGTH_SHORT).show();
                }
                isvalid = "Wait";
            } else if ( isvalid.equals("Wait") ) {
                Toast.makeText(getApplicationContext(), "Unable to fetch user data", Toast.LENGTH_SHORT).show();
                return;
            } else if (isvalid == "null_inputstream" || isvalid == "null_file") {
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                isvalid = "Wait";
                return;
            } else if (isvalid == "null_internet") {
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                isvalid = "Wait";
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Press Search Button to search before Following", Toast.LENGTH_SHORT).show();
                isvalid = "Wait";
            }

        } catch ( SQLiteConstraintException e ){
            e.printStackTrace();
        }
    }


    public class getRepo extends AsyncTask<String, Void, String > {

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
                    isvalid="null_inputstream";
                    return "null_inputstream";
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line ;

                while ( (line=bufferedReader.readLine())!=null ){
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0) {
                    isvalid="null_inputstream";
                    return "null_inputstream";
                }

                String stringJSON = new String();
                stringJSON = buffer.toString();
                Log.v(LOG_CAT, stringJSON );
                return stringJSON;
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
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_CAT, "ErrorClosingStream", e);
                    }
                }
            }
            isvalid=error;
            return error;
        }//doinbackground

        @Override
        protected void onPostExecute(String strJSON) {
        Log.v("Follow", isvalid);
            if( strJSON=="null_inputstream" || strJSON=="null_file" ){
                Toast.makeText(getApplicationContext(), "No Such User Id Found", Toast.LENGTH_SHORT).show();
                return  ;
            }

            if ( strJSON=="null_internet" ){
                Toast.makeText(getApplicationContext(), "No Internet Connectivity", Toast.LENGTH_SHORT).show();
                return ;
            }


            try {
                Log.v("JSONARRAY", strJSON);
//                JSONObject JSON = new JSONObject(strJSON);
                JSONArray JSON = new JSONArray(strJSON);
                int num = JSON.length();
                repoList.clear();
                for ( int i=0; i<num ; i++ ){
                    JSONObject repoJSON = JSON.getJSONObject(i);
                    String repoName, repoDesc;
                    isvalid =repoJSON.getJSONObject("owner").getString("login");
                    repoName = repoJSON.getString("name");
                    repoDesc = repoJSON.getString("description");
                    repoList.add(repoName );
                    Log.v("JSONString" + isvalid, repoName + repoDesc );
                }
            } catch (JSONException e) {
//                Toast
                e.printStackTrace();
            }

            ListView lv = (ListView) findViewById(R.id.repolistView);
            ArrayAdapter<String> arrayAdapter = (ArrayAdapter<String>)  lv.getAdapter();
            arrayAdapter.clear();
            arrayAdapter.addAll(repoList);
            lv.setAdapter((ArrayAdapter<String>)arrayAdapter);
        }
    }//getrepo

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
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
