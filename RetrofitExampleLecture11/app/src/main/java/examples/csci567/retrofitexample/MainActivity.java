package examples.csci567.retrofitexample;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
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
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import examples.csci567.retrofitexample.adapters.F2FListAdapter;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private F2FListAdapter mListAdapter = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static int page;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getBaseContext();
        page = 1;

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToRecyclerView(mRecyclerView);
        fab.setOnClickListener(fabClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(getAdapter());
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(mListAdapter!=null) {
                    if(mListAdapter.getData().size()<=0) {
                        fetchData();
                    }
                    else{
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    View.OnClickListener fabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            page++;
            fetchData();
        }
    };

    private void fetchData() {
        APIHelper api = APIHelper.getInstance(this);
        Log.d("RetroFitExample: ", ((Integer)page).toString());
        api.createAPIInterface(Recipes.class).recipeList("22bc1ea05b100779244d0e387f11499b", "", "",page,
                new F2FRetrofitCallback<RecipeList>() {
                    @Override
                    public void success(RecipeList recipes, Response response) {
                        super.success(recipes, response);
                        addItemsToList(recipes.getRecipeItems());
                    }
                }
        );
    }
    private void addItemsToList(ArrayList<RecipeItem> data) {
        if (data != null) {
            if (!data.isEmpty()) {
                if(mListAdapter==null || mListAdapter.getData().size()<=0) {
                    try {
                        mListAdapter.setData(data);
                    }
                    catch (Exception e){
                        Log.e("RetroFitExample: ", e.toString());
                    }
                }
                else{
                    for(int i=0;i<data.size();i++) {
                        mListAdapter.addData(data.get(i));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        }
    }

    private RecyclerView.Adapter getAdapter() {
        if (mListAdapter == null) {
            mListAdapter = new F2FListAdapter(this);
            mListAdapter.setData(new ArrayList<RecipeItem>());
            mListAdapter.setOnItemClickListener(
                    new F2FListAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(final RecipeItem item) {

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
                                    .titleColor(Color.WHITE)
                                    .customView(R.layout.add_edit_dialog, true)
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
                                            EditText rank = (EditText) v.findViewById(R.id.add_rank);
                                            title.setText(item.getTitle());
                                            rank.setText(item.getSocialRank());
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
