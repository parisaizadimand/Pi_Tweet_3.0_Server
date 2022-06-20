package ir.pi.project.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.server.model.Bot;
import ir.pi.project.shared.model.GroupChat;
import ir.pi.project.shared.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;

public class BotDB implements DBSet<Bot>{
    static private final Logger logger= LogManager.getLogger(BotDB.class);
    private final InfoConfig infoConfig=new InfoConfig();
    @Override
    public Bot get(int id) {
        synchronized (logger) {
            try {
                File directory = new File(infoConfig.getBotsDirectory());
                File Data = new File(directory, id + ".json");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Data));
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Bot bot = gson.fromJson(bufferedReader, Bot.class);
                bufferedReader.close();
                return bot;
            } catch (IOException e) {
                logger.warn("bot with id: " + id + " could not be found in bot get");
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    public LinkedList<Bot> all() {
        LinkedList<Bot> bots=new LinkedList<>();
        File directory=new File(infoConfig.getBotsDirectory());
        for (File file:
                directory.listFiles()) {
            Bot bot=this.get(ID.getIdFromFileName(file.getName()));
            bots.add(bot);
        }
        return bots;
    }

    @Override
    public void update(Bot bot) {
        synchronized (logger) {
            try {
                File directory = new File(infoConfig.getBotsDirectory());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                File Data = new File(directory, bot.getId() + ".json");
                if (!Data.exists())
                    Data.createNewFile();
                FileWriter writer = new FileWriter(Data);
                writer.write(gson.toJson(bot));
                writer.flush();
                writer.close();

            } catch (IOException e) {
                logger.warn("bot with id: " + bot.getId() + " could not be saved in bot update");
                e.printStackTrace();
            }
        }
    }
}
