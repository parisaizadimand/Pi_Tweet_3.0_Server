package ir.pi.project.server.config.texts.messages;


import ir.pi.project.server.config.texts.TextsConfig;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MessagesText {
    TextsConfig textsConfig=new TextsConfig();
    private String noSavedTweets;
    private String groupNotFound;
    private String userNotFound;
    private String takenGroupName;
    private String groupChatMade;
    private String ff;
    private String itsU;
    private String chosenUser;
    private String atLeastOne;
    private String emptyGroup;
    private String cantAddUrself;
    private String urIn;
    private String cantEditForwarded;
    private String cantEditDeleted;
    private String deletedMessage;
    private String emptyText;
    private String sent;
    private String emptyName;
    private String saved;
    private String savedMessages;
    private String leftTheGroupChat;
    private String scheduled;
    private String added;

    public MessagesText(){
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getMessaging());
        properties.load(fileReader);
        noSavedTweets = (String) properties.get("noSavedTweets");
        groupNotFound = (String) properties.get("groupNotFound");
        userNotFound = (String) properties.get("userNotFound");
        takenGroupName = (String) properties.get("takenGroupName");
        groupChatMade = (String) properties.get("groupChatMade");
        ff = (String) properties.get("ff");
        itsU = (String) properties.get("itsU");
        chosenUser = (String) properties.get("chosenUser");
        atLeastOne = (String) properties.get("atLeastOne");
        emptyGroup = (String) properties.get("emptyGroup");
        cantAddUrself = (String) properties.get("cantAddUrself");
        urIn = (String) properties.get("urIn");
        cantEditForwarded = (String) properties.get("cantEditForwarded");
        cantEditDeleted = (String) properties.get("cantEditDeleted");
        deletedMessage = (String) properties.get("deletedMessage");
        emptyText = (String) properties.get("emptyText");
        sent = (String) properties.get("sent");
        emptyName = (String) properties.get("emptyName");
        saved = (String) properties.get("saved");
        savedMessages = (String) properties.get("savedMessages");
        leftTheGroupChat = (String) properties.get("leftTheGroupChat");
        scheduled = (String) properties.get("scheduled");
        added = (String) properties.get("added");
    }

    public String getNoSavedTweets() { return noSavedTweets; }
    public String getGroupNotFound() { return groupNotFound; }
    public String getUserNotFound() { return userNotFound; }
    public String getTakenGroupName() { return takenGroupName; }
    public String getGroupChatMade() { return groupChatMade; }
    public String getFf() { return ff; }
    public String getItsU() { return itsU; }
    public String getChosenUser() { return chosenUser; }
    public String getAtLeastOne() { return atLeastOne; }
    public String getEmptyGroup() { return emptyGroup; }
    public String getCantAddUrself() { return cantAddUrself; }
    public String getUrIn() { return urIn; }
    public String getCantEditForwarded() { return cantEditForwarded; }
    public String getCantEditDeleted() { return cantEditDeleted; }
    public String getDeletedMessage() { return deletedMessage; }
    public String getEmptyText() { return emptyText; }
    public String getSent() { return sent; }
    public String getEmptyName() { return emptyName; }
    public String getSaved() { return saved; }
    public String getSavedMessages() { return savedMessages; }
    public String getLeftTheGroupChat() { return leftTheGroupChat; }
    public String getScheduled() { return scheduled; }
    public String getAdded() { return added; }
}
