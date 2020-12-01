package info.androidhive.viewpager2;

import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class RequestHelper {

    public static final int GLOBAL_TIMEOUT_SEC = 20;
    private Context context;
    private RequestQueue queue;
    private final ProgressBar spinner;
    private final ListView devicesComponent;


    public RequestHelper(RequestQueue q, Context c, ProgressBar s, ListView d) {
        // context is needed for creating Toast
        this.context = c;
        // Volley queue is needed for making the requests
        this.queue = q;
        this.spinner = s;
        this.devicesComponent = d;
    }

    public interface Callback {
        void onResponse(String response);
    }

    public interface CallbackJsonArray {
        void onResponse(JSONArray jsonArray) throws JSONException;
    }

    public interface CallbackJsonObject {
        void onResponse(JSONObject jsonObject) throws JSONException;
    }

    public void makeRequest(final String url, boolean pleaseWait, final String onSuccess, final Callback cb) {

        Log.d("makeRequest", "request to "+url);
        if (pleaseWait) {
            makeText(context, "please wait...", LENGTH_LONG).show();
        }
        StringRequest request = new StringRequest(Request.Method.GET, url,
            // on success
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("makeRequest", "GET request to "+url+" succeeded");
                    if (onSuccess != null) {
                        makeText(context, onSuccess, LENGTH_LONG).show();
                    }
                    if (cb != null) {
                        cb.onResponse(response);
                    }
                }
            },

            // on error
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    makeText(context, R.string.request_failed_msg, LENGTH_LONG).show();
                    Log.e("makeRequest", "GET request to "+url+" failed with error:\n"+error);
                }
            });

        // we set a specific timeout for the request
        // and we forbid any automatic retry (maxNumRetries=0)
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(GLOBAL_TIMEOUT_SEC),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.queue.add(request);
    }


    public void makeRequestAndParseJsonObject(final String url, boolean pleaseWait, final String onSuccessMsg, final CallbackJsonObject cbJsonObj) {

        makeRequest(url, pleaseWait, onSuccessMsg, new Callback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    cbJsonObj.onResponse(jsonObject);
                } catch (JSONException e) {
                    makeText(context, R.string.JSON_parsing_failed_msg, LENGTH_LONG).show();
                    Log.e("makeRequest", "JSON parsing failed with error:\n"+e.toString());
                }
            }
        });
    }

    public void makeRequestAndParseJsonArray(final String url, boolean pleaseWait, final String onSuccessMsg, final CallbackJsonArray cbJsonArr) {

        makeRequest(url, pleaseWait, onSuccessMsg, new Callback() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    cbJsonArr.onResponse(jsonArray);
                } catch (JSONException e) {
                    makeText(context, R.string.JSON_parsing_failed_msg, LENGTH_LONG).show();
                    Log.e("makeRequest", "JSON parsing failed with error:\n"+e.toString());
                }
            }
        });
    }
}
