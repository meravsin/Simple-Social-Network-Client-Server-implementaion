package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class PM  extends Message {
    private short opcode = 6;
    private String username;
    private String content;
    private String time;

    public PM(byte[] msg) {
        int indexStart = 0;
        int indexEnd = 0;

        //find userName
        while (msg[indexEnd] != 0) {
            indexEnd++;
        }
        this.username = new String(msg, indexStart, indexEnd, StandardCharsets.UTF_8);

        indexStart = indexEnd + 1;
        indexEnd++;

        //find content
        while (msg[indexEnd] != 0) {
            indexEnd++;
        }
        this.content = new String(msg, indexStart, indexEnd, StandardCharsets.UTF_8);

        indexStart = indexEnd + 1;
        indexEnd++;

        //find time
        while (msg[indexEnd] != 0) {
            indexEnd++;
        }
        this.time = new String(msg, indexStart, indexEnd, StandardCharsets.UTF_8);
    }



    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        HashMap<Message, Integer> toReturn = new HashMap<>();
        int toSendConnectionId = data.sendPM(connectionId, username);
        if (toSendConnectionId == -1)
            toReturn.put(new Error(opcode), connectionId);
        else{
                toReturn.put(new ACK(null, opcode), connectionId);
                data.FilterString(content);
                Message notification = new Notification((byte)0, data.getUserName(connectionId),content);
                if (data.getUserStatus(username))
                    toReturn.put(notification, toSendConnectionId);
                else
                    data.addAwaitingMessage(notification, username);
            }
            return toReturn;

        }
}