package bgu.spl.net.srv.objects;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User<T> {
    private String username;
    private String password;
    private String birthDate;
    private ConcurrentLinkedQueue<User> followers;
    private ConcurrentLinkedQueue<User> following;
    private ConcurrentLinkedQueue<User> block;
    private LinkedList<String> posts;
    private LinkedList<String> pms;
    private ConcurrentLinkedQueue<T> awaitingMessages;

    private boolean connected;
    private int connectionID;

    private ConnectionHandler handler;

    public User(String username, String password, String birthDate){
        this.username=username;
        this.password=password;

        this.followers=new ConcurrentLinkedQueue<>();
        this.following=new ConcurrentLinkedQueue<>();
        this.block = new ConcurrentLinkedQueue<>();
        this.posts = new LinkedList<>();
        this.pms = new LinkedList<>();
        this.awaitingMessages= new ConcurrentLinkedQueue<>();
        this.connected=false;
        this.connectionID=-1;


        this.birthDate=birthDate;
    }

    public void updateConnection(int connID){
        connected=!connected;
        connectionID=connID;
    }

    public boolean getStatus(){
        return this.connected;
    }

    public int getConnectionID(){
        return this.connectionID;
    }

    public void addAwaitingMessage(T msg){
        this.awaitingMessages.add(msg);
    }

    public Queue<T> getAwaitingMessages(){
        return this.awaitingMessages;
    }

    public boolean addFollower(User user){
        if(this.followers.contains(user)) return false;
        this.followers.add(user);
        user.addFollowing(this);
        return true;
    }

    private void addFollowing(User user){
        this.following.add(user);
    }

    public ConcurrentLinkedQueue<User> getFollowing() {
        return following;
    }

    public boolean removeFollower(User user){
        if(!this.followers.contains(user)) return false;
        followers.remove(user);
        user.removeFollowing(this);
        return true;
    }

    private void removeFollowing(User user){
        this.following.remove(user);
    }


    public ConcurrentLinkedQueue<User> getFollowers(){
        return this.followers;
    }

    public void addPM(String pm){
        pms.add(pm);
    }

    public boolean addToBlock(User user){
        if(block.contains(user)) return false;
        else{
            block.add(user);
            return true;
        }
    }

    public boolean isBlocking(User user){
        return block.contains(user);
    }

    public void addPost(String post){
        posts.add(post);
    }


    public String getUsername(){
        return this.username;
    }


    public int getAge(){
        int day = Integer.parseInt(birthDate.substring(0,2));
        int month = Integer.parseInt(birthDate.substring(3,5));
        int year = Integer.parseInt(birthDate.substring(6));
        LocalDate now = LocalDate.now();
        LocalDate age = LocalDate.of(year, month, day);
        return Period.between(age,now).getYears();
    }


    public boolean checkPassword(String password){
        if(password.equals(this.password)) return true;
        else return false;
    }

    public LinkedList<String> getPosts() {
        return posts;
    }
}
