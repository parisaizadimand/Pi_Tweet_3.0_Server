package ir.pi.project.server.controller.logic.entering;


import ir.pi.project.server.config.texts.entering.LogInTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.shared.enums.others.MessageStatus;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.LoadMainMenu;
import ir.pi.project.shared.response.others.ShowMessage;
import ir.pi.project.shared.util.Loop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.List;

public class LogInLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(LogInLogic.class);
    private final LogInTexts logInTexts = new LogInTexts();
    ClientHandler clientHandler;
    UserLogic userLogic;

    public LogInLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
    }

    public Response login(String username, String password) {
        if (!canBeFound(username, password)) {
            return new ShowMessage(logInTexts.getNotFound());
        } else {
            User user = userLogic.userByUsername(username);
            user.setLastSeen("Online");
            user.setOnline(true);
            if (user.getAuthToken() != 0) return new ShowMessage(logInTexts.getAlreadyIn());
            if (clientHandler.getAuthToken() == 0) {
                int authToken = new SecureRandom().nextInt();
                clientHandler.setAuthToken(authToken);
                user.setAuthToken(authToken);
            }

            context.Users.update(user);
            clientHandler.setCurrentUserId(user.getId());
            clientHandler.setSetMessagesStatusLoop(new Loop(1, new Runnable() {
                @Override
                public void run() {

                    User user = context.Users.get(clientHandler.getCurrentUserId());
                    for (List<Integer> chat : user.getChats()) {
                        for (Integer messageId : chat) {
                            try {
                                Message message = context.Messages.get(messageId);
                                if (message.getSenderId() != user.getId() && message.getStatus().equals(MessageStatus.SENT))
                                    message.setStatus(MessageStatus.DELIVERED);
                                context.Messages.update(message);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }));
            clientHandler.getSetMessagesStatusLoop().start();

            logger.info("user " + user.getId() + " logged in");
            return new LoadMainMenu(clientHandler.getAuthToken());
        }
    }


    private boolean canBeFound(String username, String password) {
        for (User user : context.Users.all())
            if (user.getUserName().equals(username) && user.getPassword().equals(password)) return true;

        return false;
    }


}
