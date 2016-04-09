package examples.csci567.firebase_lecture15;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BlueMixExample" ;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1 ;
    private RecyclerView mRecyclerView;
    private CloudantListAdapter mListAdapter = null;
    private SwipeRefreshLayout swipeRefreshLayout;

    /* *************************************
     *              GOOGLE                 *
     ***************************************/
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve all issues preventing sign-in
     * without waiting. */
    private boolean mGoogleLoginClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can resolve them when the user clicks
     * sign-in. */
    private ConnectionResult mGoogleConnectionResult;

    /* The login button for Google */
    private SignInButton mGoogleLoginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://csci567.firebaseio.com/");
        myFirebaseRef.child("message").setValue("Do you have data? You'll love Firebase.");

        myFirebaseRef.child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG,snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
            }
            @Override public void onCancelled(FirebaseError error) { }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(getAdapter());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(mListAdapter!=null) {
                    fetchData();
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //Create BMS Client for IBM BlueMix
        try {


            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                Log.d(TAG, "In Marshmallow Code");
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.GET_ACCOUNTS)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this,
                                Manifest.permission.GET_ACCOUNTS_PRIVILEGED)
                                != PackageManager.PERMISSION_GRANTED) {



                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS_PRIVILEGED},
                            MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
                else{

                }
            }
            else{
                //Permissions granted already

            }



        } catch (Exception e) {
            Log.d(TAG,e.toString());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        try {

        }
        catch (Exception e){

        }
    }

    private class RefreshAdapter extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... datastores) {
            try {
                Log.d(TAG, "In Refresh Adapter");

                //Update UI?

            }
            catch (Exception e){
                Log.e(TAG, e.toString());
            }
            return null;
        }

        protected void onProgressUpdate(Void... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Void result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mListAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
            //showDialog("Downloaded " + result + " bytes");
        }
    }


    private void fetchData() {
        Log.d(TAG, "In Fetch Data");
        new RefreshAdapter().execute();
    }

    private RecyclerView.Adapter getAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new CloudantListAdapter(this);
            mListAdapter.setData(new ArrayList<CloudantItem>());
            mListAdapter.setOnItemClickListener(
                    new CloudantListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final CloudantItem item) {

                            MaterialDialog.ButtonCallback materialCallbacks = new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                    View v = dialog.getCustomView();
                                    if (v != null) {
                                        EditText categoryText = (EditText) dialog.getCustomView().findViewById(R.id.add_title);
                                        String text = categoryText.getText().toString();
                                        Toast.makeText(getApplicationContext(), "Edit: " + text, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    Toast.makeText(getApplicationContext(), "Delete: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                                    //deleteData(item);
                                }

                                @Override
                                public void onNeutral(MaterialDialog dialog) {
                                    super.onNeutral(dialog);
                                    Toast.makeText(getApplicationContext(), "Dialog Cancel", Toast.LENGTH_SHORT).show();
                                    dialog.cancel();
                                }
                            };
                            final MaterialDialog updateDialog = new MaterialDialog.Builder(MainActivity.this)
                                    .title(getString(R.string.dialog_update_title))
                                    .titleColor(Color.BLACK)
                                    .customView(R.layout.additem_dialog, true)
                                    .positiveText(getString(R.string.dialog_update_button))
                                    .negativeText(getString(R.string.dialog_delete_button))
                                    .neutralText(R.string.dialog_cancel_button)
                                    .callback(materialCallbacks)
                                    .build();

                            DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {

                                @Override
                                public void onShow(DialogInterface dialog) {
                                    if (updateDialog != null) {
                                        View v = updateDialog.getCustomView();
                                        if (v != null) {
                                            EditText title = (EditText) v.findViewById(R.id.add_title);
                                            title.setText(item.getTitle());
                                        }
                                    }
                                }
                            };
                            updateDialog.setOnShowListener(onShowListener);
                            updateDialog.show();
                        }
                    }
            );
        }
        return mListAdapter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // accounts-related task you need to do.




                } else {

                    Log.d(TAG, "Permissions Denied");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    /**
     * A Callback from the Google Authentication Manager Event.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
