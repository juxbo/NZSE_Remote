package remote.js.nzse.hda.makeremotesgreatagain;

import java.io.Serializable;

/**
 * Created by jux on 11.11.2016.
 */

public class Sender implements Serializable {
    private String channel;
    private String program;
    private int quality;
    private int frequency;
    private String provider;

    public Sender(final String channel, final String program, final int quality, final int frequency, final String provider) {
        this.channel = channel;
        this.program = program;
        this.quality = quality;
        this.frequency = frequency;
        this.provider = provider;
    }

    @Override
    public String toString() {
        //TODO: Either remove channel or somehow display a separate consecutive channel-nr in spinner/senderliste
        final StringBuilder sb = new StringBuilder();
        sb.append(channel).append(". ").append(program);
        return sb.toString();
    }

    public String getChannel() {
        return channel;
    }
}
