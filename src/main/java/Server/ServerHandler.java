package Server;

import ClientHandler.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ServerHandler extends Thread {
    //TODO: Implement Server-server communication (Similar to client-server)

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final ServerSocket serverCoordSocket;

    public ServerHandler(ServerSocket serverCoordSocket) {
        this.serverCoordSocket = serverCoordSocket;
    }

    @Override
    public void run(){
        try{
            while(true){
                Socket serverSocket = serverCoordSocket.accept();
                InputStream inputFromClient = serverSocket.getInputStream();
                Scanner serverInputScanner = new Scanner(inputFromClient, String.valueOf(StandardCharsets.UTF_8));
                String line = serverInputScanner.nextLine();
                logger.debug("Received: " +line);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonPayload = (JSONObject) jsonParser.parse(line);
                resolveServerRequest(jsonPayload);
            }
        } catch (Exception e) {

        }
    }

    private void resolveServerRequest(JSONObject jsonPayload){

    }
}
