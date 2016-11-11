package remote.js.nzse.hda.makeremotesgreatagain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jux on 11.11.2016.
 */

public class Senderliste {
    private static ArrayList<Sender> sender=new ArrayList<>();

    public Senderliste(){
        setSender(new ArrayList<Sender>());
        fillSenderListWithTestData();
    }

    private void fillSenderListWithTestData(){
        getSender().add(new Sender(1,"ARD"));
        getSender().add(new Sender(2,"ZDF"));
        getSender().add(new Sender(3,"BibelTV"));
        getSender().add(new Sender(4,"AMKTVLangersendername"));
    }

    public ArrayList<Sender> getSender() {
        return sender;
    }

    public void setSender(ArrayList<Sender> sender) {
        this.sender = sender;
    }
}
