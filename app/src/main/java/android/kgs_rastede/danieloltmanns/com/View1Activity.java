package android.kgs_rastede.danieloltmanns.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class View1Activity extends ActionBarActivity {

    ListView listView;
    private View1ListAdapter m_adapter;
    private ArrayList<View1ListItem> m_parts = new ArrayList<>();
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view1);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(!prefs.getBoolean("logged",false)) {
            Intent intent = new Intent(View1Activity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        //Gespeicherte Login Daten werden abgerufen
        final String user = prefs.getString("user","");
        final String pass = prefs.getString("pass","");

        //Listview wird geladen
        listView = (ListView)findViewById(R.id.listView);

        //Adapter wird erstellt
        m_adapter = new View1ListAdapter(this, R.layout.view_1_item, m_parts);
        listView.setAdapter(m_adapter);

        //Daten werden geladen
        convertData(user,pass);
        //Listview wird aktualisiert
        m_adapter.notifyDataSetChanged();

    }

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
        if (id == R.id.action_logout) {
            //Logout Prozess
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            prefs.edit().putString("user","").putBoolean("logged",false).putString("login_resp","").commit();
            Toast.makeText(getApplicationContext(), "Erfolgreich Ausgeloggt", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(View1Activity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetTask extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Lade Dialog wird gezeigt
            pDialog = new ProgressDialog(View1Activity.this);
            pDialog.setMessage("Lade Daten ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... data) {
            // HTTPClient wird erstellt
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("http://www.kgsrastede.de/gp-info/substitutions/request.php?action=getEverything&action=getEverything");

            //Cookies für Login Daten werden erstellt
            CookieStore cookieStore = new BasicCookieStore();

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 365);
            Date date = calendar.getTime();

            BasicClientCookie cookie = new BasicClientCookie("OUTPUTPROFILE", "11");
            cookie.setDomain("www.kgsrastede.de");
            cookie.setPath("/gp-info/substitutions");
            cookie.setExpiryDate(date);

            BasicClientCookie cookie2 = new BasicClientCookie("PASS",data[1]);
            cookie2.setDomain("www.kgsrastede.de");
            cookie2.setPath("/");
            cookie2.setExpiryDate(date);

            BasicClientCookie cookie3 = new BasicClientCookie("USER",data[0]);
            cookie3.setDomain("www.kgsrastede.de");
            cookie3.setPath("/");
            cookie3.setExpiryDate(date);

            cookieStore.addCookie(cookie3);
            cookieStore.addCookie(cookie2);
            cookieStore.addCookie(cookie);

            HttpContext localContext = new BasicHttpContext();
            //Cookies werden eingestellt
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            try {
                //HTTP POST wird ausgeführt (Daten Abruf)
                HttpResponse response = httpclient.execute(httpget,localContext);
                String resp = EntityUtils.toString(response.getEntity());

                //Daten werden in die Konsole geschieben
                Log.v("response ", resp);

                return resp;

            } catch (IOException e) {
                Log.e("Error",e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String resp) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //Daten werden geladen und konvertiert
                        JSONObject j_main = new JSONObject(resp);
                        JSONArray j_subs = j_main.getJSONArray("substitutions");
                        if (j_subs.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Keine Daten vorhanden", Toast.LENGTH_LONG).show();
                            m_adapter.add(new View1ListItem("", "Keine","Daten","","","","","",""));
                        } else {
                            for (int i = 0; i < j_subs.length(); i++) {
                                JSONArray j_day = j_subs.getJSONArray(i);
                                for (int day_i = 0; day_i < j_day.length(); day_i++) {
                                    JSONObject j_day_o = j_day.getJSONObject(day_i);
                                    //Items werden zum Adapter hinzugefpgt
                                    m_adapter.add(new View1ListItem(j_day_o.getString("date"), j_day_o.getString("hour"), j_day_o.getString("subject"), j_day_o.getString("teacher"), j_day_o.getString("status"), j_day_o.getString("room"), j_day_o.getString("supply"), j_day_o.getString("postponement"), j_day_o.getString("notice")));
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            //Dialog wird geschlossen
            pDialog.dismiss();
        }
    }

    void convertData(String user,String pass) {
        if(isNetworkAvailable()) {
            new GetTask().execute(user, pass);
        } else {
            Toast.makeText(View1Activity.this, "Keine Verbindung", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
