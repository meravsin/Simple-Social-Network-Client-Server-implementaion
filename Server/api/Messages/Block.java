package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Block extends Message {
    private short opcode = 11;
    private String username;

    public Block(byte[] msg){
        int indexEnd = 0;

        //find username
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.username = new String(msg, 0,indexEnd , StandardCharsets.UTF_8);
    }

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        boolean success = data.blockUser(connectionId, username);
        if (success) {
            toReturn.put(new ACK(null, opcode), connectionId);
        }
        else
            toReturn.put(new Error(opcode), connectionId);
        return toReturn;
    }

}
