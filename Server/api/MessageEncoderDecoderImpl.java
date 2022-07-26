package bgu.spl.net.api;

import bgu.spl.net.api.Messages.*;
import bgu.spl.net.api.Messages.Error;
import bgu.spl.net.api.bidi.Message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements MessageEncoderDecoder<Message>{

    private byte[] messageArr;
    private int index=0;
    int len = 0;
    private ByteBuffer opcode = ByteBuffer.allocate(2);

    public MessageEncoderDecoderImpl(){
        opcode.order(ByteOrder.LITTLE_ENDIAN);
        messageArr = new byte[1 << 10];
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        if(nextByte==';'){
            opcode.flip();
            Message msg =  buildMessageObject(messageArr, bytesToShort(opcode.array()));
            clear();
            return msg;
        }
        else {
            if (index < 2) {
                opcode.put(nextByte);
                index++;
                return null;
            } else {
                pushByte(nextByte);
                return null;
            }
        }
    }


    public short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    private Message buildMessageObject(byte[] message, short opcode){
        if (opcode==1)
            return new Register(message);
        else if (opcode==2)
            return new Login(message);
        else if (opcode==3)
            return new Logout();
        else if (opcode==4)
            return new Follow(message);
        else if (opcode==5)
            return new Post(message);
        else if (opcode==6)
            return new PM(message);
        else if (opcode==7)
            return new Logstat();
        else if (opcode==8)
            return new Stat(message);
        else if (opcode==13)
            return new Block(message);
        else return null; // should never reach this option
    }

    @Override
    public byte[] encode(Message message) {
        return message.encode();

    }

    private void clear(){
        messageArr = new byte[1 << 10];//יש מצב שפה נדפקו דברים!!
        opcode.clear();
        index=0;
    }

    private void pushByte(byte nextByte) {
        if (len >= messageArr.length) {
            messageArr = Arrays.copyOf(messageArr, len * 2);
        }

        messageArr[len++] = nextByte;
    }
}
