package com.bluebox.bluebox;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


public class RequestHelper {

    public static final int GLOBAL_TIMEOUT_SEC = 20;
    private Context context;
    private RequestQueue queue;

    public RequestHelper(RequestQueue q, Context c) {
        // context is needed for creating Toast
        this.context = c;
        // Volley queue is needed for making the requests
        this.queue = q;
    }

    public interface Callback {
        void onResponse(String response);
    }

    public interface CallbackWithError {
        void onResponse(String response);
        void onError(VolleyError error);
    }

    public interface CallbackJsonArray {
        void onResponse(JSONArray jsonArray) throws JSONException;
    }

    public interface CallbackJsonObject {
        void onResponse(JSONObject jsonObject) throws JSONException;
    }

    public interface CallbackArrayList {
        void onResponse(ArrayList<Device> arrayList);
    }

    public void makeRequestWithError(String url, boolean pleaseWait, String loadingMessage, CallbackWithError cb) {

        Log.d("makeRequest", "request to "+url);
        ProgressDialog dialog = null;
        if (pleaseWait) {
            dialog = ProgressDialog.show(context, loadingMessage,"Veuillez patienter...", true);
            dialog.setIcon(R.drawable.ic_bluetooth);
        }
        final ProgressDialog finalDialog = dialog;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                // on success
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("makeRequest", "GET request to "+url+" succeeded");
                        finalDialog.dismiss();
                        if (cb != null) {
                            cb.onResponse(response);
                        }
                    }
                },

                // on error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finalDialog.dismiss();
                        if (cb != null) {
                            cb.onError(error);
                        }
                    }
                });

        // we set a specific timeout for the request
        // and we forbid any automatic retry (maxNumRetries=0)
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(5),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.queue.add(request);
    }

    public void makeRequest(String url, boolean pleaseWait, String loadingMessage, Callback cb) {

        Log.d("makeRequest", "request to "+url);
        ProgressDialog dialog = null;
        if (pleaseWait) {
            dialog = ProgressDialog.show(context, loadingMessage,"Veuillez patienter...", true);
            dialog.setIcon(R.drawable.ic_bluetooth);
        }
        final ProgressDialog finalDialog = dialog;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                // on success
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("makeRequest", "GET request to "+url+" succeeded");
                        finalDialog.dismiss();
                        if (cb != null) {
                            cb.onResponse(response);
                        }
                    }
                },

                // on error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finalDialog.dismiss();
                        AlertDialog.Builder errorDialog= new AlertDialog.Builder(context);
                        errorDialog.setIcon(R.drawable.ic_checkmark_filled);
                        errorDialog.setTitle("Erreur");
                        errorDialog.setMessage("Veuillez réessayez...");
                        errorDialog.create().show();
                        Log.e("makeRequest", "GET request to "+url+" failed with error:\n"+error.getMessage());
                    }
                });

        // we set a specific timeout for the request
        // and we forbid any automatic retry (maxNumRetries=0)
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(5),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.queue.add(request);
    }


    public void makeRequestAndParseJsonArray(String url, boolean pleaseWait, String loadingMessage, CallbackJsonArray cbJsonArr) {

        makeRequest(url, pleaseWait, loadingMessage, new Callback() {
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

    public void makeMockRequest(String url, boolean pleaseWait, String loadingMessage, CallbackArrayList cbArrayList) {

        Log.d("makeRequest", "request to "+url);

        ProgressDialog dialog = null;
        if (pleaseWait) {
            dialog = ProgressDialog.show(context, loadingMessage,"Veuillez patienter...", true);
            dialog.setIcon(R.drawable.ic_bluetooth);
        }
        ProgressDialog finalDialog = dialog;

        ArrayList<Device> deviceList = new ArrayList<Device>();
        deviceList.add(new Device("BLP9820", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("UE BOOM", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("UE BOOM 2", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("PhilipsBT", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("JBL GO", "A1:B2:C3:D4:E5:F6", false));
        deviceList.add(new Device("Bose Revolve SoundLink", "A1:B2:C3:D4:E5:F6", false));

        StringRequest request = new StringRequest(Request.Method.GET, url,
                // on success
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("makeMockRequest", "GET request to "+url+" succeeded");
                        finalDialog.dismiss();

                        Log.d("makeMockRequest", "mock request finished with size "+deviceList.size());
                        cbArrayList.onResponse(deviceList);
                    }
                },

                // on error
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("makeMockRequest", "GET request to "+url+" failed with error:\n"+error.getMessage());
                        finalDialog.dismiss();

                        Log.d("makeMockRequest", "mock request finished with size "+deviceList.size());
                        cbArrayList.onResponse(deviceList);
                    }
                });

        // we set a specific timeout for the request
        // and we forbid any automatic retry (maxNumRetries=0)
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(3),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.queue.add(request);
    }
}
