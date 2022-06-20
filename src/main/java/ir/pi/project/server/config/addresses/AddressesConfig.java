package ir.pi.project.server.config.addresses;


import ir.pi.project.server.config.MainConfig;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class AddressesConfig {

    private MainConfig mainConfigPath=new MainConfig();
    private String info;

    public AddressesConfig() {
        try {
            setProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProperties() throws IOException {
        Properties properties = new Properties();
        FileReader fileReader = new FileReader(mainConfigPath.getAddresses());
        properties.load(fileReader);
        info = (String) properties.get("info");

    }

    public String getInfo() {
        return info;
    }
}
