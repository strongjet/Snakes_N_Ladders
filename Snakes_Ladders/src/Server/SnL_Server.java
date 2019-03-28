package Server;

import Entities.Game;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import Entities.Player;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

public class SnL_Server {

    private final int port;
    private final Game game;
    private ArrayList<RunThread> activePlayers;

    public SnL_Server(int port, Game game) {
        this.port = port;
        activePlayers = new ArrayList<>();
        this.game = game;

    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                RunThread thread = new RunThread(socket);
                activePlayers.add(thread);
                thread.start();
            }

            //impliment a way to cleanup once server closes
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private synchronized List<Player> newUserBroadcast(Player player) {
        List<Player> exisitingPlayers = new ArrayList<>();
        exisitingPlayers.add(player);
        for (int x = activePlayers.size(); --x >= 0;) {
            RunThread clients = activePlayers.get(x);
            clients.playerState("newUser", player);
            exisitingPlayers.add(clients.player);
        }
        return exisitingPlayers;
    }

    private synchronized void userDisconnect(Player player, Thread thread) {
        activePlayers.remove(thread);
        for (int x = activePlayers.size(); --x >= 0;) {
            RunThread clients = activePlayers.get(x);
            if (clients.player == player) {
                activePlayers.remove(clients);
            } else {
                clients.playerState("disconnect", player);
            }
        }
    }

    class RunThread extends Thread {

        Socket socket;
        ObjectInputStream oInput;
        ObjectOutputStream oOutput;
        Player player = new Player();
        String username;
        Pair<String, Player> playerAction;

        RunThread(Socket socket) {
            this.socket = socket;
            try {
                oOutput = new ObjectOutputStream(socket.getOutputStream());
                oInput = new ObjectInputStream(socket.getInputStream());

                username = ((String) oInput.readObject()).toLowerCase();
                getUserSetup(username);

                oOutput.writeObject(this.player);
                oOutput.writeObject(newUserBroadcast(this.player));

                oOutput.writeObject(game);
                oOutput.flush();

            } catch (Exception ex) {
                closeStreams();
                ex.printStackTrace();
            }
        }

        public void run() {
            while (true) {
                String action;
                try {
                    playerAction = (Pair) oInput.readObject();
                    action = playerAction.getKey();
                } catch (Exception ex) {
                    break;
                }

                switch (action) {
                    case "move":

                        break;
                    case "update":

                        break;
                    case "disconnect":
                        userDisconnect(playerAction.getValue(), Thread.currentThread());
                        closeStreams();
                        break;
                    default:
                        break;
                }
            }
            closeStreams();
        }

        private boolean playerState(String type, Player player) {
            playerAction = new Pair<>(type, player);
            try {
                oOutput.writeObject(playerAction);
            } catch (Exception ex) {
                ex.printStackTrace();
                closeStreams();
            }
            return true;
        }

        private boolean getUserSetup(String username) {
            // Player setup //
            String profile = "./Profiles.csv";
            String csv = ",";
            String line;
            BufferedReader br = null;

            try {

                br = new BufferedReader(new FileReader(profile));
                String[] userProfile = null;

                while ((line = br.readLine()) != null) {
                    String[] user = line.split(csv);

                    if (username.equals(user[0])) {
                        userProfile = user;
                    }
                }

                if (userProfile != null) {
                    player.setScore(Integer.parseInt(userProfile[1]));
                    player.setSkin(userProfile[2]);
                } else {

                    player.setScore(0);
                    player.setSkin("default");
                    FileWriter pw = new FileWriter(profile, true);
                    pw.append(this.username);
                    pw.append(",");
                    pw.append("0");
                    pw.append(",");
                    pw.append("default");
                    pw.append("\r");
                    pw.flush();
                    pw.close();
                }
                player.setUsername(this.username);
                player.setPosition(0);

            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return true;
        }

        private void closeStreams() {
            try {
                if (oInput != null) {
                    oInput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (oOutput != null) {
                    oOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }

        }

    }

    public static void main(String[] args) {
        int port = 8884;

        String board = "";

        int gridSize = 70;
        int rows = 10;
        int columns = 10;
        int die = 6;

        Map<Integer, Integer> snake = new HashMap<>();
        Map<Integer, Integer> ladder = new HashMap<>();

        {
            snake.put(99, 54);
            snake.put(70, 55);
            snake.put(52, 42);
            snake.put(25, 2);
            snake.put(95, 72);

            ladder.put(6, 25);
            ladder.put(11, 40);
            ladder.put(60, 85);
            ladder.put(46, 90);
            ladder.put(17, 69);
        }

        Game game = new Game(board, snake, ladder, gridSize, rows, columns, die);

        SnL_Server server = new SnL_Server(port, game);
        server.startServer();
    }

}
