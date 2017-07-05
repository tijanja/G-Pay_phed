package gpay.com.g_pay;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by adetunji on 6/1/17.
 */

public class PaymentConnection
{
    private String URL = "https://api.amplifypay.com/";
    private Context context;
    private boolean isValueReady=false;
    private OnDataReady onDataReady;
    private String tokenString;
    private JSONObject jsonObject;
    private String URLdir;

    public PaymentConnection(Context c, Map<String, String> param, OnDataReady ready)
    {

        context = c;
        onDataReady = ready;
        URLdir = param.get("dir");
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

                onDataReady.dataReady(response,PaymentConnection.this);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                String s = new String(error.networkResponse.data);
                try
                {

                    JSONObject jsonObject = new JSONObject(s.trim());
                    onDataReady.onConnectionError(jsonObject);
                }
                catch (JSONException e)
                {
                    onDataReady.onNoConnection(s.trim());
                }
                catch (NullPointerException e)
                {
                    onDataReady.onNoConnection("No connection try again later");
                }

                //Log.e("Server error","------------------------");
                //Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap<>();

                //String s = tokenString+":";

                //String auth = Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);

                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Cache-Control", "no-cache");
                headers.put("Host","api.amplifypay.com");
                return headers;
            }

        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MeVolley.getInstance().add(jsonObjectRequest);

    }
}

