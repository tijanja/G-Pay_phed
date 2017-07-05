package gpay.com.g_pay;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
 * Created by adetunji on 5/17/17.
 */

public class Auth
{
    private final String URL = "http://104.131.174.54:9090/";
    private Context context;
    private OnDataReady onDataReady;
    private JSONObject param;
    private Map<String,Object> maps;
    private String headValue;

    public Auth(Context c, Map<String, Object> params, OnDataReady ready)
    {

        context = c;
        maps = params;
        param = new JSONObject(params);
        onDataReady = ready;
        connect();
    }

    private void connect()
    {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest((Integer) maps.get("method"),URL+maps.get("dir"),param , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {

                onDataReady.dataReady(response,Auth.this);

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
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap<>();

                String s = maps.get("userNameheaderValue")+":"+maps.get("passwordheaderValue");

                String auth = Base64.encodeToString(s.getBytes(), Base64.DEFAULT);

                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("authorization", "Basic "+auth);
                return headers;
            }

        };

        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MeVolley.getInstance().add(jsonObjectRequest);

        /*StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>()
        {


            public String getBodyContentType()
            {
                return "application/json";
            }

            @Override
            public void onResponse(String response)
            {
                try
                {

                    jsonObject = new JSONObject(response);
                    if(progressBar!=null && progressBar.isShown())
                    {
                        progressBar.setVisibility(View.GONE );
                    }
                    onDataReady.dataReady(jsonObject);


                }
                catch (JSONException e)
                {
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError
            {
                return params;
            }

            @Override
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap<>();

                String s = "tunji:tunji";

                String auth = Base64.encodeToString(s.getBytes(), Base64.DEFAULT);

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Basic "+auth);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError
            {
                JSONObject jsonBody = new JSONObject();
                try
                {
                    jsonBody.put("imei", "356766076068242");
                    return jsonBody.toString().getBytes("utf-8");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MeVolley.getInstance().add(request);*/
    }
}
