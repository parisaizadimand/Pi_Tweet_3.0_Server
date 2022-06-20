package ir.pi.project.server.controller.logic;

import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.server.config.texts.ExplorerTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExplorerPageLogic extends MainLogic{
    private final InfoConfig infoConfig=new InfoConfig();
    private final ExplorerTexts explorerTexts=new ExplorerTexts();
    static private final Logger logger = LogManager.getLogger(ExplorerPage.class);


    ClientHandler clientHandler;
    UserLogic userLogic;

    public ExplorerPageLogic(ClientHandler clientHandler){
    this.clientHandler=clientHandler;
    this.userLogic=new UserLogic(clientHandler);
    }

    public Response check(String username,ExplorerPage explorerPage){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        if(explorerPage.equals(ExplorerPage.WORLD)){
            List<Integer> worldList=worldList();
            if(worldList.isEmpty())
                return new ShowMessage(explorerTexts.getEmptyWorld());
            clientHandler.setCurrentTweetList(worldList);
            clientHandler.setCurrentTweetIndex(worldList.size()-1);
            clientHandler.getListOfTweets().add(worldList);
            String currentUserUsername=context.Users.get(clientHandler.getCurrentUserId()).getUserName();
            logger.info("user "+user.getId()+" opened world");
            return new ShowTweetLogic(clientHandler).showTweet();
        }
        else {
            return searchFor(username);
        }
    }



    private Response searchFor(String username) {

        try {
            File usersDirectory = new File(infoConfig.getUsersDirectory());
            for (File file : usersDirectory.listFiles()) {
                User user1 = context.Users.get(ID.getIdFromFileName(file.getName()));
                if (user1.getUserName().equals(username) && user1.isActive()) {
                    if (user1.getId() == clientHandler.getCurrentUserId())
                        return new ShowMessage(explorerTexts.getItsU());
                     else {
                        clientHandler.setUserPfToBSeenId(user1.getId());
                        User currentUser=context.Users.get(clientHandler.getCurrentUserId());
                        logger.info("user "+currentUser.getId()+" opened "+user1.getId()+" 's profile page");
                        return new ProfilePageLogic(clientHandler).update();
                    }
                }
            }

        } catch (Exception e) { e.printStackTrace(); }

        return new ShowMessage(explorerTexts.getNotFound());
    }


    private List<Integer> worldList() {

        User user = context.Users.get(clientHandler.getCurrentUserId());
        List<Integer> tweets = new ArrayList<>();
        File directory = new File(infoConfig.getTweetsDirectory());
        for (File file : directory.listFiles()) {
            Tweet tweet = context.Tweets.get(ID.getIdFromFileName(file.getName()));
            if (!tweet.isBanned()) {
                User user1 = context.Users.get(tweet.getWriter());
                System.out.println(user1.getUserName() + " " + userLogic.isMutedBy(user1.getId(), user.getId()));
                if (user1.isActive() && !userLogic.isMutedBy(user1.getId(), user.getId())) {
                    if (user1.isPublic() || userLogic.isFollowing(user.getId(), user1.getId())) {
                        tweets.add(ID.getIdFromFileName(file.getName()));
                    }
                }
            }
        }

        if (!tweets.isEmpty())
            Collections.shuffle(tweets);

        return tweets;

    }

}
