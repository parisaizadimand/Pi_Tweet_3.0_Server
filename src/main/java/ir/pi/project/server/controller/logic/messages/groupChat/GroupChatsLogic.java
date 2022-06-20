package ir.pi.project.server.controller.logic.messages.groupChat;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.others.NewGroupChat;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.groupChat.LoadNewGroupChatPage;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class GroupChatsLogic extends MainLogic {
    private final MessagesText messagesText = new MessagesText();
    static private final Logger logger = LogManager.getLogger(GroupChatsLogic.class);
    UserLogic userLogic;
    ClientHandler clientHandler;
    public GroupChatsLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
        this.userLogic=new UserLogic(clientHandler);
    }

    public Response loadNewGroupChat(){
        return new LoadNewGroupChatPage(usernames());
    }


    public Response newGroupChatCheck(NewGroupChat newGroupChat, String string){
        if(newGroupChat.equals(NewGroupChat.ADD)){
            return addMember(string);
        }else if(newGroupChat.equals(NewGroupChat.ADD_AFTER)){
            return addAfter(string);
        } //make
         else return makeTheGroupChat(string);
    }

    private Response addAfter(String username){
        if(!userLogic.userCanBeFound(username))
            return new ShowMessage(messagesText.getUserNotFound());


        User user=context.Users.get(clientHandler.getCurrentUserId());
        User otherUser=userLogic.userByUsername(username);
        if(!userLogic.isFollowing(user.getId(),otherUser.getId()) && !userLogic.isFollowing(otherUser.getId(),user.getId()))
            return new ShowMessage(messagesText.getFf());

        if(!user.isActive()) return new ShowMessage(messagesText.getUserNotFound());

        GroupChat groupChat=context.GroupChats.get(clientHandler.getCurrentChat().getGroupChatId());
        groupChat.getMembers().add(otherUser.getId());
        otherUser.getGroupChats().add(groupChat.getId());
        context.GroupChats.update(groupChat);
        context.Users.update(otherUser);
        return new ShowMessage(messagesText.getAdded());
    }

    private Response addMember(String username) {
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if (username.equals(user.getUserName()))
            return new ShowMessage(messagesText.getUrIn());

        return add(username);
    }

    private Response add(String username){
        User me = context.Users.get(clientHandler.getCurrentUserId());
        for (User user : context.Users.all()) {
            if(user.getUserName().equals(username) && user.isActive()){
                if (clientHandler.getNewGroupChatMembers().contains(user.getId())) {
                    return new ShowMessage(messagesText.getChosenUser());
                }
                if (userLogic.isFollowing(me.getId(), user.getId()) || userLogic.isFollowing(user.getId(), me.getId())) {
                    clientHandler.getNewGroupChatMembers().add(user.getId());
                    return new LoadNewGroupChatPage(usernames());
                } else return new ShowMessage(messagesText.getFf());
            }
        }

        return new ShowMessage(messagesText.getUserNotFound());
    }


    private Response makeTheGroupChat(String groupChatName){
        List<Integer> members=clientHandler.getNewGroupChatMembers();

        if(members.isEmpty())
            return new ShowMessage(messagesText.getAtLeastOne());

        if(groupChatName.isEmpty())
            return new ShowMessage(messagesText.getEmptyName());

        User me=context.Users.get(clientHandler.getCurrentUserId());
        GroupChat groupChat=new GroupChat(ID.newID(),groupChatName,me);
        me.getGroupChats().add(groupChat.getId());
        groupChat.getMembers().add(me.getId());
        for (Integer memberId: members) {
            User member=context.Users.get(memberId);
            member.getGroupChats().add(groupChat.getId());
            groupChat.getMembers().add(member.getId());
            context.Users.update(member);
            context.GroupChats.update(groupChat);

        }

        context.Users.update(me);
        logger.info("user "+me.getId()+"made a new groupChat with id: "+groupChat.getId());


        return new ShowMessage(messagesText.getGroupChatMade());


    }



    private List<String> usernames(){
        List<String> usernames=new ArrayList<>();
        for (Integer userId: clientHandler.getNewGroupChatMembers())
            usernames.add(context.Users.get(userId).getUserName());
        return usernames;
    }

}
