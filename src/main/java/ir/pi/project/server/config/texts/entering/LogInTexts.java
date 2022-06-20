package ir.pi.project.server.config.texts.entering;

import ir.pi.project.server.config.texts.TextsConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LogInTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String notFound;
    private String alreadyIn;

    public LogInTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getLogIn());
        properties.load(fileReader);
        notFound = (String) properties.get("notFound");
        alreadyIn = (String) properties.get("alreadyIn");
    }

    public String getNotFound() {
        return notFound;
    }

    public String getAlreadyIn() {
        return alreadyIn;
    }
}
