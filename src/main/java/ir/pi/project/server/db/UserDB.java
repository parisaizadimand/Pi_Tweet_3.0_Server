package ir.pi.project.server.db;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;

public class UserDB implements DBSet<User> {

    static private final Logger logger= LogManager.getLogger(UserDB.class);
    private final InfoConfig infoConfig=new InfoConfig();

    @Override
    public User get(int id) {
        synchronized (logger) {
            try {
//            File directory = new File("./src/main/resources/Info/Users");
                File directory = new File(infoConfig.getUsersDirectory());
                File Data = new File(directory, id + ".json");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Data));
//                logger.info("user file with id: " + id + " opened");
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                User user = gson.fromJson(bufferedReader, User.class);
                bufferedReader.close();
                return user;
            } catch (IOException e) {
                logger.warn("user with id: " + id + " could not be found in getByID");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public LinkedList<User> all() {
        LinkedList<User> users=new LinkedList<>();
//        File directory=new File("./src/main/resources/Info/Users");
        File directory=new File(infoConfig.getUsersDirectory());
        for (File file:
                directory.listFiles()) {
            User user=this.get(ID.getIdFromFileName(file.getName()));
            users.add(user);
        }
        logger.info("all users have been loaded");
        return users;
    }

    @Override
    public void update(User user) {
        synchronized (logger) {
            try {

//            File directory = new File("./src/main/resources/Info/Users");
                File directory = new File(infoConfig.getUsersDirectory());
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                File Data = new File(directory, user.getId() + ".json");
                if (!Data.exists())
                    Data.createNewFile();
                FileWriter writer = new FileWriter(Data);
                writer.write(gson.toJson(user));
                writer.flush();
                writer.close();
//                logger.info("user with id: " + user.getId() + " saved");

            } catch (IOException e) {
                logger.warn("user with id: " + user.getId() + " could not be saved in user update");
                e.printStackTrace();
            }

        }
    }


}
