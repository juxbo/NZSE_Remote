package remote.js.nzse.hda.makeremotesgreatagain;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Fernbedienung extends AppCompatActivity {

    public static final String IP_ADRESS = "192.168.178.43";
    private static final int SPULEN_BY =10;
    private SharedPreferences prefs = null;
    private Spinner sender;
    private SeekBar vol;
    private boolean muted=false;
    private int volCache=50;
    private boolean paused=false;
    private boolean blackbars=true;
    private Senderliste sliste;
    private HttpCommandWrapper cmd;
    private int lastChannelNr;
    private int pipChannelNr;
    private long timePaused;
    private int spulenOffset;
    private ImageButton ffBtn;
    private ImageButton rewindBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fernbedienung);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);

        //Somehow these cannot be disabled in xml (They're not greyed out)
        ffBtn=(ImageButton) findViewById(R.id.button_ff);
        rewindBtn=(ImageButton) findViewById(R.id.button_rewind);
        ffBtn.setEnabled(false);
        rewindBtn.setEnabled(false);
        setPipButtonsEnabled(false);

        lastChannelNr=0;

        sender=(Spinner) findViewById(R.id.spinner_sender);
        sliste=new Senderliste();
        List<Sender> slistFromIntent = (ArrayList<Sender>) getIntent().getSerializableExtra("sliste");
        if (slistFromIntent != null && !slistFromIntent.isEmpty()) {
            sliste.setSender(slistFromIntent);
        }
        ArrayAdapter<Sender> senderAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner, sliste.getSender());
        sender.setAdapter(senderAdapter);

        sender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                lastChannelNr=position;
                cmd.setChannelMain(sliste.findSenderByNumber(lastChannelNr).getChannel());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        vol=(SeekBar) findViewById(R.id.bar_volume);
        vol.setProgress(volCache);
        vol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int i;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                this.i = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Not necessary
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cmd.setVolume(i);
            }
        });

        Switch pipSwitch = (Switch)findViewById(R.id.switch_pip);
        pipSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    cmd.setPipEnabled(b);
                    setPipButtonsEnabled(b);
            }
        });

        cmd = new HttpCommandWrapper(IP_ADRESS);
        cmd.setDebug(true);
        cmd.setStandBy(false);
        cmd.setChannelMain(sliste.findSenderByNumber(lastChannelNr).getChannel());
    }

    private void setPipButtonsEnabled(final boolean enabled) {
        pipChannelNr=0;
        if(enabled){
            findViewById(R.id.layout_mainPip).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_pipSwap).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_pipChannel).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.layout_mainPip).setVisibility(View.GONE);
            findViewById(R.id.layout_pipSwap).setVisibility(View.GONE);
            findViewById(R.id.layout_pipChannel).setVisibility(View.GONE);
        }

        findViewById(R.id.button_piplast).setEnabled(enabled);
        findViewById(R.id.button_pipnext).setEnabled(enabled);
        findViewById(R.id.button_pipswap).setEnabled(enabled);
    }

    public void pipSwapButtonPressed(View v){
        int temp=lastChannelNr;
        lastChannelNr=pipChannelNr;
        pipChannelNr=temp;

        cmd.setChannelPip(sliste.findSenderByNumber(pipChannelNr).getChannel());
        cmd.setChannelMain(sliste.findSenderByNumber(lastChannelNr).getChannel());
    }
    public void pipNextChannelButtonPressed(View v){
        pipChannelNr++;
        cmd.setChannelPip(sliste.findSenderByNumber(pipChannelNr).getChannel());
    }
    public void pipLastChannelButtonPressed(View v){
        pipChannelNr--;
        if(pipChannelNr<0) {
            pipChannelNr = sliste.getSender().size();
        }
        cmd.setChannelPip(sliste.findSenderByNumber(pipChannelNr).getChannel());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true)) {
            //Open Rollenauswahl on first run
            showChangeRoleSpinner();
            prefs.edit().putBoolean("firstrun", false).commit();
        }
        //TODO: Pretty much the only thing to do. Finish onResume and OnPause to save and get the data we need to save to enable smooth multitasking
    }

    @Override
    protected void onPause() {
        super.onPause();
        //TODO: Find out what to do here (See onResume)
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
    //TODO: Fix bug: When you miss the Dialog it doesn't go into "default:" and instead just dismisses the dialog
    private void showChangeRoleSpinner(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Was beschreibt sie am Besten?");
        String[] types = {"Kind", "Renter", "Hausfrau", "Fernsehnutzer", "Technik Profi", "Heimkino Enthusiast", "Ich weiß nicht"};
        b.setItems(types, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: Change this to correct roles. Hide the features a role shouldn't have
                dialog.dismiss();
                switch(which){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        //Rolle noob
                        findViewById(R.id.pip).setVisibility(View.GONE);
                        break;
                    case 4:
                    case 5:
                        //Rolle pro
                        findViewById(R.id.pip).setVisibility(View.VISIBLE);
                        break;
                    default:
                        //Keine Rolle. Noob benutzen?
                        findViewById(R.id.pip).setVisibility(View.GONE);
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
            cmd.play(calculateOffset(spulenOffset));
            ffBtn.setEnabled(false);
            rewindBtn.setEnabled(false);
            paused=false;
        }else{
            Toast.makeText(this, "Pausing", Toast.LENGTH_SHORT).show();
            ((ImageButton)findViewById(R.id.button_play)).setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.play));
            cmd.pause();
            timePaused=System.currentTimeMillis();
            ffBtn.setEnabled(true);
            paused=true;
        }
    }

    public void ffButtonPressed(View v){
        if(calculateOffset(spulenOffset)> SPULEN_BY){
            spulenOffset-= SPULEN_BY;
            Toast.makeText(this, "-"+SPULEN_BY+" => Neuer Offset: "+calculateOffset(spulenOffset), Toast.LENGTH_SHORT).show();
            if(calculateOffset(spulenOffset)>= SPULEN_BY){
                ffBtn.setEnabled(false);
            }
            if(calculateOffset(spulenOffset)<calculateOffset(SPULEN_BY)) {
                rewindBtn.setEnabled(true);
            }
        }
    }
    public void rewindButtonPressed(View v){
        if(calculateOffset(spulenOffset)<calculateOffset(SPULEN_BY)){
            spulenOffset+= SPULEN_BY;
            Toast.makeText(this, "+"+SPULEN_BY+" => Neuer Offset: "+calculateOffset(spulenOffset), Toast.LENGTH_SHORT).show();
            if(calculateOffset(spulenOffset)<calculateOffset(0)){
                rewindBtn.setEnabled(false);
            }
            if(calculateOffset(spulenOffset)> SPULEN_BY) {
                ffBtn.setEnabled(true);
            }
        }
    }

    private long calculateOffset(int spulOffset) {
        return ((System.currentTimeMillis()-timePaused)/1000)+spulOffset;
    }

    public void lastChannelButtonPressed(View v){
        Toast.makeText(this, "Last Channel", Toast.LENGTH_SHORT).show();
        changeSender(-1);
//        Not necessary after implementing the changeListener of sender spinner
//        lastChannelNr--;
//        cmd.setChannelMain(sliste.findSenderByNumber(lastChannelNr).getChannel());
    }

    public void nextChannelButtonPressed(View v){
        Toast.makeText(this, "Next Channel", Toast.LENGTH_SHORT).show();
        changeSender(1);
//        Not necessary after implementing the changeListener of sender spinner
//        lastChannelNr++;
//        cmd.setChannelMain(sliste.findSenderByNumber(lastChannelNr).getChannel());
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
