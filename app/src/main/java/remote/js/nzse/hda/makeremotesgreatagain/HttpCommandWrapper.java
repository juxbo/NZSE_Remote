package remote.js.nzse.hda.makeremotesgreatagain;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Selim on 13.11.2016.
 */

public class HttpCommandWrapper {
    private final String host;
    private final Context context;

    public HttpCommandWrapper(String host, Context context) {
        this.host = host;
        this.context = context;
    }

    public void setDebug(final boolean debug) {
        String param = "debug=" + (debug ? 1 : 0);
        executeAsync(500, param);
    }

    public void setVolume(final int value) {
        String param = "volume=" + value;
        executeAsync(500, param);
    }

    public void setChannelMain(final String channel) {
        String param = "channelMain=" + channel;
        executeAsync(500, param);
    }

    public void showBlackBars(final boolean show) {
        String param = "zoomMain=" + (show ? 1 : 0);
        executeAsync(500, param);
    }

    public void setPipEnabled(final boolean value) {
        String param = "showPip=" + (value ? 1 : 0);
        executeAsync(500, param);
    }

    public void setChannelPip(final String channel) {
        String param = "channelPip=" + channel;
        executeAsync(500, param);
    }

    public void showBlackBarsInPip(final boolean show) {
        String param = "zoomPip=" + (show ? 1 : 0);
        executeAsync(500, param);
    }

    public void pause() {
        String param = "timeShiftPause=";
        executeAsync(500, param);
    }

    public void play(final long offset) {
        String param = "timeShiftPlay=" + offset;
        executeAsync(500, param);
    }

    public ArrayList<Sender> scanChannels() throws JSONException, IOException {
        //@Selim: Pls fix I don't know how to Json
        String param = "scanChannels=";
        ArrayList<Sender> toReturn = new ArrayList<>();

        JSONObject senderJson = executeHttpRequest(5000, param);
        if (senderJson != null) {
            JSONArray senderArray = senderJson.getJSONArray("channels");
            if (senderArray != null) {
                for (int i = 0; i < senderArray.length(); i++) {
                    JSONObject channelObject = senderArray.getJSONObject(i);
                    toReturn.add(new Sender(channelObject.getString("channel"), channelObject.getString("program"), channelObject.getInt("quality"), channelObject.getInt("frequency"), channelObject.getString("provider")));
                }
            }
        }

        return toReturn;
    }

    public void setStandBy(final boolean value) {
        String param = "standby=" + (value ? 1 : 0);
        executeAsync(500, param);
    }

    private void executeAsync(final int timeout, String param) {
        new AsyncTask<String, Void, Void>() {

            @Override
            protected void onPreExecute() {
                //Empty
            }

            @Override
            protected Void doInBackground(String... params) {
                try {
                    executeHttpRequest(timeout, params);
                } catch (JSONException | IOException e) {
//                    TODO: Find a way to throw this. It can only to be thrown from UI Thread
//                    Toast.makeText(context, "Fehler beim Verbinden mit TV", Toast.LENGTH_LONG);
                    Log.e("Fernbedienung", e.getMessage());
                }
                return null;
            }
        }.execute(param);
    }

    private JSONObject executeHttpRequest(int timeout, String... params) throws JSONException, IOException {
        if (params.length < 1) {
            //Fehler no params
            return null;
        }
        HttpRequest request = new HttpRequest(host, timeout, true);

        return request.execute(params[0]);
    }
}
