package examples.csci567.retrofitexample;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Query;

/**
 * Created by bryandixon on 6/8/15.
 */
public interface Recipes {

    @Headers("Accept: application/json")
    @GET("/search")
    void recipeList(@Query("key") String param1, @Query("q") String query, @Query("sort") String sort, @Query("page") int page, Callback<RecipeList> cb);

    @GET("/get")
    void getRecipe(@Query("key") String key, @Query("rId") int id, Callback<RecipeItem> cb);
}
