package examples.csci567.sugarormlecture6;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orm.SugarContext;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.editText1)
    EditText editText;

    @Bind(R.id.textView1)
    TextView textView;

    @OnClick(R.id.buttonAdd)
    public void buttonAdd(View view) {
        Entries entry = new Entries(editText.getText().toString());
        entry.save();
    }

    @OnClick(R.id.buttonGet)
    public void buttonGet(View view){
        List<Entries> entries = Entries.listAll(Entries.class);
        String toReturn = "";
        for(int i = 0; i<entries.size();i++){
            toReturn += entries.get(i).getEntry() + "\n";
        }
        textView.setText(toReturn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SugarContext.init(this);
        ButterKnife.bind(this);
    }
}
