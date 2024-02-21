package com.example.connectforegame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ConnectFour extends Application {

    private Controller controller;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ConnectFour.class.getResource("Game.fxml"));
        Pane rootNode = fxmlLoader.load();

        controller = fxmlLoader.getController();
        controller.createPlayground();

        MenuBar menuBar = createMenu(); // call the method and store in menuBer
        menuBar.prefWidthProperty().bind(stage.widthProperty()); // now whatever is the width of the stage is now width of the menuBar

        Pane menuPane = (Pane) rootNode.getChildren().get(0); // getting the 1st element of gridPane
        menuPane.getChildren().add(menuBar); // adding the menuBer in the pane

        Scene scene = new Scene(rootNode);

        stage.setTitle("Connect Four");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private MenuBar createMenu() {

        Menu fileMenu = new Menu("File");

        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(actionEvent -> controller.resetGame()); // call the reset method that present in the controller

        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(actionEvent -> controller.resetGame());

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(actionEvent -> exitGame());

        fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

        Menu helpMenu = new Menu("About");

        MenuItem aboutGame = new MenuItem("About the Game");
        aboutGame.setOnAction(actionEvent -> aboutConnectFour());

        SeparatorMenuItem separatorMenuItem1 = new SeparatorMenuItem();
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(actionEvent -> aboutMe());

        helpMenu.getItems().addAll(aboutGame, separatorMenuItem1, aboutMe);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, helpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About the Developer");
        alert.setHeaderText("About Me !");
        alert.setContentText("Hi . I am Utsab. I have done this project during the Internshala java course." +
                " I love to play games and I am very exited to share the game with you.");
        alert.show();
    }

    private void aboutConnectFour() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Connect Four Game");
        alert.setHeaderText("How to play?");
        alert.setContentText("Connect Four (also known as Connect 4, Four Up, Plot Four, Find Four, Captain's Mistress," +
                " Four in a Row, Drop Four, and Gravitrips in the Soviet Union) is a two-player connection rack game," +
                " in which the players choose a color and then take turns dropping colored tokens into a seven-column," +
                " six-row vertically suspended grid. The pieces fall straight down, occupying the lowest available space" +
                " within the column. The objective of the game is to be the first to form a horizontal, vertical, or diagonal" +
                " line of four of one's own tokens. Connect Four is a solved game. The first player can always win by playing the right moves.\n" +
                "\n" +
                "The game was first sold under the Connect Four trademark[10] by Milton Bradley in February 1974.");
        alert.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {

    }

    public static void main(String[] args) {
        launch();
    }
}