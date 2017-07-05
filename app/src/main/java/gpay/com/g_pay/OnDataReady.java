package gpay.com.g_pay;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by aKI on 08/02/2017.
 */

public interface OnDataReady
{
    public void dataReady(JSONObject jsonObject,Object object);
    public void onConnectionError(JSONObject error);
    public void onNoConnection(String message);
}
