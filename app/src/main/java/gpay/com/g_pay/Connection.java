package gpay.com.g_pay;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aKI on 08/02/2017.
 */

public class Connection
{
    private String URL = "http://192.163.215.63:8091/";
    private Context context;
    private boolean isValueReady=false;
    private OnDataReady onDataReady;
    private String tokenString;
    private JSONObject jsonObject;
    private String URLdir;
    private Map<String, Object> params;

    public Connection(Context c, Map<String, Object> param,  OnDataReady ready)
    {

        context = c;
        onDataReady = ready;
        params = param;
        URLdir = (String) param.get("dir");
        tokenString = (String)param.get("authToken");
        jsonObject = new JSONObject(param);

        //Toast.makeText(context,tokenString,Toast.LENGTH_LONG).show();
        connect();

    }


    private void connect()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest((Integer) params.get("method"),URL+URLdir,jsonObject , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                onDataReady.dataReady(response,Connection.this);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {

                try
                {
                    String s = new String(error.networkResponse.data);
                    JSONObject jsonObject = new JSONObject(s.trim());
                    onDataReady.onConnectionError(jsonObject);
                }
                catch (JSONException e)
                {
                    String s = new String(error.networkResponse.data);
                    onDataReady.onNoConnection(s.trim());
                    FirebaseCrash.report(e);
                }
                catch (NullPointerException e)
                {
                    onDataReady.onNoConnection("No connection try again later");
                    FirebaseCrash.report(e);

                }
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap<>();

                String s = tokenString+":";

                String auth = Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);

                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("authorization", "Basic "+auth);
                return headers;
            }

        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MeVolley.getInstance().add(jsonObjectRequest);

    }
}
