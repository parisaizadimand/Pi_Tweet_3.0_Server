package ir.pi.project.server.config.texts.settings;

import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DeleteAccountTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String wrongPassword;

    public DeleteAccountTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getDeleteAccount());
        properties.load(fileReader);
        wrongPassword = (String) properties.get("wrongPassword");
    }

    public String getWrongPassword() {
        return wrongPassword;
    }
}
