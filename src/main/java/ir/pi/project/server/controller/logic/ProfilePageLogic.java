package ir.pi.project.server.controller.logic;

import ir.pi.project.server.config.ShowProfileTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.messages.ShowChatLogic;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.LoadProfilePage;
import ir.pi.project.shared.response.others.ShowMessage;
import java.util.ArrayList;
import java.util.List;

public class ProfilePageLogic extends MainLogic {
    private final ShowProfileTexts showProfileTexts = new ShowProfileTexts();
    ClientHandler clientHandler;
    UserLogic userLogic;

    public ProfilePageLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
    }

    public Response update() {
        User me = context.Users.get(clientHandler.getCurrentUserId());
        User user = context.Users.get(clientHandler.getUserPfToBSeenId());

        //lastSeenLabel
        String lastSeenText = user.getLastSeen();
        if (!user.isOnline()) {
            if (lastSeenText.equals("Followers")) {
                if (!userLogic.isFollowing(me.getId(), user.getId()))
                    lastSeenText = showProfileTexts.getLastSeenRecently();
            }
            if (lastSeenText.equals("NoOne"))
                lastSeenText = showProfileTexts.getLastSeenRecently();
        }

        //BlockStateLabel _ BlockButton
        String blockStateText = "";
        String blockButtonText = showProfileTexts.getBlock();
        if (userLogic.isBlockedBy(user.getId(), me.getId())) {
            blockStateText = showProfileTexts.getuBlocked();
            blockButtonText= showProfileTexts.getUnblock();
        }
        if (userLogic.isBlockedBy(me.getId(), user.getId()))
            blockStateText = showProfileTexts.getUrBlocked();


        //FollowingStateLabel _ FollowButton
        String followingStateText = "";
        String followButtonText = showProfileTexts.getFollow();

        if (userLogic.hasRequestedToFollow(user.getId())) {
            followingStateText = showProfileTexts.getRequested();
            followButtonText=showProfileTexts.getUnfollow();
        }
        else if (!userLogic.isFollowing(me.getId(), user.getId()) && !user.isPublic())
            followingStateText = showProfileTexts.getPrivateAcc();

        else if (userLogic.isFollowing(me.getId(), user.getId())) {
            followingStateText = showProfileTexts.getFollowing();
            followButtonText=showProfileTexts.getUnfollow();
        }

        //MuteButton
        String muteButtonText = showProfileTexts.getMute();
        if(userLogic.isMutedBy(user.getId(), me.getId()))
            muteButtonText=showProfileTexts.getUnmute();

        return new LoadProfilePage(clientHandler.getHelpUser(user.getId()),followButtonText,blockButtonText,muteButtonText,blockStateText,followingStateText,lastSeenText);
    }




    public Response check(ProfilePage profilePage) {
        //ShowImage
        return switch (profilePage) {
            case MUTE -> muteLogic();
            case FOLLOW -> followLogic();
            case REPORT -> new ShowMessage(showProfileTexts.getReportText());
            case BLOCK -> blockLogic();
            case MESSAGE -> message();
            case TWEETS -> showTweets();
            default -> null;
        };
    }


    private Response muteLogic(){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=context.Users.get(clientHandler.getUserPfToBSeenId());

        if(!userLogic.isMutedBy(user.getId(),me.getId())){
            userLogic.mute(me.getId(),user.getId());
        }
        else {
            userLogic.unMute(me.getId(),user.getId());
        }

        return update();
    }

    private Response blockLogic(){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=context.Users.get(clientHandler.getUserPfToBSeenId());

        if(userLogic.isBlockedBy(user.getId(),me.getId())){
            userLogic.unBlock(me.getId(),user.getId());
        }else {
            userLogic.block(me.getId(),user.getId());
        }

        return update();
    }

    public Response followLogic(){
        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=context.Users.get(clientHandler.getUserPfToBSeenId());

        if(userLogic.isFollowing(me.getId(),user.getId())){
            userLogic.unFollow(me.getId(),user.getId());
        }

        else if(userLogic.hasRequestedToFollow(user.getId()) && !user.isPublic()){
            userLogic.deleteRequest(user.getId());
        }
        else {
            if(user.isPublic()) {
                if (userLogic.isBlockedBy(user.getId(), me.getId())) {
                    return new ShowMessage(showProfileTexts.getuBlocked());
                }
                if (userLogic.isBlockedBy(me.getId(), user.getId()))
                    return new ShowMessage(showProfileTexts.getUrBlocked());

                userLogic.follow(me.getId(),user.getId());
            }
            else {
                user.getRequests().add(me.getId());
                context.Users.update(user);
            }
        }



        return update();

    }

    private Response showTweets(){

        User me=context.Users.get(clientHandler.getCurrentUserId());
        User user=context.Users.get(clientHandler.getUserPfToBSeenId());
        List<Integer> usersTweets=user.getTweets();

        if (userLogic.isBlockedBy(user.getId(), me.getId())) {
            return new ShowMessage(showProfileTexts.getuBlocked());
        }
        if (userLogic.isBlockedBy(me.getId(), user.getId()))
            return new ShowMessage(showProfileTexts.getUrBlocked());

        if(!user.isPublic() && !userLogic.isFollowing(me.getId(),user.getId()))
            return new ShowMessage(showProfileTexts.getCantSeeTweets());

        List<Integer> tweets=new ArrayList<>();
        for (Integer tweetId: usersTweets) {
            Tweet tweet=context.Tweets.get(tweetId);
            if(!tweet.isBanned()){
                if(!userLogic.isMutedBy(tweet.getWriter(), me.getId())
                        && !userLogic.isBlockedBy(tweet.getWriter(), me.getId())){
                    tweets.add(tweetId);
                }
            }
        }

        if(tweets.isEmpty())
        return new ShowMessage(showProfileTexts.getNoTweets());

        clientHandler.setCurrentTweetList(tweets);
        clientHandler.setCurrentTweetIndex(tweets.size()-1);
        clientHandler.getListOfTweets().add(tweets);

        return new ShowTweetLogic(clientHandler).showTweet();
    }


    private Response message(){
        User user=context.Users.get(clientHandler.getUserPfToBSeenId());
        return new ShowChatLogic(clientHandler).showChat(MessagesPage.DIRECT_CHATS,user.getUserName());

    }

}
