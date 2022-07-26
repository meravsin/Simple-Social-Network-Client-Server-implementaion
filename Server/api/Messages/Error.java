package bgu.spl.net.api.Messages;


import bgu.spl.net.api.bidi.Message;

import java.nio.ByteBuffer;

public class Error extends Message {
    private short opcode=11;
    private short messageOpcode;

    public Error(short messageOpcode){
        this.messageOpcode = messageOpcode;
    }


    @Override
    public byte[] encode() {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putShort(opcode);
        bb.putShort(messageOpcode);
        return bb.array();
    }
}
