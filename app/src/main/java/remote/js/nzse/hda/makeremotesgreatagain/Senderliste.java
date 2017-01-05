package remote.js.nzse.hda.makeremotesgreatagain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jux on 11.11.2016.
 */

public class Senderliste {
    private static List<Sender> sender;

    public Senderliste() {
        sender = new ArrayList<>();
//        fillSenderListWithTestData();
    }

    private void fillSenderListWithTestData() {
        // Sender(channel, program, quality, frequency, provider)
        sender.add(new Sender("22a", "ZDF", 58, 546000, "ZDFmobil"));
        sender.add(new Sender("57d", "Das Erste", 91, 602000, "ARD"));
        sender.add(new Sender("37b", "hr-fernsehen", 91, 658000, "SWR"));
        sender.add(new Sender("37c", "arte", 91, 602000, "ARD"));
    }

    public Sender findSenderByNumber(int nr){
        // This might be a bit confusing but it works ¯\_(ツ)_/¯
        return sender.get(nr%sender.size());
    }

    public void addSender(Sender send) {
        sender.add(send);
    }

    public List<Sender> getSender() {
        return sender;
    }

    public void setSender(List<Sender> sender) {
        this.sender = sender;
    }
}
