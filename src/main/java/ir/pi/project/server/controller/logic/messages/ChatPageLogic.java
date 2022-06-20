package ir.pi.project.server.controller.logic.messages;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.NewMessageLogic;
import ir.pi.project.server.db.ID;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.enums.others.MessageStatus;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.others.ShowMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;


public class ChatPageLogic extends MainLogic {
    static private final Logger logger = LogManager.getLogger(ChatPageLogic.class);
    private final MessagesText messagesText = new MessagesText();
    ClientHandler clientHandler;
    NewMessageLogic newMessageLogic;
    public ChatPageLogic(ClientHandler clientHandler) {
        super();
        this.clientHandler = clientHandler;
        this.newMessageLogic=new NewMessageLogic();
    }

    public Response check(MessagesPage messagesPage, ChatPage chatPage, String text) {
        if (chatPage == ChatPage.LEAVE) {
            return leaveGroup(text);
        }
        if (messagesPage.equals(MessagesPage.DIRECT_CHATS)) return editMessage(text, true);
        else return editMessage(text, false);
    }

    public Response newMessage(MessagesPage messagesPage,String text,String imageInString){
        if (text==null||text.equals("")) return new ShowMessage(messagesText.getEmptyText());
        if (messagesPage.equals(MessagesPage.DIRECT_CHATS))
            return addToDirectChat(text,imageInString);
        else return addToGroupChat(text,null,imageInString);
    }

    private Response addToDirectChat(String text, String imageInString) {
        if (clientHandler.getCurrentChat().getTheOther().equals(clientHandler.getCurrentUserId())) {
            User user = context.Users.get(clientHandler.getCurrentUserId());
            Message message = new Message(ID.newID(), user.getId(), user.getId(), text);
            if(imageInString!=null) {
                message.setImageInString(imageInString);
            }message.setStatus(MessageStatus.SEEN);
            user.getSavedMessages().add(message.getId());
            context.Messages.update(message);
            context.Users.update(user);
        } else {
            newMessageLogic.newMessage(clientHandler.getCurrentUserId(), clientHandler.getCurrentChat().getTheOther(), text, imageInString, false,null);
        }
        return new ShowChatLogic(clientHandler).loadChatPage(true);
    }


    public Response addToGroupChat(String text,LocalDateTime localDateTime,String imageInString){

        User user=context.Users.get(clientHandler.getCurrentUserId());
        GroupChat groupChat = null;
        for (Integer groupChatId:user.getGroupChats()) {
            groupChat=context.GroupChats.get(groupChatId);
            if(groupChat.getGroupName().equals(clientHandler.getCurrentChat().getName())) {
                Message newMessage = new Message(ID.newID(), clientHandler.getCurrentUserId(), groupChatId, text);
                if(imageInString!=null) newMessage.setImageInString(imageInString);

                if(localDateTime!=null)newMessage.setTime(localDateTime);
                groupChat.getMessages().add(newMessage.getId());
                context.GroupChats.update(groupChat);
                context.Messages.update(newMessage);
                break;
            }
        }

        assert groupChat != null;
        for (Integer memberId: groupChat.getMembers()) {
            if(!memberId.equals(clientHandler.getCurrentUserId())) {
                User member = context.Users.get(memberId);
                member.getUnreadGroupChats().putIfAbsent(groupChat.getId(), 0);
                member.getUnreadGroupChats().put(groupChat.getId(), member.getUnreadGroupChats().get(groupChat.getId()) + 1);
                context.Users.update(member);
            }
        }


        if(localDateTime!=null) return new ShowMessage(messagesText.getScheduled());


        return new ShowChatLogic(clientHandler).loadChatPage(false);

    }


    public Response editMessage(String text,boolean isDirect){
        User user = context.Users.get(clientHandler.getCurrentUserId());

        if(text.isEmpty()) return new ShowMessage(messagesText.getEmptyText());

        Message message=context.Messages.get(clientHandler.getMessageOnWorkId());
        message.setText(text);
        context.Messages.update(message);

        logger.info("user "+user.getUserName()+" edited message with id: "+message.getId() );
        return new ShowChatLogic(clientHandler).loadChatPage(isDirect);
    }


    public Response leaveGroup(String groupName){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        GroupChat groupChat = null;
        for (Integer groupChatId: user.getGroupChats())
            if(context.GroupChats.get(groupChatId).getGroupName().equals(groupName))
                groupChat = context.GroupChats.get(groupChatId);

        assert groupChat != null;
        Message newMessage = new Message(ID.newID(),user.getId(), groupChat.getId(), messagesText.getLeftTheGroupChat());
        groupChat.getMessages().add(newMessage.getId());

        groupChat.getMembers().remove((Object) user.getId());
        user.getGroupChats().remove((Object) groupChat.getId());


        context.Messages.update(newMessage);
        context.Users.update(user);
        context.GroupChats.update(groupChat);
        logger.info("user "+user.getId()+" left group "+groupName +"with id: "+groupChat.getId());
        return new ShowMessage(messagesText.getLeftTheGroupChat());
    }

    public Response sendScheduleMessage(MessagesPage messagesPage,LocalDateTime localDateTime,String text,String imageInString){
        User user=context.Users.get(clientHandler.getCurrentUserId());

        if(messagesPage.equals(MessagesPage.DIRECT_CHATS)) {
            User otherUser=context.Users.get(clientHandler.getCurrentChat().getTheOther());
            logger.info("user "+user.getId()+" scheduled a message in chat with user with id "+otherUser.getId());
            newMessageLogic.newMessage(clientHandler.getCurrentUserId(), clientHandler.getCurrentChat().getTheOther(), text, imageInString, false, localDateTime);
        }else {
            GroupChat groupChat=context.GroupChats.get(clientHandler.getCurrentChat().getGroupChatId());
            logger.info("user "+user.getId()+" scheduled a message in groupChat "+groupChat.getGroupName() +"with id: "+groupChat.getId());
            addToGroupChat(text,localDateTime,imageInString);
        }
        return new ShowMessage(messagesText.getScheduled());
    }

}
