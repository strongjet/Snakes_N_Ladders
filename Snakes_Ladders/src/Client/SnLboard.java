package Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javax.swing.JOptionPane;

public class SnLboard extends Application {

    public SnL_Client networkClient;

    HashMap<String, Circle> userPiece;
    Pair<Integer, Integer> coordinates;
    public int gridSize;
    private int rows;
    private int columns;

    public Group tileGroup;

    public boolean myTurn;

    private int die;
    public static int onlinePlayer;
    int newPlayers;

    public SnLboard() {
        
        this.tileGroup = new Group();
        this.myTurn = false;
        userPiece = new HashMap<>();
    }

    private Parent createScene() {

        double windSizeX = (rows * gridSize) * 1.5;
        double windSizeY = (columns * gridSize) * 1.15;

        Pane root = new Pane();

        root.setPrefSize(windSizeX, windSizeY);
        root.getChildren().addAll(this.tileGroup);

        // ** Board Creation ** \\
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                Rectangle rectangle = setRectangle();

                rectangle.setTranslateX(j * gridSize);
                rectangle.setTranslateY(i * gridSize);

                this.tileGroup.getChildren().add(rectangle);

            }
        }

        // Snakes and Ladders Board (Image) \\       
        Image img_board = new Image("SnL_Board.jpeg");
        ImageView imageView = new ImageView();
        imageView.setImage(img_board);
        imageView.setFitWidth(gridSize * rows);
        imageView.setFitHeight(gridSize * columns);
        this.tileGroup.getChildren().add(imageView);

        // ** Player Dock ** \\       
        for (int j = 0; j < 6; j++) {

            Rectangle rectangle = setRectangle();

            int locationX = j * gridSize;
            int locationY = 10 * gridSize;
            rectangle.setTranslateX(locationX);
            rectangle.setTranslateY(locationY);

            this.tileGroup.getChildren().add(rectangle);

        }

        // Side Pannel TextArea \\
        double textboxWidth = windSizeX - (columns * gridSize) + 5;
        double textboxHeight = textboxWidth / 1.8;
        double borderWidth = (columns * gridSize) + 2;
        this.tileGroup.getChildren().add(setSidePannel(textboxWidth, textboxHeight, borderWidth));

        // Die + Roll Die Button \\
        setDie("0", borderWidth, textboxHeight);

        Button roll = new Button("Roll Die");
        roll.setTranslateX(borderWidth);
        roll.setTranslateY(borderWidth);
        roll.setMinWidth(textboxWidth);
        roll.setMinHeight(gridSize + 20);
        this.tileGroup.getChildren().add(roll);

        EventHandler<ActionEvent> event = (ActionEvent e) -> {

        };
        roll.setOnAction(event);

        return root;
    }

    private VBox setSidePannel(double textboxWidth, double textboxHeight, double borderWidth) {

        VBox pannel = new VBox();

        //// Set Instructions \\
        TextArea instructions = new TextArea();

        instructions.setWrapText(true);

        try {
            Scanner s = new Scanner(new File("./instructions.txt"));

            while (s.hasNext()) {
                if (s.hasNext()) {
                    instructions.appendText(s.next() + " ");
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println(ex);
        }
        instructions.setEditable(false);
        instructions.setPrefHeight(textboxHeight);
        instructions.setPrefWidth(textboxWidth);

        //// Set Chat \\
        TextArea chat = new TextArea();
        chat.setEditable(false);
        chat.setPrefHeight(textboxHeight);
        chat.setPrefWidth(textboxWidth);
        chat.appendText("--------------------------- Chat Client ---------------------------\n");

        TextArea input = new TextArea();
        input.setPrefHeight(2);
        input.setPrefWidth(textboxWidth);
        input.setWrapText(false);

        pannel.setPrefHeight(textboxHeight * 2.5);
        pannel.setPrefWidth(textboxWidth);
        pannel.setTranslateX(borderWidth);
        pannel.setTranslateY(0);

        pannel.getChildren().addAll(instructions, new Separator(), chat, input);

        return pannel;
    }

    private void setDie(String faces, double borderWidth, double textboxHeight) {

        Image img = new Image("/die/Die_" + faces + ".png");
        ImageView imageView = new ImageView();
        imageView.setImage(img);
        imageView.setFitHeight(230);
        imageView.setFitWidth(230);
        imageView.setTranslateX(borderWidth + 50);
        imageView.setTranslateY(textboxHeight * 2.3);
        this.tileGroup.getChildren().add(imageView);

    }

    public void setupUserPieces(String username, String skin, int position) {

        Circle circle = new Circle(gridSize / 2.5);
        Image image = new Image("/skins/" + skin + ".jpg");
        circle.setFill(new ImagePattern(image));
        positionCalculator(position);
        circle.setTranslateX(coordinates.getKey());
        circle.setTranslateY(coordinates.getValue());
        userPiece.put(username, circle);
        this.tileGroup.getChildren().add(circle);
        newPlayers += 1;

    }

    private Pair positionCalculator(int position) {

        String type = "R2L";
        int positionLevel = 0, x = 0, y;

        if ((position > 0 && position < 11) || (position > 20 && position < 31) || (position > 40 && position < 51)
                || (position > 60 && position < 71) || (position > 80 && position < 91)) {
            type = "L2R";
        }
        if (position > 0 && position < 11) {
            positionLevel = 9;
        } else if (position > 10 && position < 21) {
            positionLevel = 8;
        } else if (position > 20 && position < 31) {
            positionLevel = 7;
        } else if (position > 30 && position < 41) {
            positionLevel = 6;
        } else if (position > 40 && position < 51) {
            positionLevel = 5;
        } else if (position > 50 && position < 61) {
            positionLevel = 4;
        } else if (position > 60 && position < 71) {
            positionLevel = 3;
        } else if (position > 70 && position < 81) {
            positionLevel = 2;
        } else if (position > 80 && position < 91) {
            positionLevel = 1;
        } else if (position > 90 && position < 101) {
            positionLevel = 0;
        }
        if (type.contains("R2L")) {
            y = gridSize * Math.abs(10 - position) + gridSize / 2;
        } else {
            y = gridSize * position + gridSize / 2;
        }
        x = (gridSize * positionLevel + gridSize / 2) + 70 * newPlayers;

        coordinates = new Pair<>(x, y);
        return coordinates;
    }

    private Rectangle setRectangle() {

        Rectangle rectangle = new Rectangle();

        rectangle.setWidth(gridSize);
        rectangle.setHeight(gridSize);

        rectangle.setFill(Color.AQUA);
        rectangle.setStroke(Color.BLACK);

        return rectangle;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        int port = 8884;
        String hostname = "localhost";
        Object frame = null;
        String userName = JOptionPane.showInputDialog("Please enter your username: ");
        try {
            if (userName.isEmpty()) {
                userName = "Guest";
            }
        } catch (Exception ex) {
            System.out.println("exit");
            Platform.exit();
            System.exit(0);
        }
        networkClient = new SnL_Client(hostname, port, userName);
        networkClient.startClient();

        gridSize = networkClient.game.getGridSize();

        rows = networkClient.game.getRows();
        columns = networkClient.game.getColumns();
        die = networkClient.game.getDie();
        
        Scene scene = new Scene(createScene());

        primaryStage.setTitle(
                "Snake and Ladder Game");
        primaryStage.setResizable(
                false);
        primaryStage.setScene(scene);



        primaryStage.show();

        primaryStage.setOnCloseRequest(
                (WindowEvent t) -> {
                    try {
                        networkClient.disconnect();
                    } catch (Exception ex) {

                        Logger.getLogger(SnLboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Platform.exit();
                    System.exit(0);
                }
        );

    }

    public static void main(String[] args) {
        launch(args);
    }
}
