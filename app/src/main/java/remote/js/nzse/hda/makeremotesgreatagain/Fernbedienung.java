package remote.js.nzse.hda.makeremotesgreatagain;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Fernbedienung extends AppCompatActivity {

    public static final String IPAdress = "192.168.178.43";
    private Spinner sender;
    private SeekBar vol;
    private boolean muted=false;
    private int volCache=50;
    private boolean paused=false;
    private boolean blackbars=true;
    private Senderliste sliste;
    private HttpCommandWrapper cmd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fernbedienung);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.setProperty("java.net.preferIPv4Stack" , "true");

        sender=(Spinner) findViewById(R.id.spinner_sender);
        sliste=new Senderliste();
        ArrayAdapter senderAdapter = new ArrayAdapter(this, R.layout.custom_spinner, sliste.getSender());
        sender.setAdapter(senderAdapter);

        vol=(SeekBar) findViewById(R.id.bar_volume);
        vol.setProgress(volCache);

        cmd = new HttpCommandWrapper(IPAdress);
        cmd.setStandBy(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fernbedienung, menu);
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
            case R.id.action_changeAspectRatio:
                Toast.makeText(this, "Change aspect ratio", Toast.LENGTH_SHORT).show();
                blackbars = !blackbars;
                cmd.showBlackBars(blackbars);
                break;
            case R.id.action_changeRole:
                Toast.makeText(this, "Change role", Toast.LENGTH_SHORT).show();
                showChangeRoleSpinner();
                break;
            case R.id.action_openSenderliste:
                Toast.makeText(this, "Open Senderliste", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SenderlisteActivity.class);
                intent.putExtra("sliste", (ArrayList) sliste.getSender());
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //This might be a bit overkill.
    private void showChangeRoleSpinner(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Was beschreibt sie am Besten?");
        String[] types = {"Kind", "Renter","Hausfrau","Fernsehnutzer","Nerd","Pro","Heimkino Enthusiast","Lass mich in Ruhe","Ich will nur Fernsehn schauen","Ich weiß nicht"};
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Do stuff whit whatever gets chosen
                dialog.dismiss();
                switch(which){
                    case 0:

                        break;
                    case 1:

                        break;
                }
            }

        });

        b.show();
    }
    public void playButtonPressed(View v){
        //TODO: Implement Play/Pause Function and enable ff/rewind buttons only when paused before
        if(paused){
            Toast.makeText(this, "Resuming", Toast.LENGTH_SHORT).show();
            ((ImageButton)findViewById(R.id.button_play)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.pause));
            //TODO: WTF is Offset?
            cmd.play(500);
            paused=false;
        }else{
            Toast.makeText(this, "Pausing", Toast.LENGTH_SHORT).show();
            ((ImageButton)findViewById(R.id.button_play)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.play));
            cmd.pause();
            paused=true;
        }
    }

    public void lastChannelButtonPressed(View v){
        Toast.makeText(this, "Last Channel", Toast.LENGTH_SHORT).show();
        changeSender(-1);
        //TODO: Send sender to TV
        cmd.setDebug(false);
    }

    public void nextChannelButtonPressed(View v){
        Toast.makeText(this, "Next Channel", Toast.LENGTH_SHORT).show();
        changeSender(1);
        //TODO: Send sender to TV
        cmd.setDebug(true);
    }

    private void changeSender(int by){
        if((sender.getSelectedItemPosition()+by)>=sender.getAdapter().getCount() || (sender.getSelectedItemPosition()+by)<0){
            if(by>0)
                sender.setSelection(0);
            else
                sender.setSelection(sender.getAdapter().getCount()-1);
        }else{
            sender.setSelection(sender.getSelectedItemPosition()+by);
        }
    }

    public void muteButtonPressed(View v){
        Toast.makeText(this, "Mute pressed", Toast.LENGTH_SHORT).show();
        if(muted){
            //Unmute
            vol.setEnabled(true);
            vol.setProgress(volCache);
            ((ImageButton)findViewById(R.id.button_mute)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.mute));
            cmd.setVolume(volCache);
            muted=false;
        }else{
            //Mute
            volCache=vol.getProgress();
            vol.setProgress(0);
            vol.setEnabled(false);
            ((ImageButton)findViewById(R.id.button_mute)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.unmute));
            cmd.setVolume(0);
            muted=true;
        }
    }
}
