package ir.pi.project.server.controller;


import java.util.Scanner;

public class OrderManager extends Thread{

    BotHandler botHandler;
    public OrderManager() {
        this.botHandler=new BotHandler();
        System.out.println("Enter bot jarURL to add:");
    }

    public void run(){
        Scanner scanner=new Scanner(System.in);
        while (true){
            getOrder(scanner);
        }
    }

    public void getOrder(Scanner scanner){
        if(scanner.hasNextLine()) {
            String jarURL = scanner.nextLine();
            try {
                botHandler.addBot(jarURL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
