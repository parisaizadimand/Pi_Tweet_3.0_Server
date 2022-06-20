package ir.pi.project.server.config.texts.entering;

import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SignUpTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String takenUsername;
    private String takenPhoneNumber;
    private String takenEmail;
    private String successful;

    public SignUpTexts(){
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getSignUp());
        properties.load(fileReader);
        takenEmail = (String) properties.get("takenEmail");
        takenPhoneNumber = (String) properties.get("takenPhoneNumber");
        takenUsername = (String) properties.get("takenUsername");
        successful = (String) properties.get("successful");
    }

    public String getTakenUsername() { return takenUsername; }

    public String getTakenPhoneNumber() { return takenPhoneNumber; }

    public String getTakenEmail() { return takenEmail; }

    public String getSuccessful() { return successful; }
}
