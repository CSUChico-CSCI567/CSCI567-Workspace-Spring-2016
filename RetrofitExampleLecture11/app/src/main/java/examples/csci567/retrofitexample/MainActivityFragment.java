package examples.csci567.retrofitexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    View rootView;

    private static int page;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        page = 0;
        //Thread pool for Retrofit to use
        Button button = (Button) rootView.findViewById(R.id.load);
        button.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  fetchData();
              }
          }
        );
        return rootView;
    }

    private void fetchData() {
        page++;
        APIHelper api = APIHelper.getInstance(getActivity().getBaseContext());
        api.createAPIInterface(Recipes.class).recipeList("22bc1ea05b100779244d0e387f11499b", "", "t",page,
                new F2FRetrofitCallback<RecipeList>() {
                    @Override
                    public void success(RecipeList recipes, Response response) {
                        super.success(recipes, response);
                        addItemsToView(recipes.getRecipeItems());
                    }
                }
        );
    }
    private void addItemsToView(List<RecipeItem> data) {
        if (data != null) {
            if (!data.isEmpty()) {
                String text = "";
                for (int i = 0; i < data.size(); i++) {
                    //Log.d(TAG, data.get(i).getTitle());
                    text += data.get(i).getTitle() + "\n";
                }
                //text used needs to be final string as can't change after defined.
                final String toUpdate = text;
                //Allows us to update the UI thread from another thread
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = (TextView) rootView.findViewById(R.id.textview);
                        textView.setText(toUpdate);
                    }
                });

            }
        }
    }

}
