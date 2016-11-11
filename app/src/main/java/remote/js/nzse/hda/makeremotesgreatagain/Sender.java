package remote.js.nzse.hda.makeremotesgreatagain;

import java.io.Serializable;

/**
 * Created by jux on 11.11.2016.
 */

public class Sender implements Serializable{
    private int number;
    private String title;

    public Sender(final int nr,final String tit){
        number=nr;
        title=tit;
    }
    @Override
    public String toString(){
        return this.number+". "+this.title;
    }
}
