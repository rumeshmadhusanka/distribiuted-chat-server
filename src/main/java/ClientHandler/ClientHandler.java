package ClientHandler;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import Constants.ChatServerConstants.ClientConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Messaging.Messaging;

public class ClientHandler extends Thread{

    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final Socket clientSocket;
    final Object lock;
    private boolean quitFlag;


    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.lock = new Object();
    }

    @Override
    public void run() {
        try {
            // Start client handler and wait for client to connect.
            logger.info("Client " + clientSocket.getInetAddress() + ":"+ clientSocket.getPort() + " connected.");
            System.out.println("Client" + clientSocket.getInetAddress() + ":"+ clientSocket.getPort() + " connected.");
            //Create Input for the connection
            InputStream inputFromClient = clientSocket.getInputStream();
            Scanner scanner = new Scanner(inputFromClient, String.valueOf(StandardCharsets.UTF_8));

            while(!quitFlag) {
                String line = scanner.nextLine();
                System.out.println(line);
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonPayload = (JSONObject) jsonParser.parse(line);
                resolveClientRequest(jsonPayload);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        System.out.println("Connection has ended");
    }

    public void resolveClientRequest(JSONObject jsonPayload) {
        String type = (String) jsonPayload.get("type");
        JSONObject response;

        try {
            switch (type) {
                case ClientConstants.TYPE_CREATE_ID:
                    response = new JSONObject();
                    String identity = (String) jsonPayload.get(ClientConstants.IDENTITY);
                    if ((identity.length() > 3) && (identity.length() <= 16) && Character.isLetter(identity.charAt(0))){
                        response.put(ClientConstants.TYPE, type);
                        response.put(ClientConstants.APPROVED, ClientConstants.TRUE);

                    }else{
                        response.put(ClientConstants.TYPE, type);
                        response.put(ClientConstants.APPROVED, ClientConstants.FALSE);
                    }
                    Messaging.respondClient(response, this.clientSocket);

                case ClientConstants.TYPE_CREATE_ROOM:
                    String roomId = (String) jsonPayload.get(ClientConstants.ROOM_ID);
                    //TODO: Implement new room logic
                    response = new JSONObject();
                    response.put(ClientConstants.TYPE, type);
                    response.put(ClientConstants.ROOM_ID, roomId);
                    response.put(ClientConstants.APPROVED, ClientConstants.TRUE);
                    Messaging.respondClient(response, this.clientSocket);

                case ClientConstants.TYPE_DELETE_ROOM:

                case ClientConstants.TYPE_JOIN_ROOM:

                case ClientConstants.TYPE_LIST:

                case ClientConstants.TYPE_MESSAGE:

                case ClientConstants.TYPE_MOVE_JOIN:

                case ClientConstants.TYPE_QUIT:
                    this.quitFlag = true;

                case ClientConstants.TYPE_WHO:

        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
