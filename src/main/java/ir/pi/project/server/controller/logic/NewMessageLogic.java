package ir.pi.project.server.controller.logic;

import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.others.MessageStatus;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NewMessageLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(NewMessageLogic.class);

    public NewMessageLogic() {
        super();
    }

    public void newMessage(int userId, int user1Id, String ans, String imageInString, boolean isForwarded, LocalDateTime localDateTime) {
        User user = context.Users.get(userId);
        User user1 = context.Users.get(user1Id);
        boolean b = false;
        for (Map.Entry<String, Integer> entry : user1.getUnReadUsernames().entrySet()) {
            if (entry.getKey().equals(user.getUserName())) b = true;
        }
        if (!b) user1.getUnReadUsernames().put(user.getUserName(), 0);

        Message message = new Message(ID.newID(), userId, user1Id, ans);
        if (imageInString != null) {
            message.setImageInString(imageInString);
        }
        message.setStatus(MessageStatus.SENT);
        if (localDateTime != null) message.setTime(localDateTime);
        message.setForwarded(isForwarded);

        logger.info("new message with id: " + message.getId() + " was made");

        boolean chatExists = false;
        for (List<Integer> chat : user.getChats()) {
            Message message1 = context.Messages.get(chat.get(0));
            if ((message1.getReceiverId() == user1Id && message1.getSenderId() == userId) || (message1.getSenderId() == user1Id && message1.getReceiverId() == userId))
                chatExists = true;

        }
        if (chatExists) {
            for (List<Integer> chat : user.getChats()) {
                Message message1 = context.Messages.get(chat.get(0));
                if ((message1.getReceiverId() == user1Id && message1.getSenderId() == userId) || (message1.getSenderId() == user1Id && message1.getReceiverId() == userId)) {
                    chat.add(message.getId());
                    user1.getUnReadUsernames().put(user.getUserName(), user1.getUnReadUsernames().get(user.getUserName()) + 1);
                }
            }
            for (List<Integer> chat : user1.getChats()) {
                Message message1 = context.Messages.get(chat.get(0));
                if ((message1.getSenderId() == user1Id && message1.getReceiverId() == userId) || (message1.getReceiverId() == user1Id && message1.getSenderId() == userId))
                    chat.add(message.getId());

            }

        } else {
            List<Integer> newChat = new ArrayList<>();
            newChat.add(message.getId());
            user.getChats().add(newChat);
            user1.getChats().add(newChat);
            user1.getUnReadUsernames().put(user.getUserName(), user1.getUnReadUsernames().get(user.getUserName()) + 1);
        }

        context.Messages.update(message);
        context.Users.update(user);
        context.Users.update(user1);


        logger.info(user.getId() + " sent a message with id " + message.getId() + " to " + user1.getId());


    }
}
