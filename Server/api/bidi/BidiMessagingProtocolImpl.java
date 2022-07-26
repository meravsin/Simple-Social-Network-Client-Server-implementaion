package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionsImpl;
import bgu.spl.net.srv.bidi.DataManager;

import java.util.HashMap;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {

    private Connections<Message> con = ConnectionsImpl.getInstance();
    private DataManager data = DataManager.getInstance();
    private int connectionId;


    public BidiMessagingProtocolImpl(){
    }

    @Override
    public void start(int connectionId, Connections connections) {
        con= connections;
        System.out.println(connectionId);
        this.connectionId = connectionId;
    }

    @Override
    public void process(Message message) {
        HashMap<Message,Integer> messageHashMap = message.execute(data, connectionId);
        for(Message curr: messageHashMap.keySet()){
            con.send(messageHashMap.get(curr), curr);
        }
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }

}
