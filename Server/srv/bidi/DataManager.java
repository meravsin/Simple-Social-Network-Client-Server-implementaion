package bgu.spl.net.srv.bidi;

import bgu.spl.net.srv.objects.User;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.Period;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataManager<T> {
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer,String> connectedUsers = new ConcurrentHashMap<>();

    private LinkedList<String> filter = new LinkedList<>();
//    private ConcurrentHashMap<String , LinkedList<String>> Followers = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String , LinkedList<String>> Followings = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String , LinkedList<String>> PM = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<Integer , String> Posts = new ConcurrentHashMap<>();

    private static class Holder{
        private static DataManager instance  = new DataManager();
    }

    private DataManager(){}

    public  void FilterString(String text){
        for(String word:filter){
            text.replaceAll(word, "<filtered>");
        }
    }


    public void updateFilter(){
        String[] toFilter = {"word","word2","word3"};
        for(String word:toFilter){
            filter.add(word);
        }
    }

    public static DataManager getInstance(){
        return Holder.instance;
    }


    public boolean getUserStatus(String username){
        return users.get(username).getStatus();
    }

    public String getUserName(int connID) {
        return connectedUsers.get(connID);
    }

    private User getUser(int connID){
        String username = connectedUsers.get(connID);
        if(username==null) return null;
        return users.get(username);
    }

    public int getConnID(String username){
        return users.get(username).getConnectionID();
    }
    public boolean userExists(String username){
        return users.contains(username);
    }
    public boolean follow(boolean request, int connID, String sendTo){
        User user = getUser(connID);
        if(user==null) return false;

        User other = users.get(sendTo);
        if(other==null||other.isBlocking(user)||user.isBlocking(other)) return false;

        if(request){//request type is add follow
            return other.addFollower(user);
        }
        else{//request type is remove follow
            return other.removeFollower(user);
        }
    }

    public int sendPM(int connID, String username){
        User user = getUser(connID);
        User other = users.get(username);
        if(user==null||other==null) return -1;
        else{
            if(other.isBlocking(user)||user.isBlocking(other)) return -1;
            else return other.getConnectionID();
        }
    }


    public void addAwaitingMessage(T msg,String username){
        User user = users.get(username);
        user.addAwaitingMessage(msg);
    }

    public boolean logOut(int id){
        String username = connectedUsers.remove(id);
        if(username==null) return false;
        else{
            users.get(username).updateConnection(-1);
            return true;
        }
    }

    public Queue<T> attemptLogin(int connID, String username, String password){
        //returns null if login attempt failed due to incorrect credentials, or if the connection was
        //successful returns a Queue of awaiting messages.

        User user = users.get(username);
        //check if the username exists and that the client isn't already connected to a user
        if(user==null||connectedUsers.get(connID)!=null) return null;
        else{
            //check that the user isn't already connected and that the password is correct
            if(!user.getStatus()&&user.checkPassword(password)){
                user.updateConnection(connID);
                connectedUsers.put(connID,username);
                return user.getAwaitingMessages();
            }
            else return null;
        }
    }

    public boolean attemptRegister(int connID,String username, String password, String birthdate){
        User user = getUser(connID);
        //check that the request wasn't sent by a logged in client
        if(user!=null) return false;

        //check that the username isn't already taken.
        if(users.get(username)!=null) return false;
        else{
            User newUser = new User(username,password,birthdate);
            users.put(username,newUser);
            return true;
        }
    }

    public ConcurrentLinkedQueue<String> getFollowers(int connID){
        User user = getUser(connID);
        if(user == null) return null;
        else return user.getFollowers();
    }


    public boolean blockUser(int connID, String username){
        User user = getUser(connID);
        User other = users.get(username);
        if(user == null||other == null) return false;
        else{
            user.removeFollower(other);
            other.removeFollower(user);
            return user.addToBlock(other);
        }
    }

    public boolean blockCheck(String username1, String username2){
        User user1 =users.get(username1);
        User user2 = users.get(username2);
        return (user1.isBlocking(user2)||user2.isBlocking(user1));
    }

    public LinkedList<byte[]> Stat(int ConId, String[] usernames) {
        LinkedList<byte[]> toReturn = new LinkedList<>();
        User user = getUser(ConId);
        if(user == null) return null;
        else {
            for (String name : usernames) {
                if (!users.containsKey(name))
                    //לבדוק מה עושים אם אחד מהאנשים לא קיים (לבטל הכל או לשלוח את השאר)
                    return null;
                else
                    toReturn.add(stat(name));
            }
        }
            return toReturn;
        }

    public LinkedList<byte[]> logStat(int connID) {
        LinkedList<byte[]> toReturn = new LinkedList<>();
        User user = getUser(connID);
        if (user == null) return null;
        else {
            for (String currUserName : users.keySet()) {
                    toReturn.add(stat(currUserName));
                }
        }
        return toReturn;
    }


    public LinkedList<String> post(int connID,String content){
        User user = getUser(connID);
        if(user==null) return null;
        LinkedList<String> followersUsernames = new LinkedList<>();
        ConcurrentLinkedQueue<User> followers = user.getFollowers();
        for(User follower: followers){
            followersUsernames.add(follower.getUsername());
        }
        FilterString(content);
        user.addPost(content);
        return followersUsernames;
    }

    private byte[] stat(String username){
        ByteBuffer bb = ByteBuffer.allocate(8);
        User currUser = users.get(username);
        short age= (short) currUser.getAge();
        short postNum = (short) currUser.getPosts().size();
        short followerNum = (short) currUser.getFollowers().size();
        short followingNum = (short) currUser.getFollowing().size();
        bb.putShort(age);
        bb.putShort(postNum);
        bb.putShort(followerNum);
        bb.putShort(followingNum);
        return bb.array();
    }








}
