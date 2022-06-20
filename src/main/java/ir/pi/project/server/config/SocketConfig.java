package ir.pi.project.server.config;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class SocketConfig {

    private final MainConfig mainConfigPath=new MainConfig();
    private Integer port;


    public SocketConfig(){
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(mainConfigPath.getSocketConfig());
        properties.load(fileReader);
        port = Integer.parseInt((String) properties.get("port"));

    }

    public Integer getPort() {
        return port;
    }
}
