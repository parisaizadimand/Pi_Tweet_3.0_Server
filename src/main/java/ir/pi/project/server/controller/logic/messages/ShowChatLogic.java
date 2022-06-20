package ir.pi.project.server.controller.logic.messages;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.UserLogic;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.enums.others.MessageStatus;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.model.help.HelpMessage;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.LoadChatPage;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.util.*;

public class ShowChatLogic extends MainLogic {
    private final MessagesText messagesText = new MessagesText();
    static private final Logger logger = LogManager.getLogger(ShowChatLogic.class);

    ClientHandler clientHandler;
    UserLogic userLogic;

    public ShowChatLogic(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.userLogic = new UserLogic(clientHandler);
    }

    public Response showChat(MessagesPage messagesPage, String string) {
        if (messagesPage.equals(MessagesPage.DIRECT_CHATS)) {
            if (userLogic.userCanBeFound(string)) {
                User user=context.Users.get(clientHandler.getCurrentUserId());
                User otherUser=userLogic.userByUsername(string);
                if(userLogic.isFollowing(user.getId(),otherUser.getId()) || userLogic.isFollowing(otherUser.getId(),user.getId())) {
                    setDirectChat(string);
                    logger.info("user "+user.getId()+" opened chat with "+otherUser.getId());
                    return loadChatPage(true);
                }else return new ShowMessage(messagesText.getFf());
            } else return new ShowMessage(messagesText.getUserNotFound());
        }//GroupChat
        else {
            setGroupChat(string);
            return loadChatPage(false);
        }
    }

    public Response loadChatPage(boolean isDirect) {
        updateCurrentChat(isDirect);
        return new LoadChatPage(getHelpMessageList(clientHandler.getCurrentChat().getMessages()), clientHandler.getCurrentChat().getName(), isDirect, clientHandler.getCurrentUserId());
    }

    public void setDirectChat(String username) {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        clientHandler.getCurrentChat().setDirect(true);
        boolean exists = false;
        for (List<Integer> list : user.getChats()) {
            if (!list.isEmpty()) {
                Message message = context.Messages.get(list.get(0));
                User user1 = context.Users.get(message.getSenderId());
                User user2 = context.Users.get(message.getReceiverId());
                if (user1.getUserName().equals(username) && user1.getId() != user.getId() || user2.getUserName().equals(username) && user2.getId() != user.getId())
                    if (user1.isActive() && user2.isActive()) {
                        exists = true;
                        clientHandler.getCurrentChat().setMessages(list);
                        if (user1.getUserName().equals(user.getUserName())) {
                            clientHandler.getCurrentChat().setName(user2.getUserName());
                            clientHandler.getCurrentChat().setTheOther(user2.getId());
                        } else {
                            clientHandler.getCurrentChat().setName(user1.getUserName());
                            clientHandler.getCurrentChat().setTheOther(user1.getId());
                        }
                    }
            }
        }
        if (!exists) {
            User user1 = userLogic.userByUsername(username);
            clientHandler.getCurrentChat().setMessages(null);
            clientHandler.getCurrentChat().setName(user1.getUserName());
            clientHandler.getCurrentChat().setTheOther(user1.getId());
        }

    }


    public void setGroupChat(String groupName){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Integer groupChatId: user.getGroupChats()) {
            GroupChat groupChat=context.GroupChats.get(groupChatId);
            if(groupChat.getGroupName().equals(groupName)){
                clientHandler.getCurrentChat().setMembers(groupChat.getMembers());
                clientHandler.getCurrentChat().setName(groupChat.getGroupName());
                clientHandler.getCurrentChat().setMessages(groupChat.getMessages());
                clientHandler.getCurrentChat().setTheOther(groupChatId);
                clientHandler.getCurrentChat().setGroupChatId(groupChatId);
                clientHandler.getCurrentChat().setDirect(false);
            }
        }
    }


    private List<HelpMessage> getHelpMessageList(List<Integer> messages) {
        List<HelpMessage> helpMessageList = new ArrayList<>();
        if(messages!=null){
            for (Integer messageId : messages) {
                Message message = context.Messages.get(messageId);
                User sender = context.Users.get(message.getSenderId());
                HelpMessage helpMessage = new HelpMessage(messageId, message.getText(), sender.getUserName(), sender.getId(), message.getTime(), message.getStatus());
                helpMessage.setImageInString(message.getImageInString());
                helpMessage.setSenderImageInString(sender.getProfileImageInString());
                if (!helpMessage.getTime().isAfter(LocalDateTime.now()))
                    helpMessageList.add(helpMessage);

            }
        }



        return getSortedHelpMessages(helpMessageList);
    }

    private List<HelpMessage> getSortedHelpMessages(List<HelpMessage> helpMessages){
        HelpMessage help;
        for(int i=0;i<helpMessages.size();i++){
            for(int j=i;j< helpMessages.size();j++){
                if(helpMessages.get(i).getTime().isAfter(helpMessages.get(j).getTime())){
                    help=helpMessages.get(i);
                    helpMessages.set(i,helpMessages.get(j));
                    helpMessages.set(j,help);
                }
            }
        }
        return helpMessages;
    }



    private void updateCurrentChat(boolean isDirect) {
            User user = context.Users.get(clientHandler.getCurrentUserId());
            if (isDirect) {
                for (List<Integer> chat : user.getChats()) {
                    if (!chat.isEmpty()) {
                        Message message = context.Messages.get(chat.get(0));
                        User user1 = context.Users.get(message.getSenderId());
                        User user2 = context.Users.get(message.getReceiverId());
                        User otherUser = context.Users.get(clientHandler.getCurrentChat().getTheOther());
                        if ((user1.getId() == otherUser.getId() && user2.getId() == user.getId()) || (user2.getId() == otherUser.getId() && user1.getId() == user.getId())) {

                            if (user1.isActive() && user2.isActive()) {
                                for (Integer messageId : chat) {
                                    Message message1 = context.Messages.get(messageId);
                                    if (message1.getSenderId() != user.getId()) {
                                        message1.setStatus(MessageStatus.SEEN);
                                        context.Messages.update(message1);
                                    }
                                }
                                clientHandler.getCurrentChat().setMessages(chat);
                                removeUnreadDirect(clientHandler.getCurrentChat().getName());
                            }


                        }


                    }
                }

                if (clientHandler.getCurrentChat().getTheOther().equals(clientHandler.getCurrentUserId()))
                    clientHandler.getCurrentChat().setMessages(user.getSavedMessages());

            } else {
                for (Integer groupChatId : user.getGroupChats()) {
                    GroupChat groupChat = context.GroupChats.get(groupChatId);
                    if (groupChat.getGroupName().equals(clientHandler.getCurrentChat().getName())) {
                        clientHandler.getCurrentChat().setMessages(groupChat.getMessages());
                        removeUnreadGroupChat(clientHandler.getCurrentChat().getName());
                    }
                }
            }

    }

    private void removeUnreadDirect(String username) {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        for (Map.Entry<String, Integer> entry : user.getUnReadUsernames().entrySet()) {
            if(entry.getKey().equals(username)) {
                user.getUnReadUsernames().remove(entry.getKey());
                context.Users.update(user);
                break;
            }
        }
    }
    private void removeUnreadGroupChat(String groupName){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        for (Map.Entry<Integer, Integer> entry : user.getUnreadGroupChats().entrySet()) {
            GroupChat groupChat=context.GroupChats.get(entry.getKey());
            if(groupChat.getGroupName().equals(groupName)) {
                user.getUnreadGroupChats().remove(entry.getKey());
                context.Users.update(user);
                break;
            }
        }
    }
}
