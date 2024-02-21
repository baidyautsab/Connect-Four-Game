package com.example.connectforegame;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {

    private static final int COLUMNS = 7;
    private static final int ROWS = 6;
    private static final double CIRCLE_DIAMETER = 53;
    private static final String discColor1 = "#6fc2e3";
    private static final String discColor2 = "#f078c0";

    private static String PLAYER_ONE = "Player One";
    private static String PLAYER_TWO = "Player Two";

    private boolean isPlayerOneTurn = true;

    //store the inserted discs in the array
    private Disc[][] insertedDiscsArray = new Disc[ROWS][COLUMNS]; // foe structural changes for the developer

    @FXML
    public Pane rootPane;

    @FXML
    public GridPane gridPane;

    @FXML
    public Pane filePane;

    @FXML
    public Pane gameOverviewPane;

    @FXML
    public VBox playerVBox;

    @FXML
    public Label playerInfoLabel;

    private boolean isAllowedToInsert = true; // for fix the multiple insert disc issue

    public void createPlayground(){
        Shape rectangleWithHoles = new Rectangle((COLUMNS + 1) * CIRCLE_DIAMETER, (ROWS + 1)* CIRCLE_DIAMETER); // creating the rectangle( game board ) with holes

        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLUMNS; col++){
                Circle circle = new Circle();
                circle.setRadius(CIRCLE_DIAMETER / 2);
                circle.setCenterX(CIRCLE_DIAMETER / 2); // set the position of circle in X axis
                circle.setCenterY(CIRCLE_DIAMETER / 2);
                circle.setSmooth(true); // create the smooth edges of the circle

                circle.setTranslateX(col * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4); // CIRCLE_DIAMETER + 5 for margin and 2nd one for padding
                circle.setTranslateY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

                rectangleWithHoles = Shape.subtract(rectangleWithHoles, circle); // from the rectangle subtract the circles
            }
        }

        rectangleWithHoles.setFill(Color.LIGHTSKYBLUE);
        gridPane.add(rectangleWithHoles, 0, 1);

        List<Rectangle> rectangleList = createClickableColumns();

        for (Rectangle rectangle: rectangleList) {
            gridPane.add(rectangle, 0, 1);
        }
    }

    private List<Rectangle> createClickableColumns(){

        List<Rectangle> rectangleList = new ArrayList<>();

        for ( int col = 0; col < COLUMNS; col++){

            Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, (ROWS + 1) * CIRCLE_DIAMETER);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (CIRCLE_DIAMETER + 5) +CIRCLE_DIAMETER / 4);

            // Mouse hover effect
            rectangle.setOnMouseEntered(mouseEvent -> rectangle.setFill(Color.valueOf("#eeeeee26")));
            rectangle.setOnMouseExited(mouseEvent -> rectangle.setFill(Color.TRANSPARENT));

            final int column = col;
            rectangle.setOnMouseClicked(mouseEvent -> {
                    if (isAllowedToInsert){
                        isAllowedToInsert = false; // when disc is being dropped then no more disc will be inserted
                        insertDisc(new Disc(isPlayerOneTurn), column);
            }
            });

            rectangleList.add(rectangle);
        }
        return rectangleList;
    }

    private void insertDisc(Disc disc, int column) {

        int row = ROWS - 1;
        while (row >= 0){
            if(insertedDiscsArray[row][column] == null)
                break;
            row--;
        }

        if(row < 0) // if column is full, cant insert
            return;

        insertedDiscsArray[row] [column] = disc; // for structural changes for developers
        gameOverviewPane.getChildren().add(disc);

        disc.setTranslateX(column * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);

        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
        translateTransition.setToY(row * (CIRCLE_DIAMETER + 5) + CIRCLE_DIAMETER / 4);
        final int currentRow = row;
        translateTransition.setOnFinished(actionEvent -> {

            isAllowedToInsert = true; // Finally, when disc is drooped allow next player to insert disc
            if(gameEnded(currentRow, column)){
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn; // toggle the player
            // toggle the label text
            playerInfoLabel.setText(isPlayerOneTurn? PLAYER_ONE : PLAYER_TWO);
        });
        translateTransition.play();
    }

    private void gameOver() {
        String winner = isPlayerOneTurn ? PLAYER_ONE : PLAYER_TWO;
        System.out.println("Winner is : " + winner);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Result");
        alert.setHeaderText("The Winner is " + winner);

        ButtonType yesBtn = new ButtonType("Yes");
        ButtonType noBtn = new ButtonType("No,Exit");
        alert.getButtonTypes().setAll(yesBtn, noBtn);

        Platform.runLater(()->{ // this code will be executed when the last transformation is done otherwise exception will occur
            Optional<ButtonType> btnClicked = alert.showAndWait();
            if ( btnClicked.isPresent() && btnClicked.get() == yesBtn){
                // if user chose Yes the game
                resetGame();
            }else {
                // if user chose No then exit the game
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        gameOverviewPane.getChildren().clear(); // Remove all Inserted Disc from Pane

        for (int row = 0; row < insertedDiscsArray.length; row++) { // Structurally make all elements of insertDiscArray null
            for (int col = 0; col < insertedDiscsArray[row].length; col++) {
                insertedDiscsArray[row][col] = null;
            }
        }
        isPlayerOneTurn = true; // Let player start the game
        playerInfoLabel.setText(PLAYER_ONE);

        createPlayground(); // Prepare a fresh playground
    }

    private boolean gameEnded(int row, int column) {
        // index of each element present in column [row] [column]
        List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)// range of row values = 0,1,2,3,4,5
                                    .mapToObj(r -> new Point2D(r, column)) //0,3 1,3 2,3 3,3 4,3 5,3 -->Point2D x,y
                                    .toList();

        List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)// range of column values = 0,1,2,3,4,5
                .mapToObj(col -> new Point2D(row, col))
                .toList();

        Point2D startPoint1 = new Point2D(row - 3, column + 3);
        List<Point2D> diagonalPoints = IntStream.rangeClosed(0, 6)
                                    .mapToObj(i -> startPoint1.add(i, -i))
                                    .toList();

        Point2D startPoint2 = new Point2D(row - 3, column - 3);
        List<Point2D> diagonal2Points = IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startPoint2.add(i, i))
                .toList();

        // now check the combination
        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
                            || checkCombinations(diagonalPoints) || checkCombinations(diagonal2Points);

        return isEnded;
    }

    private Disc getDiscIfPresent(int row, int column){ // To prevent ArrayIndexOutOfBoundException

        if (row >= ROWS || row < 0 || column >= COLUMNS || column < 0)
            return null;
        return insertedDiscsArray[row][column];
    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;

        for (Point2D point: points) {
            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.isPlayerOneMove == isPlayerOneTurn){
                chain++;

                if(chain == 4){
                    return true;
                }
            }else {
                chain= 0;
            }
        }
        return false;
    }

    private static class Disc extends Circle{
        private final boolean isPlayerOneMove;

        public Disc(boolean isPlayerOneMove){
            this.isPlayerOneMove = isPlayerOneMove;
            setRadius(CIRCLE_DIAMETER / 2);
            setFill(isPlayerOneMove? Color.valueOf(discColor1): Color.valueOf(discColor2));
            setCenterX(CIRCLE_DIAMETER / 2);
            setCenterY(CIRCLE_DIAMETER / 2);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}