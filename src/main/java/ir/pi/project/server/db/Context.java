package ir.pi.project.server.db;


import ir.pi.project.server.model.Bot;
import ir.pi.project.shared.model.*;

public class Context {
    public DBSet<User> Users = new UserDB();
    public DBSet<Tweet> Tweets=new TweetDB();
    public DBSet<Message> Messages=new MessageDB();
    public DBSet<GroupChat> GroupChats=new GroupChatDB();
    public DBSet<Group> Groups=new GroupDB();
    public DBSet<Bot> Bots=new BotDB();

}
