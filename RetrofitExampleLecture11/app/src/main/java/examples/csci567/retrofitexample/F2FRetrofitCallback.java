package examples.csci567.retrofitexample;

import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by bryandixon on 6/9/15.
 */
public class F2FRetrofitCallback<S> implements Callback<S> {
    private static final String TAG = F2FRetrofitCallback.class.getSimpleName();

    @Override
    public void success(S s, Response response) {

    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to make http request for: " + error.getUrl());
        Response errorResponse = error.getResponse();
        Log.e(TAG, error.toString());
        if (errorResponse != null) {
            Log.e(TAG, errorResponse.getReason());
            if (errorResponse.getStatus() == 422) {
                Log.e(TAG, "Errors during create or update");
            } else if (errorResponse.getStatus() == 500) {
                Log.e(TAG, "Server Side errors");
            }
        }
    }
}
