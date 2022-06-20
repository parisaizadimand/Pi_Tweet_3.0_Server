package ir.pi.project.server.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.pi.project.server.config.addresses.InfoConfig;
import ir.pi.project.shared.model.Tweet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.LinkedList;

public class TweetDB implements DBSet<Tweet>{

    static private final Logger logger= LogManager.getLogger(TweetDB.class);
    private InfoConfig infoConfig=new InfoConfig();

    @Override
    public Tweet get(int id) {
        synchronized (logger) {
            try {
//            File directory=new File("./src/main/resources/Info/Tweets");
                File directory = new File(infoConfig.getTweetsDirectory());
                File Data = new File(directory, id + ".json");
                BufferedReader bufferedReader = new BufferedReader(new FileReader(Data));
//                logger.info("tweet file with id: " + id + " opened");

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                Tweet tweet = gson.fromJson(bufferedReader, Tweet.class);
                bufferedReader.close();
                return tweet;
            } catch (Exception e) {
                System.out.println("haha");
                logger.warn("tweet with id: " + id + " could not be found in tweet get");
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public LinkedList<Tweet> all() {
        synchronized (logger) {

            LinkedList<Tweet> tweets = new LinkedList<>();
//        File directory=new File("./src/main/resources/Info/Tweets");
            File directory = new File(infoConfig.getTweetsDirectory());

            for (File file :
                    directory.listFiles()) {
                Tweet tweet = this.get(ID.getIdFromFileName(file.getName()));
                tweets.add(tweet);
            }
            logger.info("all tweets have been loaded");
            return tweets;
        }
    }

    @Override
    public void update(Tweet tweet) {
        synchronized (logger) {
            try {
//            File directory=new File("./src/main/resources/Info/Tweets");
                File directory = new File(infoConfig.getTweetsDirectory());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                File Data = new File(directory, tweet.getId() + ".json");
                if (!Data.exists())
                    Data.createNewFile();
                FileWriter writer = new FileWriter(Data);
                writer.write(gson.toJson(tweet));
                writer.flush();
                writer.close();
//                logger.info("tweet with id " + tweet.getId() + " saved");

            } catch (IOException e) {
                logger.warn("tweet with id: " + tweet.getId() + " could not be saved in tweet update");
                e.printStackTrace();
            }
        }
    }
}
