package examples.csci567.bluemix_lecture13;

import android.Manifest;
import android.content.Context;
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
import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.BMSClient;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Request;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.Response;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.ResponseListener;
import com.ibm.mobilefirstplatform.clientsdk.android.security.api.AuthorizationManager;
import com.ibm.mobilefirstplatform.clientsdk.android.security.googleauthentication.GoogleAuthenticationManager;

import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BlueMixExample" ;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1 ;
    private RecyclerView mRecyclerView;
    private CloudantListAdapter mListAdapter = null;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            BMSClient.getInstance().initialize(getApplicationContext(),
                    "http://CSCI567Test.mybluemix.net",
                    "5b677712-8dd0-496e-8cdb-2dcadc72c194");

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
                    //Permissions granted already
                    GoogleAuthenticationManager.getInstance().register(this);

                    //Now that we have a BMS client get requested protected resource from mobile backend.
                    Request request = new Request("/protected", Request.GET);
                    request.send(this, new ResponseListener() {
                        @Override
                        public void onSuccess (Response response) {
                            Log.d("Myapp", "onSuccess :: " + response.getResponseText());
                            Log.d("MyApp Google ID", AuthorizationManager.getInstance().getUserIdentity().getId());
                            Log.d("MyApp Google Email", AuthorizationManager.getInstance().getUserIdentity().getDisplayName());
                        }
                        @Override
                        public void onFailure (Response response, Throwable t, JSONObject extendedInfo) {
                            if (null != t) {
                                Log.d("Myapp", "onFailure :: " + t.getMessage());
                            } else if (null != extendedInfo) {
                                Log.d("Myapp", "onFailure :: " + extendedInfo.toString());
                            } else {
                                Log.d("Myapp", "onFailure :: " + response.getResponseText());
                            }
                        }
                    });
                }
            }
            else{
                //Permissions granted already
                GoogleAuthenticationManager.getInstance().register(this);

                //Now that we have a BMS client get requested protected resource from mobile backend.
                Request request = new Request("/protected", Request.GET);
                request.send(this, new ResponseListener() {
                    @Override
                    public void onSuccess (Response response) {
                        Log.d("Myapp", "onSuccess :: " + response.getResponseText());
                        Log.d("MyApp", AuthorizationManager.getInstance().getUserIdentity().toString());
                    }
                    @Override
                    public void onFailure (Response response, Throwable t, JSONObject extendedInfo) {
                        if (null != t) {
                            Log.d("Myapp", "onFailure :: " + t.getMessage());
                        } else if (null != extendedInfo) {
                            Log.d("Myapp", "onFailure :: " + extendedInfo.toString());
                        } else {
                            Log.d("Myapp", "onFailure :: " + response.getResponseText());
                        }
                    }
                });
            }



        } catch (MalformedURLException e) {
            Log.d(TAG,e.toString());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        try {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MaterialDialog.ButtonCallback materialCallbacks = new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            View v = dialog.getCustomView();
                            if (v != null) {
                                EditText categoryText = (EditText) dialog.getCustomView().findViewById(R.id.add_title);
                                String text = categoryText.getText().toString();
                                Toast.makeText(getApplicationContext(), "Add: " + text, Toast.LENGTH_SHORT).show();
                                try{
                                    // Create a DatastoreManager using application internal storage path
                                    File path = getApplicationContext().getDir("datastores", Context.MODE_PRIVATE);
                                    DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());

                                    Datastore ds = manager.openDatastore("my_datastore");

                                    // Create a document

                                    // Create a document
                                    MutableDocumentRevision rev = new MutableDocumentRevision();


                                    // Build up body content from a Map
                                    Map<String, Object> json = new HashMap<String, Object>();
                                    json.put("title", text);
                                    rev.body = DocumentBodyFactory.create(json);

                                    //Actually Create Document
                                    DocumentRevision revision = ds.createDocumentFromRevision(rev);



                                }
                                catch (Exception e){
                                    Log.e(TAG, e.toString());
                                }
                            }
                        }

                        @Override
                        public void onNeutral(MaterialDialog dialog) {
                            super.onNeutral(dialog);
                            Toast.makeText(getApplicationContext(), "Dialog Cancel", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    };
                    final MaterialDialog updateDialog = new MaterialDialog.Builder(MainActivity.this)
                            .title(getString(R.string.dialog_add_title))
                            .titleColor(Color.BLACK)
                            .customView(R.layout.additem_dialog, true)
                            .positiveText(getString(R.string.dialog_add_button))
                            .neutralText(R.string.dialog_cancel_button)
                            .callback(materialCallbacks)
                            .build();

                    DialogInterface.OnShowListener onShowListener = new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(DialogInterface dialog) {
                            if (updateDialog != null) {
                                View v = updateDialog.getCustomView();
                            }
                        }
                    };
                    updateDialog.setOnShowListener(onShowListener);
                    updateDialog.show();
                }
            });
        }
        catch (Exception e){

        }
    }

    private class RefreshAdapter extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... datastores) {
            try {
                Log.d(TAG, "In Refresh Adapter");
                // Create a DatastoreManager using application internal storage path
                File path = getApplicationContext().getDir("datastores", Context.MODE_PRIVATE);
                DatastoreManager manager = new DatastoreManager(path.getAbsolutePath());
                Datastore ds = manager.openDatastore("my_datastore");
                // read all documents in one go
                int pageSize = ds.getDocumentCount();
                List<BasicDocumentRevision> docs = ds.getAllDocuments(0, pageSize, true);
                mListAdapter.removeAll();
                for(int i=0;i<pageSize;i++){
                    Log.d(TAG, docs.get(i).getBody().asMap().get("title").toString());
                    Log.d(TAG, docs.get(i).getId());
                    mListAdapter.addData(new CloudantItem(docs.get(i).getBody().asMap().get("title").toString()));
                }
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

                    GoogleAuthenticationManager.getInstance().register(this);

                    //Now that we have a BMS client get requested protected resource from mobile backend.
                    Request request = new Request("/protected", Request.GET);
                    request.send(this, new ResponseListener() {
                        @Override
                        public void onSuccess (Response response) {
                            Log.d("Myapp", "onSuccess :: " + response.getResponseText());
                            Log.d("MyApp", AuthorizationManager.getInstance().getUserIdentity().toString());
                        }
                        @Override
                        public void onFailure (Response response, Throwable t, JSONObject extendedInfo) {
                            if (null != t) {
                                Log.d("Myapp", "onFailure :: " + t.getMessage());
                            } else if (null != extendedInfo) {
                                Log.d("Myapp", "onFailure :: " + extendedInfo.toString());
                            } else {
                                Log.d("Myapp", "onFailure :: " + response.getResponseText());
                            }
                        }
                    });


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
        GoogleAuthenticationManager.getInstance()
                .onActivityResultCalled(requestCode, resultCode, data);

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
