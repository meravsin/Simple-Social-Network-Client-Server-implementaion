package bgu.spl.net.api.Messages;

import bgu.spl.net.api.bidi.Message;

import java.nio.ByteBuffer;

public class ACK extends Message {
    short opcode =10;
    short messageOpcode;
    byte[] extraContent;


    public ACK(byte[] optional, short MessageOpcode){
        this.extraContent = optional;
        this.messageOpcode = MessageOpcode;
    }



    @Override
    public byte[] encode() {
        ByteBuffer bb;
        if (extraContent == null){
            bb = ByteBuffer.allocate(4);
            bb.putShort(opcode);
            bb.putShort(messageOpcode);
        }
        else {
            bb = ByteBuffer.allocate(4+ extraContent.length);
            bb.putShort(opcode);
            bb.putShort(messageOpcode);
            bb.put(extraContent);
        }
        return bb.array();
    }
}
