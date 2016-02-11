package examples.csci567.androidlifecycle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements View.OnClickListener{

    private final String TAG = "ALC-Fragment1 ";
    private View rootView;


    public MainActivityFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Button button = (Button) rootView.findViewById(R.id.launch);
        button.setOnClickListener(this);
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

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.launch) {
            //launch second fragment

            // Check that the activity is using the layout version with
            // the fragment_container FrameLayout
            if (getActivity().findViewById(R.id.fragment_container) != null) {
                SecondFragment secondFragment = new SecondFragment();
                secondFragment.setArguments(getActivity().getIntent().getExtras());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, secondFragment).addToBackStack("MainActivityFragment").commit();
            }
        }
    }
}
