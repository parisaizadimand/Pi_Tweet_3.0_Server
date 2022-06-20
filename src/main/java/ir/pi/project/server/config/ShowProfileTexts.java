package ir.pi.project.server.config;

import ir.pi.project.server.config.texts.TextsConfig;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ShowProfileTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String follow;
    private String unfollow;
    private String mute;
    private String unmute;
    private String block;
    private String unblock;
    private String deleteRequest;
    private String following;
    private String requested;
    private String privateAcc;
    private String online;
    private String lastSeenRecently;
    private String urBlocked;
    private String uBlocked;
    private String noBio;
    private String reportText;
    private String noTweets;
    private String startedFollowing;
    private String stoppedFollowing;
    private String requestAccepted;
    private String requestDeleted;
    private String cantSeeTweets;

    public ShowProfileTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getShowProfile());
        properties.load(fileReader);
        follow = (String) properties.get("follow");
        unfollow = (String) properties.get("unfollow");
        block = (String) properties.get("block");
        unblock = (String) properties.get("unblock");
        mute = (String) properties.get("mute");
        unmute = (String) properties.get("unmute");
        deleteRequest = (String) properties.get("deleteRequest");
        following = (String) properties.get("following");
        requested = (String) properties.get("requested");
        privateAcc = (String) properties.get("privateAcc");
        online = (String) properties.get("online");
        lastSeenRecently = (String) properties.get("lastSeenRecently");
        urBlocked = (String) properties.get("urBlocked");
        uBlocked = (String) properties.get("uBlocked");
        noBio = (String) properties.get("noBio");
        reportText = (String) properties.get("reportText");
        noTweets = (String) properties.get("noTweets");
        startedFollowing = (String) properties.get("startedFollowing");
        stoppedFollowing = (String) properties.get("stoppedFollowing");
        requestAccepted = (String) properties.get("requestAccepted");
        requestDeleted = (String) properties.get("requestDeleted");
        cantSeeTweets = (String) properties.get("cantSeeTweets");
    }

    public String getFollow() {
        return follow;
    }
    public String getUnfollow() {
        return unfollow;
    }
    public String getMute() {
        return mute;
    }
    public String getUnmute() {
        return unmute;
    }
    public String getBlock() {
        return block;
    }
    public String getUnblock() {
        return unblock;
    }
    public String getDeleteRequest() {
        return deleteRequest;
    }
    public String getFollowing() {
        return following;
    }
    public String getRequested() {
        return requested;
    }
    public String getPrivateAcc() {
        return privateAcc;
    }
    public String getOnline() {
        return online;
    }
    public String getLastSeenRecently() {
        return lastSeenRecently;
    }
    public String getUrBlocked() {
        return urBlocked;
    }
    public String getuBlocked() {
        return uBlocked;
    }
    public String getNoBio() {
        return noBio;
    }
    public String getReportText() {
        return reportText;
    }
    public String getNoTweets() {
        return noTweets;
    }
    public String getCantSeeTweets() {
        return cantSeeTweets;
    }
    public String getStartedFollowing() { return startedFollowing; }
    public String getStoppedFollowing() { return stoppedFollowing; }

    public String getRequestAccepted() {
        return requestAccepted;
    }

    public String getRequestDeleted() {
        return requestDeleted;
    }
}
