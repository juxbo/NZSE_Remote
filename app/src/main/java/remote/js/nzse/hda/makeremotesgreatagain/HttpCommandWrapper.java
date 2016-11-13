package remote.js.nzse.hda.makeremotesgreatagain;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Selim on 13.11.2016.
 */

public class HttpCommandWrapper {
    private final String host;

    public HttpCommandWrapper(String host) {
        this.host = host;
    }

    public void setDebug(final boolean debug) {
        String param = "debug=" + (debug ? 1 : 0);
        execute(param, 500);
    }

    public void setVolume(final int value) {
        String param = "volume=" + value;
        execute(param, 500);
    }

    public void setChannelMain(final String channel) {
        String param = "channelMain=" + channel;
        execute(param, 500);
    }

    public void showBlackBars(final boolean show) {
        String param = "zoomMain=" + (show ? 1 : 0);
        execute(param, 500);
    }

    public void setPipEnabled(final boolean value) {
        String param = "showPip=" + (value ? 1 : 0);
        execute(param, 500);
    }

    public void setChannelPip(final String channel) {
        String param = "channelPip=" + channel;
        execute(param, 500);
    }

    public void showBlackBarsInPip(final boolean show) {
        String param = "zoomPip=" + (show ? 1 : 0);
        execute(param, 500);
    }

    public void pause() {
        String param = "timeShiftPause=";
        execute(param, 500);
    }

    public void play(final int offset) {
        String param = "timeShiftPlay=" + offset;
        execute(param, 500);
    }

    public List<Sender> scanChannels() {
        String param = "scanChannels=";
        execute(param, 5000);
        return Collections.emptyList();
    }

    public void setStandBy(final boolean value) {
        String param = "standby=" + (value ? 1 : 0);
        execute(param, 500);
    }

    private void execute(String param, int timeout) {
        HttpRequest request = new HttpRequest(host, timeout, true);
        try {
            request.execute(param);
        } catch (JSONException | IOException e) {
            Log.e("Fernbedienung", e.getMessage());
        }
    }
}
