package ir.pi.project.server.config.texts.myPage;

import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EditInfoTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String takenUsername;
    private String takenPhoneNumber;
    private String takenEmail;
    private String saved;
    private String emptyField;

    public EditInfoTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getEditInfo());
        properties.load(fileReader);
        takenEmail = (String) properties.get("takenEmail");
        takenPhoneNumber = (String) properties.get("takenPhoneNumber");
        takenUsername = (String) properties.get("takenUsername");
        saved = (String) properties.get("saved");
        emptyField = (String) properties.get("emptyField");
    }

    public String getTakenUsername() { return takenUsername; }
    public String getTakenPhoneNumber() { return takenPhoneNumber; }
    public String getTakenEmail() { return takenEmail; }
    public String getSaved() { return saved; }
    public String getEmptyField() { return emptyField; }
}
