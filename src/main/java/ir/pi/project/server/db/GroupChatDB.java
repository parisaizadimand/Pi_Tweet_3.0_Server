package ir.pi.project.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.shared.model.GroupChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;

public class GroupChatDB implements DBSet<GroupChat>{
    static private final Logger logger= LogManager.getLogger(GroupDB.class);
    private final InfoConfig infoConfig=new InfoConfig();

    @Override
    public GroupChat get(int id) {
        synchronized (logger) {
            try {
//            File directory=new File("./src/main/resources/Info/GroupChats");
                File directory = new File(infoConfig.getGroupChatsDirectory());
                File Data = new File(directory, id + ".json");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Data));
//            logger.info("groupChat file with id: "+id+ " opened");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                GroupChat groupChat = gson.fromJson(bufferedReader, GroupChat.class);
                bufferedReader.close();
                return groupChat;
            } catch (IOException e) {
                logger.warn("groupChat with id: " + id + " could not be found in groupChat get");
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public LinkedList<GroupChat> all() {
        return null;
    }


    @Override
    public void update(GroupChat groupChat) {
        synchronized (logger) {
            try {
//            File directory=new File("./src/main/resources/Info/GroupChats");
                File directory = new File(infoConfig.getGroupChatsDirectory());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                File Data = new File(directory, groupChat.getId() + ".json");
                if (!Data.exists())
                    Data.createNewFile();
                FileWriter writer = new FileWriter(Data);
                writer.write(gson.toJson(groupChat));
                writer.flush();
                writer.close();
//                logger.info("groupChat with id: " + groupChat.getId() + " saved");

            } catch (IOException e) {
                logger.warn("groupChat with id: " + groupChat.getId() + " could not be saved in groupChat update");
                e.printStackTrace();
            }
        }
    }
}
