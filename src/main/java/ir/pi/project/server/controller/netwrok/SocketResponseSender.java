package ir.pi.project.server.controller.netwrok;

import ir.pi.project.server.controller.ResponseSender;
import ir.pi.project.shared.event.Event;
import ir.pi.project.shared.gson.Deserializer;
import ir.pi.project.shared.gson.Serializer;
import ir.pi.project.shared.response.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketResponseSender implements ResponseSender {
    private final Socket socket;
    private final PrintStream printStream;
    private final Scanner scanner;
    private final Gson gson;

    public SocketResponseSender(Socket socket) throws IOException {
        this.socket = socket;
        this.scanner = new Scanner(socket.getInputStream());
        this.printStream = new PrintStream(socket.getOutputStream());
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Event.class, new Deserializer<>())
                .registerTypeAdapter(Response.class, new Serializer<>())
                .setLenient()
                .create();
    }

    @Override
    public Event getEvent() {

        while (true) {
            if(scanner.hasNextLine()) {
                String eventString = scanner.nextLine();
                return gson.fromJson(eventString, Event.class);
            }
        }

    }

    @Override
    public void sendResponse(Response response) {
        printStream.println(gson.toJson(response, Response.class));
    }

    @Override
    public void close() {
        try {
            printStream.close();
            scanner.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
