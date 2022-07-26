package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Follow extends Message {

    private short  opcode=4;
    private boolean type; // true for follow or false for unfollow
    private String username;

    public Follow(byte[] msg){
        if(msg[0] == 0)
            type = false;
        else
            type= true;

        //find userName
        this.username = new String(msg, 1,msg.length-1 , StandardCharsets.UTF_8);
    }

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        boolean success = data.follow(type, connectionId, username);
        if (success)
            toReturn.put(new ACK(username.getBytes(StandardCharsets.UTF_8), opcode), connectionId);
        else
            toReturn.put(new Error(opcode), connectionId);
        return toReturn;
        }

}
