package ir.pi.project.server;


import ir.pi.project.server.controller.OrderManager;
import ir.pi.project.server.controller.SocketManager;
import ir.pi.project.server.db.MessageDB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class main {
    static private final Logger logger= LogManager.getLogger(main.class);


    public static void main(String[] args) {
        logger.info("server started");
        SocketManager socketManager = new SocketManager();
        socketManager.start();
        OrderManager orderManager=new OrderManager();
        orderManager.start();
    }
}
