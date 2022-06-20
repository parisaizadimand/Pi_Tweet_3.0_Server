package ir.pi.project.server.config.texts;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MainMenuTexts {
    TextsConfig textsConfig=new TextsConfig();
    private String emptyTimeLine;

    public MainMenuTexts() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(textsConfig.getMainMenu());
        properties.load(fileReader);
        emptyTimeLine = (String) properties.get("emptyTimeLine");
    }

    public String getEmptyTimeLine() {
        return emptyTimeLine;
    }
}
