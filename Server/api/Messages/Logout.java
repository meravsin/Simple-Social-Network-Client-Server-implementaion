package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.util.HashMap;

public class Logout extends Message {

    private short opcode=3;

    public Logout(){
    }

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        boolean success = data.logOut(connectionId);
        if (success)
            toReturn.put(new ACK(null, opcode), connectionId);
        else
            toReturn.put(new Error(opcode), connectionId);
        return toReturn;
    }

}
