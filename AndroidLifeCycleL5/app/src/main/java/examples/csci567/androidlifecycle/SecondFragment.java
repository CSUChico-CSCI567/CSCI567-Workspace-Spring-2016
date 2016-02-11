package examples.csci567.androidlifecycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bryandixon on 6/7/15.
 */
public class SecondFragment extends Fragment {
    private final String TAG = "ALC-Fragment2 ";
    private View rootView;


    public SecondFragment() {
    }

    protected void onAttach(){
        Log.d(TAG, "onAttach");
    }

    protected void onCreate(){
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        rootView = inflater.inflate(R.layout.fragment2_main, container, false);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
    }

    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    public void onDestroyView(){
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void onDetach(){
        super.onDetach();
        Log.d(TAG, "onDetach");
    }
}
