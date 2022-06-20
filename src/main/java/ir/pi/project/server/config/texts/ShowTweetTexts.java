package ir.pi.project.server.config.texts;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ShowTweetTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String noComment;
    private String saved;
    private String reported;
    private String commented;
    private String noMore;
    private String retweeted;
    private String emptyTextArea;
    private String liked;
    private String disliked;
    private String cantRetweetUrs;

    public ShowTweetTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getShowTweet());
        properties.load(fileReader);
        noComment = (String) properties.get("noComment");
        saved = (String) properties.get("saved");
        reported = (String) properties.get("reported");
        commented = (String) properties.get("commented");
        noMore = (String) properties.get("noMore");
        retweeted = (String) properties.get("retweeted");
        emptyTextArea = (String) properties.get("emptyTextArea");
        liked = (String) properties.get("liked");
        disliked = (String) properties.get("disliked");
        cantRetweetUrs = (String) properties.get("cantRetweetUrs");
    }

    public String getNoComment() { return noComment; }
    public String getSaved() { return saved; }
    public String getReported() { return reported; }
    public String getCommented() { return commented; }
    public String getNoMore() { return noMore; }
    public String getRetweeted() { return retweeted; }
    public String getEmptyTextArea() { return emptyTextArea; }
    public String getLiked() { return liked; }
    public String getDisliked() { return disliked; }
    public String getCantRetweetUrs() { return cantRetweetUrs; }
}
