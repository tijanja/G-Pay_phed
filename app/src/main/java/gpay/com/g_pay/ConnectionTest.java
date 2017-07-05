package gpay.com.g_pay;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by aKI on 08/02/2017.
 */

public class ConnectionTest
{
    private String URL = "http://104.131.174.54:9090/";
    private Context context;
    private boolean isValueReady=false;
    private OnDataReady onDataReady;
    private String tokenString;
    private JSONObject jsonObject;
    private String URLdir;

    public ConnectionTest(Context c, Map<String, String> param,  OnDataReady ready)
    {

        context = c;
        onDataReady = ready;
        URLdir = param.get("dir");
        tokenString = param.get("authToken");
        jsonObject = new JSONObject(param);

        //Toast.makeText(context,tokenString,Toast.LENGTH_LONG).show();
        connect();

    }


    private void connect()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,URL+URLdir,jsonObject , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                onDataReady.dataReady(response,ConnectionTest.this);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                String s = new String(error.networkResponse.data);
                try
                {
                    JSONObject jsonObject = new JSONObject(s);
                    onDataReady.onConnectionError(jsonObject);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                //Log.e("Server error","------------------------");
                //Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
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
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MeVolley.getInstance().add(jsonObjectRequest);

    }
}
