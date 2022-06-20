package ir.pi.project.server.controller.logic.messages.multipleMessaging;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.NewMessageLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.multipleMessaging.LoadMultipleToUsersPage;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

public class MultipleToUsersLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(MultipleToUsersLogic.class);
    private final MessagesText messagesText = new MessagesText();
    NewMessageLogic newMessageLogic;
    ClientHandler clientHandler;
    UserLogic userLogic;

    public MultipleToUsersLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
        this.newMessageLogic = new NewMessageLogic();
    }

    public Response addToUsernames(String username) {
        if (username == null || username.equals("") || !userLogic.userCanBeFound(username))
            return new ShowMessage(messagesText.getUserNotFound());
        User me = context.Users.get(clientHandler.getCurrentUserId());
        if (username.equals(me.getUserName()))
            return new ShowMessage(messagesText.getItsU());

        User user = userLogic.userByUsername(username);
        if (!user.isActive())
            return new ShowMessage(messagesText.getUserNotFound());
        List<Integer> users = clientHandler.getCurrentMultipleUsers();
        if (users.contains(user.getId()))
            return new ShowMessage(messagesText.getChosenUser());

        if (!userLogic.isFollowing(user.getId(), me.getId()) && !userLogic.isFollowing(me.getId(), user.getId()))
            return new ShowMessage(messagesText.getFf());

        users.add(user.getId());
        List<String> usernames = new ArrayList<>();
        for (Integer userId : users) {
            usernames.add(context.Users.get(userId).getUserName());
        }
        return new LoadMultipleToUsersPage(usernames);

    }


    public Response sendMessage(String text,String imageInString) {
        if (text == null || text.equals(""))
            return new ShowMessage(messagesText.getEmptyText());
        User user = context.Users.get(clientHandler.getCurrentUserId());
        if (clientHandler.getCurrentMultipleUsers().isEmpty())
            return new ShowMessage(messagesText.getAtLeastOne());
        else {
            for (Integer userId : clientHandler.getCurrentMultipleUsers()) {
                User user1 = context.Users.get(userId);
                newMessageLogic.newMessage(user.getId(), user1.getId(), text, imageInString, false,null);
                clientHandler.setCurrentMultipleUsers(new ArrayList<>());
            }
        }

        logger.info("user "+user.getId()+" sent multiple message to users");
        return new ShowMessage(messagesText.getSent());

    }
}

