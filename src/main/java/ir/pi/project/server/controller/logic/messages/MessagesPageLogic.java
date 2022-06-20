package ir.pi.project.server.controller.logic.messages;

import ir.pi.project.server.config.texts.messages.MessagesText;
import ir.pi.project.server.controller.ClientHandler;
import ir.pi.project.server.controller.logic.MainLogic;
import ir.pi.project.server.controller.logic.ShowTweetLogic;
import ir.pi.project.server.controller.logic.messages.groupChat.GroupChatsLogic;
import ir.pi.project.server.controller.logic.messages.multipleMessaging.MultipleToGroupsLogic;
import ir.pi.project.server.controller.logic.messages.multipleMessaging.MultipleToUsersLogic;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.model.Group;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.Message;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.response.Response;
import ir.pi.project.shared.response.messages.directChat.LoadDirectChatsListPage;
import ir.pi.project.shared.response.messages.groupChat.LoadGroupChatsListPage;
import ir.pi.project.shared.response.messages.groups.LoadGroupsPage;
import ir.pi.project.shared.response.messages.multipleMessaging.LoadMultipleToGroupsPage;
import ir.pi.project.shared.response.messages.multipleMessaging.LoadMultipleToUsersPage;
import ir.pi.project.shared.response.others.ShowMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesPageLogic extends MainLogic {
    private final MessagesText messagesText = new MessagesText();
    ClientHandler clientHandler;

    public MessagesPageLogic(ClientHandler clientHandler) {
        super();
        this.clientHandler = clientHandler;
    }

    public Response check(MessagesPage messagesPage) {
        return switch (messagesPage) {
            case GROUP_CHATS -> loadGroupChatsListPage();
            case NEW_GROUP_CHAT -> new GroupChatsLogic(clientHandler).loadNewGroupChat();
            case DIRECT_CHATS -> loadDirectChatsListPage();
            case TO_GROUPS -> new LoadMultipleToGroupsPage(null);
            case TO_USERS -> new LoadMultipleToUsersPage(null);
            case SAVED_MESSAGES ->  loadSavedMessages();
            case SHOW_GROUPS -> loadGroupsPage();
            default -> loadSavedTweets();
        };
    }


    public Response checkAddToMultiples(MessagesPage messagesPage, String string) {
        if (messagesPage.equals(MessagesPage.TO_GROUPS)) {
            return new MultipleToGroupsLogic(clientHandler).addToGroupNames(string);
        } else {
            return new MultipleToUsersLogic(clientHandler).addToUsernames(string);
        }
    }

    public Response checkSendMultipleMessage(MessagesPage messagesPage, String text,String imageInString) {
        if (messagesPage.equals(MessagesPage.TO_GROUPS)) {
            return new MultipleToGroupsLogic(clientHandler).sendMessage(text,imageInString);
        } else {
            return new MultipleToUsersLogic(clientHandler).sendMessage(text,imageInString);
        }
    }

    private Response loadGroupsPage() {
        User me = context.Users.get(clientHandler.getCurrentUserId());
        List<String> groupNames = new ArrayList<>();
        for (Integer groupId : me.getGroups()) {
            Group group = context.Groups.get(groupId);
            groupNames.add(group.getName());
        }
        return new LoadGroupsPage(groupNames);
    }


    private Response loadSavedTweets() {
        User user = context.Users.get(clientHandler.getCurrentUserId());
        if (!user.getSavedTweets().isEmpty()) {
            clientHandler.setCurrentTweetList(user.getSavedTweets());
            clientHandler.setCurrentTweetIndex(user.getSavedTweets().size() - 1);
            clientHandler.getListOfTweets().add(user.getSavedTweets());

            return new ShowTweetLogic(clientHandler).showTweet();
        }
        return new ShowMessage(messagesText.getNoSavedTweets());
    }


    public Response loadDirectChatsListPage() {
            User user = context.Users.get(clientHandler.getCurrentUserId());

            List<String> unreadUsernames = new ArrayList<>();
            if (!user.getUnReadUsernames().isEmpty())
                for (HashMap.Entry<String, Integer> entry : user.getUnReadUsernames().entrySet())
                    unreadUsernames.add(entry.getKey());

            List<String> alreadyReadUsernames = new ArrayList<>();

            if (!user.getChats().isEmpty()) {
                for (List<Integer> list : user.getChats()) {
                    if (!list.isEmpty()) {
                        Message message = context.Messages.get(list.get(0));
//                        System.out.println("were in MessagePageLogic");
//                        System.out.println("in "+context.Users.get(clientHandler.getCurrentUserId()).getUserName()+"~~");
//                        System.out.println("message with id: "+list.get(0)+" : "+message);
                        User user1 = context.Users.get(message.getSenderId());
                        User user2 = context.Users.get(message.getReceiverId());
                        if (user1.isActive() && user2.isActive()) {
                            if (user2.getId() == user.getId() && directChatIsAlreadyRead(unreadUsernames, user1.getUserName()))
                                alreadyReadUsernames.add(user1.getUserName());
                            if (user1.getId() == user.getId() && directChatIsAlreadyRead(unreadUsernames, user2.getUserName()))
                                alreadyReadUsernames.add(user2.getUserName());
                        }
                    }
                }
            }

            return new LoadDirectChatsListPage(user.getUnReadUsernames(), alreadyReadUsernames);

    }

    private boolean directChatIsAlreadyRead(List<String> unreadUsernames, String userName) {
        for (String s : unreadUsernames)
            if (s.equals(userName)) return false;
        return true;
    }



    public Response loadGroupChatsListPage(){
        User user = context.Users.get(clientHandler.getCurrentUserId());
        List<GroupChat> unreadGroupChats=new ArrayList<>();
        Map<String,Integer> unreadGroupChatsMap=new HashMap<>();
        if (!user.getUnreadGroupChats().isEmpty()) {
            for (HashMap.Entry<Integer, Integer> entry : user.getUnreadGroupChats().entrySet()) {
                GroupChat groupChat=context.GroupChats.get(entry.getKey());
                unreadGroupChats.add(groupChat);
                unreadGroupChatsMap.put(groupChat.getGroupName(),entry.getValue());
            }
        }

        List<String> alreadyReadGroupChats=new ArrayList<>();
        if (!user.getGroupChats().isEmpty()) {
            for (Integer groupChatId : user.getGroupChats()) {
                GroupChat groupChat=context.GroupChats.get(groupChatId);
                if(groupChatIsAlreadyRead(unreadGroupChats,groupChatId)){
                    alreadyReadGroupChats.add(groupChat.getGroupName());
                }
            }
        }


        return new LoadGroupChatsListPage(unreadGroupChatsMap,alreadyReadGroupChats);
    }


    private boolean groupChatIsAlreadyRead(List<GroupChat> unreadGroupChats, Integer groupId){
        for (GroupChat unreadGroupChat : unreadGroupChats)
            if (unreadGroupChat.getId() == groupId) return false;
        return true;
    }

    private Response loadSavedMessages(){
        User user=context.Users.get(clientHandler.getCurrentUserId());
        clientHandler.getCurrentChat().setMessages(user.getSavedMessages());
        clientHandler.getCurrentChat().setTheOther(user.getId());
        clientHandler.getCurrentChat().setName(messagesText.getSavedMessages());
        clientHandler.getCurrentChat().setDirect(true);
        return new ShowChatLogic(clientHandler).loadChatPage(true);
    }


}
