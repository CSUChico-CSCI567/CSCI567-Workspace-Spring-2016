package csci567.loginactivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bryandixon on 4/10/16.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity-L16";
    private RecyclerView mRecyclerView;
    private FirebaseListAdapter mListAdapter = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LayoutInflater inflater;
    private Firebase messageRef = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        inflater = this.getLayoutInflater();
        //Double check we have a firebase reference & authentication if not exit
        if(LoginActivity.mFirebaseRef!=null){
            if(LoginActivity.mFirebaseRef.getAuth()!=null){
                messageRef = LoginActivity.mFirebaseRef.child("messages").child(LoginActivity.mAuthData.getUid());
            }
            else{
                finish();
            }
        }
        else{
            finish();
        }

        messageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG,Long.toString(snapshot.getChildrenCount()));
                mListAdapter.removeAll();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    FirebaseItem item = postSnapshot.getValue(FirebaseItem.class);
                    mListAdapter.addData(item);
                    Log.d(TAG, item.getMessage());
                }
                mListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG,"The read failed: " + firebaseError.getMessage());
            }
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
                    //fetchData();
                    //Event Listener w/ Firebase makes this a bit useless
                    swipeRefreshLayout.setRefreshing(false);
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Seemingly fab can throw a Null exception, I'm asserting that fab has an instance at this point
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Get the layout inflater

                final View view2 = inflater.inflate(R.layout.additem_dialog,null);

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setTitle(R.string.dialog_add_item_title)
                        .setView(view2)
                        // Add action buttons
                        .setPositiveButton(R.string.dialog_add_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                String message = ((EditText) view2.findViewById(R.id.add_title)).getText().toString();
                                Log.d(TAG, message);

                                //Hacky way to prevent duplicates
                                ArrayList<FirebaseItem> list = mListAdapter.getData();
                                for(int i=0;i<list.size();i++){
                                    if(list.get(i).getMessage().equals(message)){
                                        Toast.makeText(getApplicationContext(),"No Duplicates Allowed",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                //If not a duplicate use push to add to Firebase
                                Firebase newPostRef = messageRef.push();

                                Map<String, String> map = new HashMap<String, String>();
                                map.put("message", message);
                                //Put User ID into message, so we can get only our user data back
                                //newPostRef.child(LoginActivity.mAuthData.getUid()).setValue(map);
                                newPostRef.setValue(map);
                                // sign in the user ...
                            }
                        })
                        .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //cancel
                            }
                        });

                builder.create().show();
               /*) Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    private RecyclerView.Adapter getAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new FirebaseListAdapter(this);
            mListAdapter.setData(new ArrayList<FirebaseItem>());
            mListAdapter.setOnItemClickListener(
                    new FirebaseListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final FirebaseItem item) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            // Get the layout inflater

                            View view = inflater.inflate(R.layout.additem_dialog,null);
                            EditText editText = (EditText) view.findViewById(R.id.add_title);
                            editText.setText(item.getMessage());


                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the dialog layout
                            builder.setTitle(R.string.dialog_edit_item_title)
                                    .setView(view)
                                    // Add action buttons
                                    .setPositiveButton(R.string.dialog_update_button, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            //update entry?
                                        }
                                    })
                                    .setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //cancel do nothing
                                        }
                                    });

                            builder.create().show();

                        }
                    });
        }
        return mListAdapter;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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