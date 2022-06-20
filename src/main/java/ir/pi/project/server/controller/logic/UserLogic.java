package ir.pi.project.server.controller.logic;

import ir.pi.project.server.config.ShowProfileTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.shared.model.Group;
import ir.pi.project.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserLogic extends MainLogic{
    static private final Logger logger= LogManager.getLogger(UserLogic.class);
    private final ShowProfileTexts showProfileText=new ShowProfileTexts();
    ClientHandler clientHandler;
    public UserLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
    }

    public User userByUsername(String username){
        for (User user: context.Users.all()) {
            if(user.getUserName().equals(username))
                return user;
        }
        return null;
    }

    public boolean userCanBeFound(String username){
        for (User user: context.Users.all()) {
            if(user.getUserName().equals(username))
                return true;
        }
        return false;
    }

    public void follow(int userId,int user1Id) {
        User me=context.Users.get(userId);
        User user1 = context.Users.get(user1Id);
        if (user1.isPublic()) {
            user1.getFollowers().add(me.getId());
            me.getFollowings().add(user1Id);
            user1.getNotifications().add(me.getUserName() + showProfileText.getStartedFollowing());

        } else {
            user1.getRequests().add(me.getId());
        }


        context.Users.update(user1);
        context.Users.update(me);
        logger.info("user "+me.getId() + " followed " + user1.getId());
    }

    public void deleteRequest(int id){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        User user1 = context.Users.get(id);
        for (int i = 0; i < user1.getRequests().size(); i++) {
            if (user1.getRequests().get(i).equals(clientHandler.getCurrentUserId())) {
                user1.getRequests().remove(i);
            }
        }
        context.Users.update(user1);
        context.Users.update(context.Users.get(clientHandler.getCurrentUserId()));
        logger.info("user "+user.getId()+" deleted its request to user "+user1.getId());
    }

    public void unFollow(int userId,int user1Id) {
        User me=context.Users.get(userId);
        User user1 = context.Users.get(user1Id);
        for (int i = 0; i < user1.getFollowers().size(); i++) {
            if (user1.getFollowers().get(i) == me.getId()) {
                user1.getFollowers().remove(i);
                for (Integer groupId :
                        user1.getGroups()) {
                    Group group = context.Groups.get(groupId);
                    if (group.getMembers().contains(user1.getId())) {
                        group.getMembers().remove((Object) user1.getId());
                    }
                }
                user1.getNotifications().add(me.getUserName() + showProfileText.getStoppedFollowing());
            }
        }
        for (int i = 0; i < me.getFollowings().size(); i++) {
            if (me.getFollowings().get(i) == user1Id) {
                me.getFollowings().remove(i);
            }
        }
        context.Users.update(user1);
        context.Users.update(me);
        logger.info("user "+me.getId() + " unfollowed " + user1.getId());

    }

    public boolean isFollowing(int user1Id,int user2Id) {
        User otherUser = context.Users.get(user2Id);
        for (int i = 0; i < otherUser.getFollowers().size(); i++) {
            if (otherUser.getFollowers().get(i) ==user1Id) {
                return true;
            }
        }
        return false;
    }

    public boolean hasRequestedToFollow(int id) {
        User user1 = context.Users.get(id);
        for (int i = 0; i < user1.getRequests().size(); i++) {
            if (user1.getRequests().get(i).equals(clientHandler.getCurrentUserId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBlockedBy(int userId,int user1Id) {
        User user1 = context.Users.get(user1Id);
        for (int i = 0; i < user1.getBlackList().size(); i++) {
            if (user1.getBlackList().get(i) == userId) {
                return true;
            }
        }
        return false;
    }

    public void block(int userId,int user1Id) {

        User user=context.Users.get(userId);
        User user1 = context.Users.get(user1Id);
        user.getBlackList().add(user1Id);
        context.Users.update(user1);
        context.Users.update(user);
        if (isFollowing(userId,user1Id)) unFollow(userId,user1Id);
        if (isFollowing(user1Id,userId)) {
            unFollow(user1Id,userId);
        }

        logger.info("user "+user.getId() + " blocked " + user1.getId());
    }

    public void unBlock(int userId,int user1Id) {

        User user=context.Users.get(userId);
        User user1 = context.Users.get(user1Id);
        for (int i = 0; i < user.getBlackList().size(); i++) {
            if (user1Id == user.getBlackList().get(i)) {
                user.getBlackList().remove(i);

            }
        }
        context.Users.update(user);
        logger.info("user" +user.getId() + " unBlocked " + user1.getId());
    }

    public void mute(int userId,int user1Id) {
        User user=context.Users.get(userId);
        User user1=context.Users.get(user1Id);

        if (!isMutedBy(user1.getId(),user.getId())) {
            user.getMuted().add(user1.getId());
        }
        context.Users.update(user);
        context.Users.update(user1);
        System.out.println(user.getMuted());
        logger.info("user "+user.getId() + " muted " + user1.getId());
    }

    public void unMute(int userId,int user1Id){
        User user=context.Users.get(userId);
        User user1=context.Users.get(user1Id);

        for(int i=0;i<user.getMuted().size();i++){
            if(user.getMuted().get(i).equals(user1Id))
                user.getMuted().remove(i);
        }
        context.Users.update(user);
        context.Users.update(user1);
        logger.info("user "+user.getId() + " unMuted " + user1.getId());

    }

    public boolean isMutedBy(int userId,int user1Id){
        User user1=context.Users.get(user1Id);
        for (int i = 0; i < user1.getMuted().size(); i++) {
            if (user1.getMuted().get(i) == userId) {
                return true;
            }
        }

        return false;
    }
}
