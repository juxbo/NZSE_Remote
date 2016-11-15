package remote.js.nzse.hda.makeremotesgreatagain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SenderlisteActivity extends AppCompatActivity {

    private ArrayList<Sender> senderliste;
    private ListView sender;
    private ArrayAdapter senderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senderliste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSenderliste);
        setSupportActionBar(toolbar);

        senderliste=(ArrayList<Sender>)getIntent().getSerializableExtra("sliste");

        sender=(ListView) findViewById(R.id.listview_sender);
        this.senderAdapter = new ArrayAdapter(this,R.layout.custom_listview, R.id.listview_textView, senderliste);
        sender.setAdapter(senderAdapter);

        //TODO: Somehow find out what button has been pressed (which line of listview)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_senderliste, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_scan:
                Toast.makeText(this, "Scan", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onDeleteRowButtonClicked(View v){
        Toast.makeText(this, "Clicked some Delete Button", Toast.LENGTH_SHORT).show();
        final int position = sender.getPositionForView((View) v.getParent());
        senderliste.remove(position);
        senderAdapter.notifyDataSetChanged();
    }

    public void onArrowUpClicked(View v) {
        final int position = sender.getPositionForView((View) v.getParent());
        Sender s = senderliste.get(position);
        senderliste.remove(position);
        senderliste.add(position - 1, s);
        senderAdapter.notifyDataSetChanged();
    }

    public void onArrowDownClicked(View v) {
        final int position = sender.getPositionForView((View) v.getParent());
        Sender s = senderliste.get(position);
        senderliste.remove(position);
        senderliste.add(position + 1, s);
        senderAdapter.notifyDataSetChanged();
    }
}
