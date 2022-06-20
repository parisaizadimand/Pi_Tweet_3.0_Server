package ir.pi.project.server.config.addresses;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class InfoConfig {
    AddressesConfig addressesConfig=new AddressesConfig();
    private String usersDirectory;
    private String tweetsDirectory;
    private String groupsDirectory;
    private String groupChatsDirectory;
    private String messagesDirectory;
    private String botsDirectory;

    public InfoConfig() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(addressesConfig.getInfo());
        properties.load(fileReader);
        usersDirectory = (String) properties.get("usersDirectory");
        tweetsDirectory = (String) properties.get("tweetsDirectory");
        groupsDirectory = (String) properties.get("groupsDirectory");
        groupChatsDirectory = (String) properties.get("groupChatsDirectory");
        messagesDirectory = (String) properties.get("messagesDirectory");
        botsDirectory = (String) properties.get("botsDirectory");
    }


    public String getUsersDirectory() { return usersDirectory; }

    public String getTweetsDirectory() { return tweetsDirectory; }

    public String getGroupsDirectory() { return groupsDirectory; }

    public String getGroupChatsDirectory() { return groupChatsDirectory; }

    public String getMessagesDirectory() { return messagesDirectory; }
    public String getBotsDirectory() { return botsDirectory; }

}
