package Client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import Entities.*;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;

public class SnL_Client {

    Socket socket;
    ObjectInputStream oInput;
    ObjectOutputStream oOutput;
    Player player;
    Game game;
    //SnLboard board = new SnLboard();

    private final int port;
    public List<Player> allPlayers = new ArrayList<>();
    private final String hostname;
    private final String username;
    Pair<String, Player> message;

    SnL_Client(String hostname, int port, String username) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;

    }

    public boolean startClient() {

        try {
            socket = new Socket(hostname, port);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        try {
            oInput = new ObjectInputStream(socket.getInputStream());
            oOutput = new ObjectOutputStream(socket.getOutputStream());

            oOutput.writeObject(username);
            player = (Player) oInput.readObject();

            allPlayers = (List<Player>) oInput.readObject();

            SnLboard.onlinePlayer = allPlayers.size();
            game = (Game) oInput.readObject();

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        new clientThread().start();

        return true;
    }

    void disconnect() {
        try {
            message = new Pair<>("disconnect", this.player);
            oOutput.writeObject(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class clientThread extends Thread {

        @Override
        public void run() {

            SnLboard board = new SnLboard();

            allPlayers.forEach((players) -> {
                String otherUsername = players.username;
                String skin = players.skin;
                int position = players.position;
                board.setupUserPieces(otherUsername, skin, position);
            });

            while (true) {

                try {
                    message = (Pair) oInput.readObject();
                } catch (Exception ex) {
                    System.out.println("User Disconnect");
                    break;
                }

                String action = message.getKey();
                switch (action) {
                    case "newUser":
                        allPlayers.add(message.getValue());
//                        for (int i = 0; i < allPlayers.size(); i++) {
//                            System.out.println(allPlayers.get(i).getUsername());
//                        }
                        SnLboard.onlinePlayer = allPlayers.size();
                        break;
                    case "move":
                        //move();
                        break;
                    case "disconnect":
                        for (Player player : allPlayers) {
                            if (player.username.equals(message.getValue().username)) {
                                allPlayers.remove(player);
                                break;
                            }
                        }
                        SnLboard.onlinePlayer = allPlayers.size();
                        break;
                    default:
                        break;

                }
            }
        }
    }

//    public static void main(String[] args) {
//        int port = 8884;
//        String hostname = "localhost";
//        String userName = "Anonymous";
//
//        SnL_Client client = new SnL_Client(hostname, port, userName);
//        client.startClient();
//
//    }
}
