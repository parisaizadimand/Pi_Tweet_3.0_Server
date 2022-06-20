package ir.pi.project.server.controller.logic.myPage;

import ir.pi.project.server.config.ShowProfileTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.shared.enums.others.RequestComponent;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.myPage.lists.LoadBlackList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ListsLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(ListsLogic.class);
    private final ShowProfileTexts showProfileTexts=new ShowProfileTexts();
    ClientHandler clientHandler;
    UserLogic userLogic;
    public ListsLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
        this.userLogic=new UserLogic(clientHandler);
    }

    public Response unblock(String username){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=userLogic.userByUsername(username);
        userLogic.unBlock(me.getId(),user.getId());
        Map<String,String> blacklist=new HashMap<>();
        for (Integer blockedId: user.getBlackList()) {
            User blocked=context.Users.get(blockedId);
            blacklist.put(blocked.getUserName(),blocked.getProfileImageInString());
        }
            return new LoadBlackList(blacklist);
    }

    public Response checkRequestEvent(String username, RequestComponent requestComponent){
        User user=userLogic.userByUsername(username);
        return switch (requestComponent) {
            case DELETE -> deleteRequest(user.getId());
            case DELETE_AND_INFORM -> deleteAndInformRequest(user.getId());
            default -> acceptRequest(user.getId());
        };
    }

    private Response acceptRequest(int otherUserId){
        User user1=context.Users.get(clientHandler.getCurrentUserId());
        User user2=context.Users.get(otherUserId);

        user1.getNotifications().add(user2.getUserName() +" "+showProfileTexts.getStartedFollowing());
        user2.getMyRequests().add(user1.getUserName()+" "+showProfileTexts.getRequestAccepted());
        for (int i=0;i<user1.getRequests().size();i++){
            if (user1.getRequests().get(i).equals(user2.getId())) {
                user1.getFollowers().add(user1.getRequests().get(i));
                user1.getRequests().remove(i);
                break;
            }
        }
        user2.getFollowings().add(user1.getId());

        context.Users.update(user1);
        context.Users.update(user2);
        logger.info("user "+user1.getId()+" accepted "+user2.getId()+"'s request");
        return new MyPageLogic(clientHandler).loadRequestsList();
    }

    private Response deleteRequest(int otherUserId){
        User user1=context.Users.get(clientHandler.getCurrentUserId());
        User user2=context.Users.get(otherUserId);
        for (int i=0;i<user1.getRequests().size();i++){
            if (user1.getRequests().get(i).equals(user2.getId())) {
                user1.getRequests().remove(i);
                break;
            }
        }
        context.Users.update(user1);
        context.Users.update(user2);
        logger.info("user "+user1.getId()+" deleted "+user2.getId()+"'s request");
        return new MyPageLogic(clientHandler).loadRequestsList();
    }

    private Response deleteAndInformRequest(int otherUserId){
        User user1=context.Users.get(clientHandler.getCurrentUserId());
        User user2=context.Users.get(otherUserId);
        for (int i=0;i<user1.getRequests().size();i++){
            if (user1.getRequests().get(i).equals(user2.getId())) {
                user1.getRequests().remove(i);
                user2.getMyRequests().add(user1.getUserName()+showProfileTexts.getRequestDeleted());
                break;
            }
        }
        context.Users.update(user1);
        context.Users.update(user2);
        logger.info("user "+user1.getId()+" deleted "+user2.getId()+"'s request");

        return new MyPageLogic(clientHandler).loadRequestsList();

    }

    public Response unfollow(String username){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=userLogic.userByUsername(username);

        userLogic.unFollow(me.getId(), user.getId());
        return new MyPageLogic(clientHandler).loadFollowingsList();
    }

    public Response deleteFollower(String username){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=userLogic.userByUsername(username);

        userLogic.unFollow(user.getId(),me.getId());

        return new MyPageLogic(clientHandler).loadFollowersList();
    }

}
