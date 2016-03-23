package examples.csci567.locationpicker12;

import com.google.android.gms.location.places.Place;

/**
 * Created by bryandixon on 3/3/16.
 */
public class PlaceItem {
    private Place place;

    public PlaceItem(Place place){
        this.place = place;
    }

    public void setPlace(Place place){
        this.place = place;
    }

    public Place getPlace(){
        return this.place;
    }

}
