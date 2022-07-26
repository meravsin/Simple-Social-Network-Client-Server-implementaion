package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Notification extends Message {
    Byte notificationType;
    String sender;
    String content;
    short opcode = 9;


    public Notification(byte type,String sender, String content){
        this.notificationType =type;
        this.sender =sender;
        this.content =content;
    }


    @Override
    public byte[] encode() {
        byte[] encodedSender = sender.getBytes(StandardCharsets.UTF_8);
        byte[] encodedContent = sender.getBytes(StandardCharsets.UTF_8);
        int len = encodedSender.length + encodedContent.length + 3;
        ByteBuffer bb = ByteBuffer.allocate(len);
        bb.putShort(opcode);
        bb.put(notificationType);
        bb.put(encodedSender);
        bb.put((byte)0);
        bb.put(encodedContent);
        bb.put((byte)0);
        return bb.array();
    }
}
