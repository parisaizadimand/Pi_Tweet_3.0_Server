package ir.pi.project.server.controller.logic.settings;

import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.entering.LoadWelcomePage;
import ir.pi.project.shared.response.settings.LoadDeleteAccountPage;
import ir.pi.project.shared.response.settings.LoadPrivacySettingsPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class SettingsPageLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(SettingsPageLogic.class);
    ClientHandler clientHandler;

    public SettingsPageLogic(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
    }

    public Response check(SettingsPage settings){
        switch (settings) {
            case PRIVACY:
                return new LoadPrivacySettingsPage(clientHandler.getHelpUser(clientHandler.getCurrentUserId()));
            case DELETE_ACCOUNT:
                return new LoadDeleteAccountPage();
            default:
                logOut();
                return new LoadWelcomePage();
        }
    }


    public void logOut(){
        if (clientHandler.getCurrentUserId()!=-1) {
            User user = context.Users.get(clientHandler.getCurrentUserId());
            String date = LocalDateTime.now().getYear() + " " + LocalDateTime.now().getMonth() + " " + LocalDateTime.now().getDayOfMonth() +
                    " - " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute();
            user.setLastSeen("LastSeen: " + date);
            user.setOnline(false);
            user.setAuthToken(0);
            context.Users.update(user);

            clientHandler.getSetMessagesStatusLoop().stop();

            logger.info("user " + user.getId() + " logged out");
        }

    }



}
