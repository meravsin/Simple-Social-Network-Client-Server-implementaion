package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Login extends Message {
    private final String userName;
    private final String password;
    private byte Captcha;
    short opcode = 2;

    public Login(byte[] msg){
        int indexStart = 0;
        int indexEnd = 0;

        //find userName
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.userName = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);
        indexStart = indexEnd + 1;
        indexEnd++;

        //find password
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.password = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);
    }



    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        Queue<Message> success = data.attemptLogin(connectionId, userName, password);
        HashMap<Message, Integer> toReturn = new HashMap<>();
        // user cannot log in
        if (success == null) {
            toReturn.put(new Error(opcode), connectionId);
            return toReturn;
        } else {
            for (Message message : success)
                toReturn.put(message, connectionId);
            toReturn.put(new ACK(null, opcode), connectionId);
        }
        return toReturn;
    }

}
