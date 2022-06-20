package ir.pi.project.server.controller.logic.settings;

import ir.pi.project.server.config.texts.settings.PrivacySettingsTexts;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import ir.pi.project.shared.response.settings.LoadNewPassword;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrivacySettingsLogic extends MainLogic {

    static private final Logger logger = LogManager.getLogger(PrivacySettingsLogic.class);
    private final PrivacySettingsTexts privacySettingsTexts = new PrivacySettingsTexts();
    ClientHandler clientHandler;

    public PrivacySettingsLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public Response saveSettings(boolean isActive, boolean isPublic, String lastSeen, String password) {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        user.setActive(isActive);
        user.setPublic(isPublic);
        user.setLastSeenState(lastSeen);
        if (!password.equals(""))
            user.setPassword(password);
        context.Users.update(user);
        logger.info("user " + user.getId() + "'s privacy settings updated");

        return new ShowMessage(privacySettingsTexts.getSaved());
    }

    public Response checkPassword(String password) {
        User user = context.Users.get(clientHandler.getCurrentUserId());

        if (password.equals(user.getPassword()))
            return new LoadNewPassword();


        return new ShowMessage(privacySettingsTexts.getWrongPassword());
    }

}