package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.util.HashMap;
import java.util.LinkedList;

public class Logstat extends Message {
    private short opcode=7;


    public Logstat(){}

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        LinkedList<byte[]> stat = data.logStat(connectionId);
        if (stat == null)
            toReturn.put(new Error(opcode), connectionId);
        else {
            for (byte[] currStat : stat)
                toReturn.put(new ACK(currStat, opcode), connectionId);
        }
        return toReturn;
    }

}
