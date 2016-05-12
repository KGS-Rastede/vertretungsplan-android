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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends ActionBarActivity {

    Button btn_login;
    EditText et_user;
    EditText et_pass;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_user = (EditText) findViewById(R.id.et_user);
        et_pass = (EditText) findViewById(R.id.et_pass);

        btn_login = (Button) findViewById(R.id.btnLogin);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p_user = et_user.getText().toString().trim();
                String p_pass = et_pass.getText().toString().trim();
                if(isNetworkAvailable()) {
                    new LoginTask().execute(p_user, p_pass);
                } else {
                    Toast.makeText(LoginActivity.this, "Keine Verbindung", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class LoginTask extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Login ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(final String... data) {
            //HTTPClient wird erstellt
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.kgsrastede.de/gp-info/substitutions/login.php?action=login");

            try {
                //Daten für POST werden erstellt
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("user", data[0]));
                nameValuePairs.add(new BasicNameValuePair("pass", data[1]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //HTTP Post wird ausgeführt
                HttpResponse response = httpclient.execute(httppost);
                //Daten werden gespeichert
                final String resp = EntityUtils.toString(response.getEntity());

                Log.v("response ", resp);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject j_o = null;
                        try {
                            j_o = new JSONObject(resp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            assert j_o != null;
                            if(j_o.getString("loginstatus").equals("ok")) {
                                //LOGIN successful
                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                try {
                                    prefs.edit().putString("user", data[0]).putString("pass", SHA1(data[1])).putBoolean("logged", true).putString("login_resp",resp).commit();
                                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(getApplicationContext(),"Erfolgreicher Login",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, View1Activity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            } else {
                                //WRONG LOGIN
                                Toast.makeText(getApplicationContext(),"FALSCHER Login",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return resp;
            } catch (IOException e) {
                Log.e("Error",e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String resp) {
            //Wenn der Task zu ende ist wird der Lade Dialog geschlossen.
            pDialog.dismiss();
        }
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
