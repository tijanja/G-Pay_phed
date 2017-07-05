package gpay.com.g_pay;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by aKI on 26/03/2016.
 */
public class MeVolley extends Application
{
    private RequestQueue mRequestQueue;
    private static MeVolley mInstance;
    public static final String TAG = MeVolley.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    public static synchronized MeVolley getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public <T> void add(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancel()
    {
        mRequestQueue.cancelAll(TAG);
    }
}
