package ir.pi.project.server.config.texts.myPage;


import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ForwardTweetTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String notFound;
    private String itsU;
    private String ff;
    private String forwardedFrom;
    private String forwarded;
    private String emptyUsername;

    public ForwardTweetTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getForwardTweet());
        properties.load(fileReader);
        notFound = (String) properties.get("notFound");
        itsU = (String) properties.get("itsU");
        ff = (String) properties.get("ff");
        forwardedFrom = (String) properties.get("forwardedFrom");
        forwarded = (String) properties.get("forwarded");
        emptyUsername = (String) properties.get("emptyUsername");
    }

    public String getNotFound() { return notFound; }
    public String getItsU() { return itsU; }
    public String getFf() { return ff; }
    public String getForwardedFrom() { return forwardedFrom; }
    public String getForwarded() { return forwarded; }
    public String getEmptyUsername() { return emptyUsername; }
}
