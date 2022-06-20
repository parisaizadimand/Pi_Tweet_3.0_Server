package ir.pi.project.server.controller;

import ir.pi.project.server.db.Context;
import ir.pi.project.server.db.ID;
import ir.pi.project.server.model.Bot;
import ir.pi.project.shared.model.User;
import ir.pi.project.shared.util.Loop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;


public class BotHandler {

    Context context = new Context();
    static private final Logger logger = LogManager.getLogger(BotHandler.class);

    public BotHandler() {
        logger.info("starting bots");
        Loop botsLoop = new Loop(1, new Runnable() {
            @Override
            public void run() {
                startBots();
            }
        });
        botsLoop.start();
    }

    private void startBots() {
        try {
            for (Bot bot : context.Bots.all()) {
                URLClassLoader loader = new URLClassLoader(new URL[]{new URL(bot.getJarURL())});
                Class botClass = loader.loadClass("Main");
                Object instance = botClass.getConstructor().newInstance();
                botClass.getMethod("setId", int.class).invoke(instance, bot.getId());
                botClass.getMethod("start").invoke(instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBot(String jarURL) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        URLClassLoader loader = new URLClassLoader(new URL[]{new URL(jarURL)});
        Class botClass = loader.loadClass("Main");
        Object instance = botClass.getConstructor().newInstance();
        String username = (String) botClass.getMethod("getUsername").invoke(instance);


        int id = ID.newID();
        botClass.getMethod("setId", int.class).invoke(instance, id);


        User user = new User(id, null, null, username, null, "1");
        user.setPhoneNumber("1");
        user.setOnline(true);
        user.setEPBCanSee(false);

        context.Users.update(user);

        Bot bot = new Bot(jarURL, id);
        context.Bots.update(bot);


        botClass.getMethod("start").invoke(instance);

        logger.info("bot with id " + user.getId() + " added");
        System.out.println("bot successfully added!");

    }

}
