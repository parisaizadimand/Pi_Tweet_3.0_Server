package ir.pi.project.server.config.texts.myPage;

import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MyPageTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String noTweets;
    private String noBio;
    private String emptyTextArea;
    private String successful;
    private String emptyBlacklist;
    private String emptyRequests;
    private String emptyPending;
    private String emptyNotifications;
    private String noFollowings;
    private String noFollower;


    public MyPageTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getMyPage());
        properties.load(fileReader);
        noBio = (String) properties.get("noBio");
        noTweets = (String) properties.get("noTweets");
        emptyTextArea = (String) properties.get("emptyTextArea");
        successful = (String) properties.get("successful");
        emptyBlacklist = (String) properties.get("emptyBlacklist");
        emptyRequests = (String) properties.get("emptyRequests");
        noFollowings = (String) properties.get("noFollowings");
        noFollower = (String) properties.get("noFollower");
        emptyPending = (String) properties.get("emptyPending");
        emptyNotifications = (String) properties.get("emptyNotifications");
    }

    public String getNoTweets() {
        return noTweets;
    }
    public String getNoBio() {
        return noBio;
    }
    public String getEmptyTextArea() { return emptyTextArea; }
    public String getSuccessful() {
        return successful;
    }
    public String getEmptyBlacklist() { return emptyBlacklist; }
    public String getEmptyRequests() { return emptyRequests; }
    public String getNoFollowings() { return noFollowings; }
    public String getNoFollower() { return noFollower; }
    public String getEmptyPending() { return emptyPending; }
    public String getEmptyNotifications() { return emptyNotifications; }
}
