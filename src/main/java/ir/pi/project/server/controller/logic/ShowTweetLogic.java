package ir.pi.project.server.controller.logic;

import ir.pi.project.server.config.others.LimitsConfig;
import ir.pi.project.server.config.texts.ShowTweetTexts;
import ir.pi.project.server.config.texts.myPage.ForwardTweetTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.others.TweetComponent;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.model.help.HelpUser;
import ir.pi.project.shared.response.*;
import ir.pi.project.shared.response.others.LoadForwardPage;
import ir.pi.project.shared.response.others.LoadTweet;
import ir.pi.project.shared.response.others.Nothing;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class ShowTweetLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(ShowTweetLogic.class);
    private final ShowTweetTexts showTweetTexts = new ShowTweetTexts();
    private final LimitsConfig limitsConfig = new LimitsConfig();
    ClientHandler clientHandler;
    UserLogic userLogic;

    public ShowTweetLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic=new UserLogic(clientHandler);
    }

    public Response check(TweetComponent tweetComponent) {
        int index = clientHandler.getCurrentTweetIndex();
        switch (tweetComponent) {
            case NEXT:
                if (clientHandler.getCurrentTweetIndex() != 0) {
                    index--;
                    clientHandler.setCurrentTweetIndex(index);
                    return showTweet();
                }

                return new ShowMessage(showTweetTexts.getNoMore());

            case PREVIOUS:
                if (index != clientHandler.getCurrentTweetList().size() - 1) {
                    index++;
                    clientHandler.setCurrentTweetIndex(index);
                    return showTweet();
                }

                return new ShowMessage(showTweetTexts.getNoMore());
            case LIKE:

                return like();
            case SAVE:

                return save();
            case FORWARD:

                return new LoadForwardPage();
            case RETWEET:

                return retweet();
            case REPORT:

                return report();

            case UPLOAD_COMMENT_IMAGE:

                return  null;

            case COMMENTS:

                return showComments();

            default:

                return back();
        }

    }

    public Response showTweet() {
        System.out.println("showTweet");
        Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        User writer = context.Users.get(tweet.getWriter());
        HelpUser retweetedBy=null;
        if(tweet.isRetweeted())retweetedBy=clientHandler.getHelpUser(tweet.getRetweetedBy());
        return new LoadTweet(tweet,clientHandler.getHelpUser(writer.getId()),retweetedBy);
    }

    public Response newComment(String text,String imageInString) {
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if (text == null || text.equals("")) return new ShowMessage(showTweetTexts.getEmptyTextArea());
        Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));

        Tweet comment = new Tweet(ID.newID(), clientHandler.getCurrentUserId(), text);
        if(imageInString!=null) {
            comment.setImageInString(imageInString);
        }
        context.Tweets.update(comment);
        tweet.getComments().add(comment.getId());
        context.Tweets.update(tweet);
        user.getTweets().add(comment.getId());
        context.Users.update(user);
        logger.info("user"+user.getId()+" commented on tweet with id: " + tweet.getId());

        return new ShowMessage(showTweetTexts.getCommented());
    }




    private Response like() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        if (!user.getLikedTweets().isEmpty()) {
            for (int j = 0; j < user.getLikedTweets().size(); j++) {
                if (clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()).equals(user.getLikedTweets().get(j))) {
                    user.getLikedTweets().remove(j);
                    context.Users.update(user);
                    Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
                    tweet.addToLikeNums(-1);
                    context.Tweets.update(tweet);
                    logger.info("user "+user.getId()+"disliked tweet with id: " + tweet.getId());
                    return new ShowMessage(showTweetTexts.getDisliked());
                }
            }
        }
        user.getLikedTweets().add(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        tweet.addToLikeNums(1);
        context.Users.update(user);
        context.Tweets.update(tweet);
        logger.info("user "+user.getId()+" Liked tweet with id: " + tweet.getId());
        return new ShowMessage(showTweetTexts.getLiked());

    }


    private Response save() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        user.getSavedTweets().add(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        context.Users.update(user);
        logger.info("user "+user.getId()+" saved tweet with id: " + clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        return new ShowMessage(showTweetTexts.getSaved());
    }

    private Response retweet() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        if(context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex())).getWriter()==clientHandler.getCurrentUserId())
            return new ShowMessage(showTweetTexts.getCantRetweetUrs());

        Tweet tweet=context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        Tweet retweet=new Tweet(ID.newID(),tweet.getWriter(),tweet.getText());
        retweet.setTime(tweet.getTime());
        retweet.setRetweeted(true);
        retweet.setImageInString(tweet.getImageInString());
        retweet.setReportedTimes(tweet.getReportedTimes());
        retweet.setComments(tweet.getComments());
        retweet.setLikesNum(tweet.getLikesNum());
        retweet.setRetweetedBy(clientHandler.getCurrentUserId());
        user.getTweets().add(retweet.getId());
        context.Users.update(user);
        context.Tweets.update(retweet);
        logger.info("user "+user.getId()+" retweeted tweet with id: " + clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        return new ShowMessage(showTweetTexts.getRetweeted());
    }

    private Response report() {
        Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        tweet.addToReportedTimes();
        if (tweet.getReportedTimes() == limitsConfig.getReportLimit()) {
            tweet.setBanned(true);
        }
        context.Tweets.update(tweet);
        return new ShowMessage(showTweetTexts.getReported());
    }


    private Response showComments() {
        User user=context.Users.get(clientHandler.getCurrentUserId());
        Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
        List<Integer> comments=new ArrayList<>();
        for (Integer commentId: tweet.getComments()) {
            Tweet comment=context.Tweets.get(commentId);
            if (!comment.isBanned()) {
//                if(tweet.getWriter()==clientHandler.getCurrentUserId()) comments.add(commentId);
//                else {
                    User user1 = context.Users.get(comment.getWriter());
                    System.out.println(user1.getUserName() + " " + userLogic.isMutedBy(user1.getId(), user.getId()));
                    if (user1.getId() == clientHandler.getCurrentUserId()) comments.add(commentId);
                    else if (user1.isActive() && !userLogic.isMutedBy(user1.getId(), user.getId())) comments.add(commentId);
//                }
            }
        }
        if (comments.isEmpty())
            return new ShowMessage(showTweetTexts.getNoComment());
        else {
            clientHandler.setCurrentTweetList(comments);
            clientHandler.setCurrentTweetIndex(comments.size() - 1);
            clientHandler.getListOfTweets().add(comments);
            return showTweet();
        }
    }

    private Response back() {
        clientHandler.getListOfTweets().remove(clientHandler.getListOfTweets().size() - 1);
        if (clientHandler.getListOfTweets().size() == 0)
            return new Nothing();

        int currentTweetId = clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex());
        clientHandler.setCurrentTweetList(clientHandler.getListOfTweets().get(clientHandler.getListOfTweets().size() - 1));
        for (int i = 0; i < clientHandler.getCurrentTweetList().size(); i++) {
            Tweet tweet = context.Tweets.get(clientHandler.getCurrentTweetList().get(i));
            for (Integer commentId :
                    tweet.getComments()) {
                if (commentId.equals(currentTweetId)) {
                    clientHandler.setCurrentTweetIndex(i);
                    break;
                }
            }

        }

        return showTweet();
    }

    public Response forward(String username){
        User thisUser = context.Users.get(clientHandler.getCurrentUserId());

        ForwardTweetTexts forwardTweetTexts=new ForwardTweetTexts();
        if(username.isEmpty())
            return new ShowMessage(forwardTweetTexts.getEmptyUsername());
        if(!userLogic.userCanBeFound(username))
            return new ShowMessage(forwardTweetTexts.getNotFound());
        if(username.equals(context.Users.get(clientHandler.getCurrentUserId()).getUserName()))
            return new ShowMessage(forwardTweetTexts.getItsU());
        User otherUser=userLogic.userByUsername(username);
        if(userLogic.isFollowing(clientHandler.getCurrentUserId(),otherUser.getId())
                ||userLogic.isFollowing(otherUser.getId(),clientHandler.getCurrentUserId())){
            Tweet tweet=context.Tweets.get(clientHandler.getCurrentTweetList().get(clientHandler.getCurrentTweetIndex()));
            User writer=context.Users.get(tweet.getWriter());
            String text=forwardTweetTexts.getForwardedFrom()+ "'"+writer.getUserName()+"'\n--------------------------------------------------------------\n"
                    +tweet.getText();
            new NewMessageLogic().newMessage(clientHandler.getCurrentUserId(),otherUser.getId(),text, tweet.getImageInString(), true,null);
           logger.info("user "+thisUser.getId()+" forwarded a message to user "+otherUser.getId());
            return new ShowMessage(forwardTweetTexts.getForwarded());
        }
        else
            return new ShowMessage(forwardTweetTexts.getFf());
    }


}



