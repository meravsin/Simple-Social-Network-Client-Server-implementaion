package bgu.spl.net.srv.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    void broadcast(T msg);

    void disconnect(int connectionId);

    int addNewConnection(ConnectionHandler<T> connectionHandler);
}
