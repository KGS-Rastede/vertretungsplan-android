package android.kgs_rastede.danieloltmanns.com;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class View2Activity extends ActionBarActivity {
    private View2ListAdapter m_adapter;
    private ArrayList<View2ListItem> m_parts = new ArrayList<>();

    GridView table1;
    GridView table2;
    String[] days = new String[45];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view2);

        //Benutzername wird geladen
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = prefs.getString("user","");
        //Benutzername wird ins richtige Format gebracht
        user = user.substring(0,1).toUpperCase()+user.substring(1,2).toLowerCase()+user.substring(2,3).toUpperCase()+user.substring(3,4).toLowerCase()+user.substring(4);

        //Adapter wird erstellt
        m_adapter = new View2ListAdapter(getApplicationContext(),R.layout.view_2_item, m_parts);

        //Gridview wird geladen
        table1 = (GridView)findViewById(R.id.table);
        table1.setAdapter(m_adapter);

        //Tag Überschriften werden gespeichert
        days[0] = "MO";
        days[1] = "DI";
        days[2] = "MI";
        days[3] = "DO";
        days[4] = "FR";

        if(isNetworkAvailable()) {
            new GetTask().execute(user, "0");
        } else {
            Toast.makeText(View2Activity.this, "Keine Verbindung", Toast.LENGTH_SHORT).show();
        }

        m_parts = new ArrayList<>();
        m_adapter = new View2ListAdapter(getApplicationContext(),R.layout.view_2_item, m_parts);

        table2 = (GridView)findViewById(R.id.table2);
        table2.setAdapter(m_adapter);

        //Tag Überschriften werden gespeichert
        days = new String[45];
        days[0] = "MO";
        days[1] = "DI";
        days[2] = "MI";
        days[3] = "DO";
        days[4] = "FR";

        if(isNetworkAvailable()) {
            new GetTask().execute(user, "1");
        } else {
            Toast.makeText(View2Activity.this, "Keine Verbindung", Toast.LENGTH_SHORT).show();
        }
    }

    public class GetTask extends AsyncTask<String, String, String> {
        int number;
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Lade Dialog wird erstellt
            pDialog = new ProgressDialog(View2Activity.this);
            pDialog.setMessage("Lade Daten ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... data) {
            //HTTPClient wird erstellt
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.kgsrastede.de/termine/sek2/vertretungen/gadget/sek2.php?action=getstudenttables&sname="+data[0]);

            try {
                //HTTP Post wird ausgeführt
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());

                Log.v("response ", responseStr);
                number = Integer.parseInt(data[1]);
                return responseStr;
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
                        //Daten werden vom schelchten Format ins richtige umgewandelt
                        JSONObject j_main = new JSONObject(resp);
                        JSONObject j_subs = j_main.getJSONObject("entries");
                        for (Iterator it = j_subs.keys(); it.hasNext(); ) {
                            String name = (String) it.next();
                            JSONArray arr = j_subs.getJSONArray(name);
                            for (int i = number; i < arr.length(); i++) {
                                JSONArray arr2 = arr.getJSONArray(i);
                                for (int i2 = 0; i2 < arr2.length(); i2++) {
                                    int itemIndex = i2 + 5;
                                    if (i2 > 0) {
                                        itemIndex += i2 * 4;
                                    }
                                    if (i > 0) {
                                        itemIndex += i;
                                    }
                                    days[itemIndex] = arr2.get(i2).toString();
                                }
                            }
                            break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Items werden zum Adapter hinzugefügt
                    String color = "";
                    for (int i3 = 0; i3 < days.length; i3++) {
                        if(i3 < 5) { color = "#1abc9c"; }
                        if(i3 >= 5 && i3 < 15) { color = "#f1c40f"; }
                        if(i3 >= 15 && i3 < 25) { color = "#3498db"; }
                        if(i3 >= 25 && i3 < 35) { color = "#9b59b6"; }
                        if(i3 >= 35 && i3 < 45) { color = "#bdc3c7"; }
                        m_adapter.add(new View2ListItem(days[i3], color));
                    }
                }
            });

            pDialog.dismiss();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
