package csci567.loginactivity;

/**
 * Created by bryandixon on 4/10/16.
 */
public class FirebaseItem {
    private String message;
    //private String uid;

    public FirebaseItem() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }

    public FirebaseItem(String message){
        this.message=message;
        //this.uid = uid;
    }

    public String getMessage(){
        return message;
    }



    public void setMessage(String message){
        this.message=message;
    }
    /*  public String getUid(){
        return uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }*/


}
