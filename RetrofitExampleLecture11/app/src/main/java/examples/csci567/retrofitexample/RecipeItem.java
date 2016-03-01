package examples.csci567.retrofitexample;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bryandixon on 6/9/15.
 */
public class RecipeItem {
    private static final String TAG = RecipeItem.class.getSimpleName();


    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_url")
    @Expose
    private String imageURL;
    @SerializedName("source_url")
    @Expose
    private String sourceURL;
    @SerializedName("f2f_url")
    @Expose
    private String f2fURL;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("publisher_url")
    @Expose
    private String publisherURL;
    @SerializedName("social_rank")
    @Expose
    private String socialRank;
    @SerializedName("page")
    @Expose
    private Integer page;


    public RecipeItem(String title){
        this.title = title;
    }
    /**
     *
     * @return
     * The recipe title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSocialRank(){ return socialRank;}

    public String getImageURL(){ return imageURL;}


}
