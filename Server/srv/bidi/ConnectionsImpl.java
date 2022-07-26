package bgu.spl.net.srv.bidi;

import bgu.spl.net.api.bidi.Message;
import bgu.spl.net.srv.objects.User;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {


    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionsHandlers=new ConcurrentHashMap<>();
    int idCounter=0;

    private ConnectionsImpl(){
        DataManager.getInstance().updateFilter();
    }

    private static class Con{
        private static ConnectionsImpl instance  = new ConnectionsImpl();
    }

    public static ConnectionsImpl getInstance(){
        return Con.instance;
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if (connectionsHandlers.containsKey(connectionId)){
            connectionsHandlers.get(connectionId).send(msg);
            return true;
        }
        else
            return false;
    }

    @Override
    public void broadcast(T msg) {
        for (ConnectionHandler<T> c : connectionsHandlers.values())
            c.send(msg);
    }

    @Override
    public void disconnect(int connectionId) {
        connectionsHandlers.remove(connectionId);

    }

    public synchronized int addNewConnection(ConnectionHandler<T> handler){
        idCounter++;
        connectionsHandlers.put(idCounter,handler);
        return idCounter;
    }

}
