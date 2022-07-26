package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.DataManager;

import java.util.HashMap;

public abstract class Message {
    enum Type{
        NULL,REGISTER,LOGIN,LOGOUT,FOLLOW,
        POST,PM,LOGSTAT,STAT,NOTIFICATION,
        ERROR,BLOCK;
    }
    private Type type;

    public byte[] encode (){//יש מצב שפה נדפקים דברים
        return null;
    }
    public String decode(){ return null;}

    public HashMap<Message, Integer> execute(DataManager data, int connectionId){
        return null;
    }
}
