package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Register extends Message {
    private String username;
    private String password;
    private String birthday;
    private short opcode=1;

    public Register(byte[] msg){
        int indexStart = 0;
        int indexEnd = 0;

        //find userName
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.username = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);

        indexStart = indexEnd + 1;
        indexEnd++;

        //find password
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.password = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);

        indexStart = indexEnd + 1;
        indexEnd++;

        //find birthDay
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.birthday = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);
    }


    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        boolean regiStatus = data.attemptRegister(connectionId,username,password,birthday);
        HashMap<Message, Integer> toReturn = new HashMap<>();
        if(regiStatus){
            toReturn.put(new ACK(null, opcode), connectionId);
        }
        else{
            toReturn.put(new Error(opcode), connectionId);
        }
        return toReturn;
    }

}
