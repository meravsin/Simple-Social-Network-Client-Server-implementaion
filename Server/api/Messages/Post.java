package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.bidi.DataManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;

public class Post extends Message {
    private short opcode=5;
    private String content;
    private LinkedList<String> hashtags= new LinkedList<>();

    public Post(byte[] msg){
        int indexStart = 0;
        int indexEnd = 0;

        //find content
        while(msg[indexEnd] != 0){
            indexEnd++;
        }
        this.content = new String(msg, indexStart,indexEnd , StandardCharsets.UTF_8);

        //find hashtags
        String[] temp =content.split(" ");
        for (String word : temp){
            if (word.startsWith("@"))
                hashtags.add(word);
        }
    }

    @Override
    public HashMap<Message, Integer> execute(DataManager data, int connectionId) {
        String sender = data.getUserName(connectionId);
        HashMap<Message, Integer> toReturn = new HashMap<>();
        LinkedList<String> toNotify = data.post(connectionId,content);
        if(toNotify==null){
            toReturn.put(new Error(opcode), connectionId);
            return toReturn;
        }
        toReturn.put(new ACK(null, opcode), connectionId);
        for(String username:hashtags){
            if(data.userExists(username)&&!toNotify.contains(username)){
                if(!data.blockCheck(sender,username)){
                    toNotify.add(username);
                }
            }
        }

        for(String username:toNotify){
            Message notification = new Notification((byte)1, sender,content);
            int sendToID = data.getConnID(username);
            if(sendToID!=-1){
                toReturn.put(notification,sendToID);
            }
            else{
                data.addAwaitingMessage(notification,username);
            }
        }

        return toReturn;
    }

}
