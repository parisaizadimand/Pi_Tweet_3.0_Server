package ir.pi.project.server.controller;

import ir.pi.project.server.controller.logic.*;
import ir.pi.project.server.controller.logic.entering.WelcomeLogic;
import ir.pi.project.server.controller.logic.messages.*;
import ir.pi.project.server.controller.logic.messages.groupChat.GroupChatsLogic;
import ir.pi.project.server.controller.logic.messages.groups.GroupsLogic;
import ir.pi.project.server.controller.logic.myPage.EditInfoLogic;
import ir.pi.project.server.controller.logic.myPage.ListsLogic;
import ir.pi.project.server.controller.logic.myPage.MyPageLogic;
import ir.pi.project.server.controller.logic.settings.DeleteAccountPageLogic;
import ir.pi.project.server.controller.logic.settings.PrivacySettingsLogic;
import ir.pi.project.server.controller.logic.settings.SettingsPageLogic;
import ir.pi.project.server.db.Context;
import ir.pi.project.server.model.CurrentChat;
import ir.pi.project.shared.enums.Pages.*;
import ir.pi.project.shared.enums.others.*;
import ir.pi.project.shared.event.Event;
import ir.pi.project.shared.event.EventVisitor;
import ir.pi.project.shared.model.Tweet;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.model.help.HelpUser;
import ir.pi.project.shared.response.*;
import ir.pi.project.shared.response.entering.LoadLoginPage;
import ir.pi.project.shared.response.entering.LoadSignUpPage;
import ir.pi.project.shared.response.myPage.LoadMyPage;
import ir.pi.project.shared.response.myPage.LoadNotificationsPage;
import ir.pi.project.shared.response.others.LoadMainMenu;
import ir.pi.project.shared.response.others.LoadTweet;
import ir.pi.project.shared.response.others.ShowMessage;
import ir.pi.project.shared.response.settings.LoadCurrentPassword;
import ir.pi.project.shared.util.Loop;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread implements EventVisitor {

    private final ResponseSender sender;
    private int authToken = 0;
    Integer currentUserId=-1;
    Integer userPfToBSeenId=-1;
    int currentTweetIndex;
    List<Integer> currentTweetList;
    Context context = new Context();

    List<Integer> currentMultipleUsers;
    List<Integer> currentMultipleGroups;

    List<List<Integer>> listOfTweets;

    Integer currentGroupId=-1;

    String newGroupChatName;
    List<Integer> newGroupChatMembers;


    CurrentChat currentChat;

    Integer messageOnWorkId=-1;

    Loop setMessagesStatusLoop;



    public ClientHandler(ResponseSender sender) {
        this.sender = sender;
        this.listOfTweets = new ArrayList<>();
        currentMultipleGroups = new ArrayList<>();
        currentMultipleUsers = new ArrayList<>();

        newGroupChatName=null;
        newGroupChatMembers=new ArrayList<>();

        currentChat=new CurrentChat();

    }

    public void run() {
        while (true) {
            Event event = sender.getEvent();
            if (event.getAuthToken() == authToken)
                sender.sendResponse(event.visit(this));
            else sender.sendResponse(new ShowMessage("InvalidToken"));
        }
    }

    @Override
    public Response signUp(String firstName, String lastName, String userName, String password, String email, String phoneNumber, String birthDate, boolean canSee) {
        return new WelcomeLogic(this).signUp(firstName, lastName, userName, password, email, phoneNumber, birthDate, canSee);
    }

    @Override
    public Response logIn(String username, String password) {
        return new WelcomeLogic(this).logIn(username, password);
    }

    @Override
    public Response welcomePageEvent(WelcomePage welcomePage) {
        if (welcomePage.equals(WelcomePage.LOG_IN))
            return new LoadLoginPage();
        return new LoadSignUpPage();
    }

    @Override
    public Response mainMenuPageEvent(MainMenuPage mainMenuPage) {
        return new MainMenuLogic(this).loadPage(mainMenuPage);
    }

    @Override
    public Response getMainMenu() {
        return new LoadMainMenu(authToken);
    }

    @Override
    public Response myPageEvent(MyPage myPage) {
        return new MyPageLogic(this).checkEvent(myPage);
    }

    @Override
    public Response newTweet(String text,String tweetImageInByte) {
        return new MyPageLogic(this).newTweet(text,tweetImageInByte);
    }

    @Override
    public Response myPageUpReq() {
        return new LoadMyPage(getHelpUser(currentUserId));
    }

    @Override
    public Response notificationsUpReq() {
        return new LoadNotificationsPage(context.Users.get(currentUserId).getNotifications());
    }

    @Override
    public Response pendingListUpReq() {
        return new MyPageLogic(this).loadPending();
    }

    @Override
    public Response forwardTweetEvent(String username) {
        return new ShowTweetLogic(this).forward(username);
    }

    @Override
    public Response unblock(String username) {
        return new ListsLogic(this).unblock(username);
    }

    @Override
    public Response requestComponentEvent(String username, RequestComponent requestComponent) {
        return new ListsLogic(this).checkRequestEvent(username, requestComponent);
    }

    @Override
    public Response requestsListUpReq() {
        return new MyPageLogic(this).loadRequestsList();
    }

    @Override
    public Response deleteFollowerEvent(String username) {
        return new ListsLogic(this).deleteFollower(username);
    }

    @Override
    public Response followersListUpReq() {
        return new MyPageLogic(this).loadFollowersList();
    }

    @Override
    public Response unfollowEvent(String username) {
        return new ListsLogic(this).unfollow(username);
    }

    @Override
    public Response followingsListUpReq() {
        return new MyPageLogic(this).loadFollowingsList();
    }

    @Override
    public Response saveInfoEvent(String firstName, String lastName, String userName, String email, String phoneNumber, String birthDate, String biography, boolean canSee,String imageInByte) {
        return new EditInfoLogic(this).save(firstName, lastName, userName, email, phoneNumber, birthDate, biography, canSee,imageInByte);

    }

    @Override
    public Response tweetComponentEvent(TweetComponent tweetComponent) {
        return new ShowTweetLogic(this).check(tweetComponent);
    }

    @Override
    public Response showTweetUpReq() {
        Tweet tweet = context.Tweets.get(currentTweetList.get(currentTweetIndex));
        User writer = context.Users.get(tweet.getWriter());
        HelpUser retweetedBy=null;
        if(tweet.isRetweeted())retweetedBy=getHelpUser(tweet.getRetweetedBy());
        return new LoadTweet(tweet, getHelpUser(writer.getId()),retweetedBy);
    }

    @Override
    public Response newComment(String text,String imageInByte) {
        return new ShowTweetLogic(this).newComment(text,imageInByte);
    }

    @Override
    public Response explorerPageEvent(String username, ExplorerPage explorerPage) {
        return new ExplorerPageLogic(this).check(username, explorerPage);

    }

    @Override
    public Response profilePageEvent(ProfilePage profilePage) {
        return new ProfilePageLogic(this).check(profilePage);
    }

    @Override
    public Response profilePageUpReq() {
        return new ProfilePageLogic(this).update();
    }

    @Override
    public Response messagesPageEvent(MessagesPage messagesPage) {
        return new MessagesPageLogic(this).check(messagesPage);
    }

    @Override
    public Response addToMultiplesEvent(MessagesPage messagesPage, String string) {
        return new MessagesPageLogic(this).checkAddToMultiples(messagesPage, string);
    }

    @Override
    public Response sendMultipleMessageEvent(MessagesPage messagesPage, String text,String imageInString) {
        return new MessagesPageLogic(this).checkSendMultipleMessage(messagesPage, text,imageInString);
    }

    @Override
    public Response groupsPageEvent(GroupsPage groupsPage, String string) {
        return new GroupsLogic(this).check(groupsPage, string);
    }

    @Override
    public Response messagesUpReqEvent(MessagesUpReqs messagesUpReqs) {
        return new MessagesUpReqLogic(this).check(messagesUpReqs);
    }

    @Override
    public Response newGroupChatEvent(NewGroupChat newGroupChat, String string) {
        return new GroupChatsLogic(this).newGroupChatCheck(newGroupChat,string);
    }

    @Override
    public Response showChatEvent(MessagesPage messagesPage, String string) {
        return new ShowChatLogic(this).showChat(messagesPage,string);
    }

    @Override
    public Response chatPageEvent(MessagesPage messagesPage,ChatPage chatPage, String text) {
        return new ChatPageLogic(this).check(messagesPage,chatPage,text);
    }

    @Override
    public Response newMessageEvent(MessagesPage messagesPage, String text, String imageInString) {
        return new ChatPageLogic(this).newMessage(messagesPage,text,imageInString);
    }

    @Override
    public Response messageOnWorkEvent(MessageOnWork messageOnWork, int messageOnWorkId) {
        this.messageOnWorkId=messageOnWorkId;
        return new MessageOnWorkLogic(this).check(messageOnWork);
    }

    @Override
    public Response scheduleMessageEvent(MessagesPage messagesPage,LocalDateTime localDateTime, String text,String imageInString) {
        return new ChatPageLogic(this).sendScheduleMessage(messagesPage,localDateTime,text,imageInString);
    }

    @Override
    public Response closeEvent() {
        new SettingsPageLogic(this).logOut();

        this.stop();

        return null;
    }

    @Override
    public Response settingsPageEvent(SettingsPage settingsPage) {
        return new SettingsPageLogic(this).check(settingsPage);
    }

    @Override
    public Response deleteAccount(String password) {
        return new DeleteAccountPageLogic(this).checkPassword(password);
    }

    @Override
    public Response changePasswordEvent() {
        return new LoadCurrentPassword();
    }

    @Override
    public Response checkPassword(String password) {
        return new PrivacySettingsLogic(this).checkPassword(password);
    }


    @Override
    public Response savePrivacySettingsEvent(boolean isActive, boolean isPublic, String lastSeen, String password) {
        return new PrivacySettingsLogic(this).saveSettings(isActive, isPublic, lastSeen, password);
    }



    public HelpUser getHelpUser(int userId){
        User currentUser=context.Users.get(userId);
        HelpUser helpUser=new HelpUser(currentUser.getFirstName(),currentUser.getLastName(), currentUser.getUserName()
                ,currentUser.getPassword(),currentUser.getBirthDate(),currentUser.getEmail(),currentUser.getPhoneNumber());
        helpUser.setBiography(currentUser.getBiography());
        helpUser.setLastSeen(currentUser.getLastSeen());
        helpUser.setLastSeenState(currentUser.getLastSeenState());
        helpUser.setEPBCanSee(currentUser.isEPBCanSee());
        helpUser.setActive(currentUser.isActive());
        helpUser.setOnline(currentUser.isOnline());
        helpUser.setPublic(currentUser.isPublic());
        helpUser.setFollowers(currentUser.getFollowers());
        helpUser.setFollowings(currentUser.getFollowings());
        helpUser.setRetweets(currentUser.getRetweets());
        helpUser.setEPBCanSee(currentUser.isEPBCanSee());
        helpUser.setProfileImageInString(currentUser.getProfileImageInString());

        return helpUser;
    }


    public int getAuthToken() {
        return authToken;
    }
    public void setAuthToken(int authToken) {
        this.authToken = authToken;
    }

    public void setCurrentUserId(Integer currentUserId) { this.currentUserId = currentUserId; }
    public Integer getCurrentUserId() { return currentUserId; }
    public int getCurrentTweetIndex() { return currentTweetIndex; }
    public List<Integer> getCurrentTweetList() { return currentTweetList; }
    public void setCurrentTweetIndex(int currentTweetIndex) { this.currentTweetIndex = currentTweetIndex; }
    public void setCurrentTweetList(List<Integer> currentTweetList) { this.currentTweetList = currentTweetList; }
    public List<List<Integer>> getListOfTweets() { return listOfTweets; }
    public void setListOfTweets(List<List<Integer>> listOfTweets) { this.listOfTweets = listOfTweets; }
    public Integer getUserPfToBSeenId() { return userPfToBSeenId; }
    public void setUserPfToBSeenId(Integer userPfToBSeenId) { this.userPfToBSeenId = userPfToBSeenId; }
    public List<Integer> getCurrentMultipleUsers() { return currentMultipleUsers; }
    public void setCurrentMultipleUsers(List<Integer> currentMultipleUsers) { this.currentMultipleUsers = currentMultipleUsers; }
    public List<Integer> getCurrentMultipleGroups() { return currentMultipleGroups; }
    public void setCurrentMultipleGroups(List<Integer> currentMultipleGroups) { this.currentMultipleGroups = currentMultipleGroups; }
    public Integer getCurrentGroupId() { return currentGroupId; }
    public void setCurrentGroupId(Integer currentGroupId) { this.currentGroupId = currentGroupId; }
    public String getNewGroupChatName() { return newGroupChatName; }
    public void setNewGroupChatName(String newGroupChatName) { this.newGroupChatName = newGroupChatName; }
    public List<Integer> getNewGroupChatMembers() { return newGroupChatMembers; }
    public void setNewGroupChatMembers(List<Integer> newGroupChatMembers) { this.newGroupChatMembers = newGroupChatMembers; }
    public CurrentChat getCurrentChat() { return currentChat; }
    public void setCurrentChat(CurrentChat currentChat) { this.currentChat = currentChat; }
    public Integer getMessageOnWorkId() { return messageOnWorkId; }
    public void setMessageOnWorkId(Integer messageOnWorkId) { this.messageOnWorkId = messageOnWorkId; }
    public Loop getSetMessagesStatusLoop() {
        return setMessagesStatusLoop;
    }
    public void setSetMessagesStatusLoop(Loop setMessagesStatusLoop) { this.setMessagesStatusLoop = setMessagesStatusLoop; }
}
