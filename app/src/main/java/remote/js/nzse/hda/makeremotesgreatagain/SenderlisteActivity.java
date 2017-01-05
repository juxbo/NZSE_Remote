package remote.js.nzse.hda.makeremotesgreatagain;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class SenderlisteActivity extends AppCompatActivity {

    private ArrayList<Sender> senderliste;
    private ListView sender;
    private ArrayAdapter senderAdapter;
    private boolean scanHappening;
    private int rolle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senderliste);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSenderliste);
        setSupportActionBar(toolbar);

        senderliste=(ArrayList<Sender>)getIntent().getSerializableExtra("sliste");
        rolle = getIntent().getIntExtra("rolle", 4);

        sender=(ListView) findViewById(R.id.listview_sender);
        this.senderAdapter = new ArrayAdapter(this,R.layout.custom_listview, R.id.listview_textView, senderliste);
        sender.setAdapter(senderAdapter);

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        scanHappening=false;
    }

    @Override
    public void onBackPressed() {
        if (!scanHappening) {
        Intent intent = new Intent(getApplicationContext(), Fernbedienung.class);
        intent.putExtra("sliste", senderliste);
            intent.putExtra("rolle", rolle);
        startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_senderliste, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(scanHappening){
            menu.getItem(0).setEnabled(false);
        }
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
                scanChannels();
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void scanChannels() {
        scanHappening=true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        invalidateOptionsMenu();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPostExecute(Void result) {
                Toast.makeText(getApplicationContext(), "Sendersuchlauf abgeschlossen", Toast.LENGTH_SHORT).show();
                runOnUiThread(new Runnable() {
                    public void run() {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Intent intent = new Intent(getApplicationContext(), SenderlisteActivity.class);
                        intent.putExtra("sliste", senderliste);
                        intent.putExtra("rolle", rolle);
                        finish();
                        startActivity(intent);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                });
            }

            @Override
            protected void onPreExecute() {
                //Toast.makeText(getApplicationContext(), "Sendersuchlauf gestartet", Toast.LENGTH_SHORT).show();
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

            }

            @Override
            protected Void doInBackground(Void... voids) {
                HttpCommandWrapper commandWrapper = new HttpCommandWrapper(Fernbedienung.IP_ADRESS, getApplicationContext());
                try {
                    ArrayList<Sender> sl = commandWrapper.scanChannels();
                    if(!sl.isEmpty()){
                        senderliste = sl;
                    }else{
                        //Something is really wrong.
                        throw new JSONException("Senderliste leer");
                    }

                }catch (JSONException | IOException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Kann nicht mit Fernseher verbinden.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e("Senderscan", e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    private void clearList() {
        senderliste.clear();
        senderAdapter.notifyDataSetChanged();
    }

    public void onDeleteRowButtonClicked(View v){
//        Toast.makeText(this, "Clicked some Delete Button", Toast.LENGTH_SHORT).show();
        final int position = sender.getPositionForView((View) v.getParent());
        senderliste.remove(position);
        senderAdapter.notifyDataSetChanged();
    }

    public void onArrowUpClicked(View v) {
        final int position = sender.getPositionForView((View) v.getParent());
        Sender s = senderliste.get(position);
        senderliste.remove(position);
        if(position <= 0){
               senderliste.add(senderliste.size(),s);
        }else {
            senderliste.add(position - 1, s);
        }
        senderAdapter.notifyDataSetChanged();
    }

    public void onArrowDownClicked(View v) {
        final int position = sender.getPositionForView((View) v.getParent());
        Sender s = senderliste.get(position);
        senderliste.remove(position);
        if(position >= senderliste.size()){
            senderliste.add(0,s);
        }else {
            senderliste.add(position + 1, s);
        }
        senderAdapter.notifyDataSetChanged();
    }
}
