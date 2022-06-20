package ir.pi.project.server.controller.logic;

import ir.pi.project.server.config.texts.MainMenuTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.LoadMessagesPage;
import ir.pi.project.shared.response.myPage.LoadMyPage;
import ir.pi.project.shared.response.others.LoadExplorerPage;
import ir.pi.project.shared.response.others.ShowMessage;
import ir.pi.project.shared.response.settings.LoadSettingsPage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainMenuLogic extends MainLogic{
    private final MainMenuTexts mainMenuTexts=new MainMenuTexts();
    ClientHandler clientHandler;
    UserLogic userLogic;
    public MainMenuLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
        this.userLogic=new UserLogic(clientHandler);
    }

    public Response loadPage(MainMenuPage mainMenuPage){
        return switch (mainMenuPage) {
            case MY_PAGE -> new LoadMyPage(clientHandler.getHelpUser(clientHandler.getCurrentUserId()));
            case TIME_LINE -> showTimelineTweets();
            case EXPLORER -> new LoadExplorerPage();
            case MESSAGES -> new LoadMessagesPage();
            default -> new LoadSettingsPage();
        };
    }


    public Response showTimelineTweets() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        List<Integer> tweets = new ArrayList<>();
        if (!user.getFollowings().isEmpty()) {
            for (int i = 0; i < user.getFollowings().size(); i++) {
                User user1 = context.Users.get(user.getFollowings().get(i));
                if (user1.isActive()) {
                    if (!userLogic.isMutedBy(user1.getId(), user.getId()) && !userLogic.isBlockedBy(user1.getId(), user.getId())) {
                        if (!user1.getTweets().isEmpty()) {
                            for (int j = 0; j < user1.getTweets().size(); j++) {
                                Tweet tweet = context.Tweets.get(user1.getTweets().get(j));
                                if (!tweet.isBanned()) {
                                    if(tweet.isRetweeted()){
                                        User writer = context.Users.get(tweet.getWriter());
                                        if (writer.isPublic() || userLogic.isFollowing(user.getId(), writer.getId()))
                                            if (!userLogic.isBlockedBy(writer.getId(), user.getId()) && !userLogic.isMutedBy(writer.getId(), user.getId()) && writer.isActive() && writer.getId() != user.getId())
                                                tweets.add(user1.getTweets().get(j));
                                    } else tweets.add(user1.getTweets().get(j));
                                }
                            }
                        }
                        if (!user1.getLikedTweets().isEmpty()) {
                            for (int j = 0; j < user1.getLikedTweets().size(); j++) {
                                Tweet tweet = context.Tweets.get(user1.getLikedTweets().get(j));
                                if (!tweet.isBanned()){
                                    User user2 = context.Users.get(tweet.getWriter());
                                    if (user2.isActive() && !userLogic.isMutedBy(user2.getId(), user.getId()) && user2.getId() != user.getId())
                                        if (user2.isPublic() || userLogic.isFollowing(user.getId(), user2.getId()))
                                            tweets.add(user1.getLikedTweets().get(j));
                                }
                            }
                        }
                    }
                }
            }
            if (!tweets.isEmpty())
                Collections.shuffle(tweets);
        }


        if (tweets.isEmpty())
            return new ShowMessage(mainMenuTexts.getEmptyTimeLine());
         else {
             clientHandler.setCurrentTweetList(tweets);
             clientHandler.setCurrentTweetIndex(tweets.size()-1);
             clientHandler.getListOfTweets().add(tweets);
            return new ShowTweetLogic(clientHandler).showTweet();
        }
    }



}
