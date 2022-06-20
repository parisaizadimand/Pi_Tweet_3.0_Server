package ir.pi.project.server.controller.logic.messages;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.shared.enums.others.Buttons;
import ir.pi.project.shared.enums.others.MessageOnWork;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.LoadButton;
import ir.pi.project.shared.response.others.Nothing;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageOnWorkLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(MessageOnWorkLogic.class);
    private final MessagesText messagesText = new MessagesText();

    ClientHandler clientHandler;

    public MessageOnWorkLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public Response check(MessageOnWork messageOnWork) {
        return switch (messageOnWork) {
            case SAVE -> save();
            case DELETE -> delete();
            default -> edit();
        };
    }


    public Response save() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        user.getSavedMessages().add(clientHandler.getMessageOnWorkId());
        context.Users.update(user);
        logger.info("user "+user.getId()+ " saved a message with id: " + clientHandler.getMessageOnWorkId());
        return new ShowMessage(messagesText.getSaved());
    }

    public Response delete() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        Message message = context.Messages.get(clientHandler.getMessageOnWorkId());
        message.setText(messagesText.getDeletedMessage());
        message.setImageInString(null);
        message.setDeleted(true);
        context.Messages.update(message);
        logger.info("user  "+ user.getId()+ "deleted message with id: " + message.getId());
        return new Nothing();
    }

    public Response edit() {
        if (context.Messages.get(clientHandler.getMessageOnWorkId()).isForwarded())
            return new ShowMessage(messagesText.getCantEditForwarded());
        if(context.Messages.get(clientHandler.getMessageOnWorkId()).isDeleted())
            return new ShowMessage(messagesText.getCantEditDeleted());

        return new LoadButton(Buttons.EDIT_BUTTON);
    }
}
