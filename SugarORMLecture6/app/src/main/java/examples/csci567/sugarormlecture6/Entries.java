package examples.csci567.sugarormlecture6;

import com.orm.SugarRecord;

/**
 * Created by bryandixon on 2/16/16.
 */
public class Entries extends SugarRecord{
    String entry;

    public Entries() {
        super();
    }

    public Entries( String entry) {
        super();
        this.entry=entry;
    }

    public String getEntry(){
        return this.entry;
    }
}