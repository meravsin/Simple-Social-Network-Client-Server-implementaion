package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Stat extends Message {
    private short opcode;
    private String[] usernames;

    public Stat(byte[] msg){
        int indexEnd = 0;

        //find usernames
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.usernames = new String(msg, 0,indexEnd , StandardCharsets.UTF_8).split(Pattern.quote("|"));
    }

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        LinkedList<byte[]> stat = data.Stat(connectionId, usernames);
        if (stat == null)
            toReturn.put(new Error(opcode), connectionId);
        else {
            for (byte[] currStat : stat)
                toReturn.put(new ACK(currStat, opcode), connectionId);
        }
        return toReturn;
    }

}
