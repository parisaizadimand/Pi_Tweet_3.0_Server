package ir.pi.project.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.shared.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;

public class MessageDB implements DBSet<Message>{
    static private final Logger logger= LogManager.getLogger(MessageDB.class);
    private final InfoConfig infoConfig=new InfoConfig();

    @Override
    public Message get(int id) {
        synchronized (logger) {
            try {
                File directory = new File(infoConfig.getMessagesDirectory());
                File Data = new File(directory, id + ".json");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Data));
//                logger.info("message file with " + id + " opened");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Message message = null;
                try {
                    message = gson.fromJson(bufferedReader, Message.class);
                } catch (Exception e) {
                    System.out.println(id);
                }
                bufferedReader.close();
                if (message == null) {
                    System.out.println("message id:" + id);
                }
                return message;
            } catch (IOException e) {
                logger.warn("message with id: " + id + " could not be found in message get");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public LinkedList<Message> all() {
        return null;
    }

    @Override
    public void update(Message message) {
        synchronized (logger) {
            try {
//            File directory=new File("./src/main/resources/Info/Messages");

//                System.out.println("message: "+message);
                File directory = new File(infoConfig.getMessagesDirectory());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                File Data = new File(directory, message.getId() + ".json");
                if (!Data.exists())
                    Data.createNewFile();
                FileWriter writer = new FileWriter(Data);
                writer.write(gson.toJson(message));
                writer.flush();
                writer.close();
//                logger.info("message with id " + message.getId() + " saved");

            } catch (IOException e) {

                logger.warn("message with id: " + message.getId() + " could not be saved in message update");
                e.printStackTrace();
            }
        }

    }
}
