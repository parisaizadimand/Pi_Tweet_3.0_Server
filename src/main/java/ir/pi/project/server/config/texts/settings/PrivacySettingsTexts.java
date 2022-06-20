package ir.pi.project.server.config.texts.settings;


import ir.pi.project.server.config.texts.TextsConfig;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class PrivacySettingsTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String wrongPassword;
    private String saved;

    public PrivacySettingsTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getPrivacySettings());
        properties.load(fileReader);
        wrongPassword = (String) properties.get("wrongPassword");
        saved = (String) properties.get("saved");
    }

    public String getWrongPassword() {
        return wrongPassword;
    }

    public String getSaved() {
        return saved;
    }
}


