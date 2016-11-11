package remote.js.nzse.hda.makeremotesgreatagain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class SenderlisteActivity extends AppCompatActivity {

    private ArrayList<Sender> senderliste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senderliste);
        senderliste=(ArrayList<Sender>)getIntent().getSerializableExtra("sliste");

        //TODO: Make some sort of list view with buttons and stuff using the senderliste arraylist
        for(Sender s:senderliste){
            Toast.makeText(this, s.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
