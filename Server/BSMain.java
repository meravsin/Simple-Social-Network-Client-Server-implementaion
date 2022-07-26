package bgu.spl.net;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.bidi.ConnectionsImpl;

import java.io.IOException;
import java.util.function.Supplier;

public class BSMain {
    public static void main(String[] args){
        try(
        Server server = Server.ThreadPerClient(Integer.parseInt(args[0]),() -> new BidiMessagingProtocolImpl(),
                () -> new MessageEncoderDecoderImpl());)
        {server.serve();}
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
