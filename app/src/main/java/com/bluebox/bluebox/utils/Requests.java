package com.bluebox.bluebox.utils;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bluebox.bluebox.R;
import com.bluebox.bluebox.devicelist.Device;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;


public class Requests {

    // the timeout before a request fails, in seconds
    public static final int TIMEOUT_BEFORE_ERROR = 20;

    // RequestQueue is the object from the Volley library needed for making the requests
    protected RequestQueue queue;
    // Context is needed for creating Toast and print information to user
    protected Context context;

    public Requests(RequestQueue q, Context c) {
        this.queue = q;
        this.context = c;
    }

    public interface Callback<T> {
        void returnResponse(T response);
    }

    public interface ErrorCallback<T> {
        void returnError(T error);
    }

    public void makeRequest(String url, String loadingMessage, Callback<String> cb, ErrorCallback errorCb) {
        Logger.d("request to "+url);

        // Dialog that says "please wait..." and blocks all user clicks until dismiss() is called
        final ProgressDialog pleaseWaitDialog = ProgressDialog.show(context, loadingMessage,"Veuillez patienter...", true);
        pleaseWaitDialog.setIcon(R.drawable.ic_bluetooth);

        // Dialog that says "error occurred..."
        final AlertDialog.Builder errorDialog = new AlertDialog.Builder(context);
        errorDialog.setIcon(R.drawable.ic_checkmark_filled);
        errorDialog.setTitle("Erreur");
        errorDialog.setMessage("Veuillez réessayez...");

        // make the request
        StringRequest request = new StringRequest(Request.Method.GET, url,
                // on success
                (String response) -> {
                    Logger.d("GET request to "+url+" succeeded");
                    pleaseWaitDialog.dismiss();
                    if (cb != null) {
                        cb.returnResponse(response);
                    }
                },

                // on error
                (VolleyError error) -> {
                    Logger.e("GET request to "+url+" failed with error: "+error.getMessage());
                    pleaseWaitDialog.dismiss();
                    errorDialog.create().show();
                    if (errorCb != null) {
                        errorCb.returnError(error);
                    }
                });

        // we set a custom timeout for the request, and we forbid any automatic retry (maxNumRetries=0)
        request.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(TIMEOUT_BEFORE_ERROR),
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // finally, we add the request to the Volley RequestQueue object
        // it will be automatically launched
        this.queue.add(request);
    }

    public void makeRequestAndParseJsonArray(String url, String loadingMessage, Callback<JSONArray> cb) {
        makeRequest(url, loadingMessage, (String response) -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                cb.returnResponse(jsonArray);
            } catch (JSONException e) {
                makeText(context, R.string.JSON_parsing_failed_msg, LENGTH_LONG).show();
                Logger.e("JSON parsing failed with error: "+e.toString());
            }
        }, null);
    }

    public void makeFakeScan(Callback<ArrayList<Device>> cb) {
        ArrayList<Device> fakeDeviceList = new ArrayList<>();
        fakeDeviceList.add(new Device("BLP9820", "A1:B2:C3:D4:E5:F6", false));
        fakeDeviceList.add(new Device("Samsung A51", "A1:B2:C3:D4:E5:F6", false));
        fakeDeviceList.add(new Device("UE BOOM 2", "A1:B2:C3:D4:E5:F6", false));
        fakeDeviceList.add(new Device("PhilipsBT", "A1:B2:C3:D4:E5:F6", false));
        fakeDeviceList.add(new Device("Bose Revolve SoundLink", "A1:B2:C3:D4:E5:F6", false));
        Logger.d("fake request finished with size "+fakeDeviceList.size());

        makeRequest("https://google.com", "",
                response -> {
                    cb.returnResponse(fakeDeviceList);
                },
                error -> {
                    cb.returnResponse(fakeDeviceList);
                });
    }
}
