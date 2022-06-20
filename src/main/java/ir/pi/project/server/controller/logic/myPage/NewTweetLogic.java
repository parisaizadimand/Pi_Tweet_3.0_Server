package ir.pi.project.server.controller.logic.myPage;

import ir.pi.project.server.config.texts.myPage.MyPageTexts;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewTweetLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(NewTweetLogic.class);
    private final MyPageTexts myPageTexts=new MyPageTexts();

    int userId;
    public NewTweetLogic(int userId){
        this.userId=userId;
    }

    public Response newTweet(String text){
        if(text.equals("")) return new ShowMessage("");
        User user=context.Users.get(userId);
        Tweet tweet=new Tweet(ID.newID(),user.getId(),text);
        context.Tweets.update(tweet);

        user.getTweets().add(tweet.getId());
        context.Users.update(user);
        logger.info("user "+user.getId()+" made a new tweet with id: "+tweet.getId());

        return new ShowMessage(myPageTexts.getSuccessful());
    }
}
