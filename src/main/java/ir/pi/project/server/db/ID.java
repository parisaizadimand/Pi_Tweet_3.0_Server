package ir.pi.project.server.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class ID {
    static private final Logger logger= LogManager.getLogger(ID.class);

    public static int getIdFromFileName(String fileName){
        for(int i=0;i<5;i++){
            fileName=fileName.substring(0,fileName.length()-1);
        }
        int q=Integer.parseInt(fileName);
        return q;
    }

    public static int newID(){
        int s=0;
        try {
            File lastId = new File("./src/main/resources/lastId");
            Scanner sc = new Scanner(lastId);
            int q = sc.nextInt();
            s=q;
            FileOutputStream fout = new FileOutputStream(lastId, false);
            PrintStream out = new PrintStream(fout);
            q++;
            out.println(q);
            out.flush();
            out.close();


            logger.info("new ID generated");


        } catch (FileNotFoundException e) {
            logger.warn("New ID could not be made");
            e.printStackTrace();
        }
        return s;
    }


    public static int lastUsedId(){

            File lastId = new File("./src/main/resources/lastId");
        Scanner sc = null;
        try {
            sc = new Scanner(lastId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int q = sc.nextInt();
        q--;
            return q;

    }

}
