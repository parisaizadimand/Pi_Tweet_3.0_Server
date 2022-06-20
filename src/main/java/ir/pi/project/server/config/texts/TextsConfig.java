package ir.pi.project.server.config.texts;
import ir.pi.project.server.config.MainConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class TextsConfig {
    private final MainConfig mainConfigPath=new MainConfig();
    private String signUp;
    private String logIn;
    private String editInfo;
    private String showProfile;
    private String explorer;
    private String privacySettings;
    private String deleteAccount;
    private String mainMenu;
    private String forwardTweet;
    private String myPage;
    private String showTweet;
    private String messaging;

    public TextsConfig() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(mainConfigPath.getTexts());
        properties.load(fileReader);
        signUp = (String) properties.get("signUp");
        logIn = (String) properties.get("logIn");
        editInfo = (String) properties.get("editInfo");
        showProfile = (String) properties.get("showProfile");
        explorer = (String) properties.get("explorer");
        privacySettings = (String) properties.get("privacySettings");
        deleteAccount = (String) properties.get("deleteAccount");
        mainMenu = (String) properties.get("mainMenu");
        myPage = (String) properties.get("myPage");
        showTweet = (String) properties.get("showTweet");
        messaging = (String) properties.get("messaging");
        forwardTweet = (String) properties.get("forwardTweet");


    }

    public String getSignUp() { return signUp;}
    public String getLogIn() { return logIn; }
    public String getEditInfo() { return editInfo; }
    public String getShowProfile() { return showProfile; }
    public String getExplorer() { return explorer; }
    public String getPrivacySettings() { return privacySettings; }
    public String getDeleteAccount() { return deleteAccount; }
    public String getMainMenu() { return mainMenu; }
    public String getForwardTweet() { return forwardTweet; }
    public String getMyPage() { return myPage; }
    public String getShowTweet() { return showTweet; }
    public String getMessaging() { return messaging; }
}
