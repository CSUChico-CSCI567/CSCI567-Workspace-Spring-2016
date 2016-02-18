package examples.csci567.sharedpreferenceslecture8;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Preference Example";
    public static final String PREFS_NAME = "MyPrefsFile";

    @Bind(R.id.checkbox1)
    CheckBox checkBox1;

    @Bind(R.id.checkbox2) CheckBox checkBox2;

    @OnClick(R.id.checkbox1)
    public void Checbox1OnClick(View view) {
        Log.d(TAG, String.valueOf(checkBox1.isChecked()));
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox1", checkBox1.isChecked());

        // Commit the edits!
        editor.commit();
    }

    @OnClick(R.id.checkbox2)
    public void Checbox2OnClick(View view) {
        Log.d(TAG, String.valueOf(checkBox2.isChecked()));
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox2", checkBox2.isChecked());

        // Commit the edits!
        editor.commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean checkbox1setting = settings.getBoolean("checkbox1", false);
        checkBox1.setChecked(checkbox1setting);
        boolean checkbox2setting = settings.getBoolean("checkbox2", false);
        checkBox2.setChecked(checkbox2setting);
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
            Log.d(TAG, "Settings selected");
            return true;
        }

        if (id == R.id.option2) {
            Log.d(TAG, "Option 2 selected");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
