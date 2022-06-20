package ir.pi.project.server.controller.logic.myPage;

import ir.pi.project.server.config.texts.myPage.MyPageTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.ShowTweetLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.myPage.LoadEditProfilePage;
import ir.pi.project.shared.response.myPage.LoadNotificationsPage;
import ir.pi.project.shared.response.myPage.LoadPendingListPage;
import ir.pi.project.shared.response.myPage.lists.LoadBlackList;
import ir.pi.project.shared.response.myPage.lists.LoadFollowersList;
import ir.pi.project.shared.response.myPage.lists.LoadFollowingsList;
import ir.pi.project.shared.response.myPage.lists.LoadRequestsList;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class MyPageLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(MyPageLogic.class);
    private final MyPageTexts myPageTexts=new MyPageTexts();
    int userId;
    NewTweetLogic newTweetLogic;
    ClientHandler clientHandler;
    public MyPageLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
        this.userId=clientHandler.getCurrentUserId();
        this.newTweetLogic=new NewTweetLogic(userId);
    }

    public Response newTweet(String text,String tweetImageInString){
        if(text==null||text.equals("")) return new ShowMessage(myPageTexts.getEmptyTextArea());
        User user=context.Users.get(userId);
        Tweet tweet=new Tweet(ID.newID(),user.getId(),text);
        if(tweetImageInString!=null) {
            tweet.setImageInString(tweetImageInString);
        }
        context.Tweets.update(tweet);
        user.getTweets().add(tweet.getId());
        context.Users.update(user);
        logger.info("user "+user.getId()+" made a new tweet with id: "+tweet.getId());

        return new ShowMessage(myPageTexts.getSuccessful());
    }

    public Response checkEvent(MyPage myPage){

        return switch (myPage) {
            case NOTIFICATIONS -> loadNotifications();
            case REQUESTS -> loadRequestsList();
            case PENDING -> loadPending();
            case BLACKLIST -> loadBlacklist();
            case TWEETS -> loadMyTweets();
            case FOLLOWERS -> loadFollowersList();
            case FOLLOWINGS -> loadFollowingsList();
            default -> new LoadEditProfilePage(clientHandler.getHelpUser(clientHandler.getCurrentUserId()));
        };
    }

    public Response loadMyTweets() {
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if (user.getTweets().isEmpty()) {
            return new ShowMessage(myPageTexts.getNoTweets());
        }

        clientHandler.setCurrentTweetList(user.getTweets());
        clientHandler.setCurrentTweetIndex(user.getTweets().size()-1);
        clientHandler.getListOfTweets().add(user.getTweets());

        return new ShowTweetLogic(clientHandler).showTweet();
    }

    private Response loadNotifications(){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if(user.getNotifications().isEmpty())
            return new ShowMessage(myPageTexts.getEmptyNotifications());

        return new LoadNotificationsPage(user.getNotifications());
    }
    public Response loadPending(){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if(user.getMyRequests().isEmpty())
            return new ShowMessage(myPageTexts.getEmptyPending());

        return new LoadPendingListPage(user.getMyRequests());
    }

    private Response loadBlacklist(){
        Map<String,String> blacklist=new HashMap<>();
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Integer blockedId: user.getBlackList()) {
            User blocked=context.Users.get(blockedId);
            if(blocked.getProfileImageInString()==null)
                blocked.setProfileImageInString("empty");
            blacklist.put(blocked.getUserName(),blocked.getProfileImageInString());
        }

        if(blacklist.isEmpty())
            return new ShowMessage(myPageTexts.getEmptyBlacklist());

        else
            return new LoadBlackList(blacklist);
    }
    public Response loadRequestsList(){
        Map<String,String> requests=new HashMap<>();
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Integer requesterId:
             user.getRequests()) {
            User requester=context.Users.get(requesterId);
            if(requester.getProfileImageInString()==null)
                requester.setProfileImageInString("empty");
            requests.put(requester.getUserName(),requester.getProfileImageInString());
        }

            return new LoadRequestsList(requests);
    }

    public Response loadFollowersList(){
        Map<String,String> followers=new HashMap<>();
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Integer followerId : user.getFollowers()) {
            User follower=context.Users.get(followerId);
            if(follower.getProfileImageInString()==null)
                follower.setProfileImageInString("empty");
            followers.put(follower.getUserName(),follower.getProfileImageInString()); }
        return new LoadFollowersList(followers);
    }

    public Response loadFollowingsList(){
        Map<String,String> followings=new HashMap<>();
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Integer followingId : user.getFollowings()) {
            User following=context.Users.get(followingId);
            if(following.getProfileImageInString()==null)
                following.setProfileImageInString("empty");
            followings.put(following.getUserName(),following.getProfileImageInString());
        }
        return new LoadFollowingsList(followings);
    }


}
