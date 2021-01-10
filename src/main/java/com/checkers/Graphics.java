package com.checkers;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.Optional;

public class Graphics {
    private final GridPane mainGrid;
    private final GridPane boardGrid;
    private final Label gameMsg;

    public GridPane getMainGrid() { return mainGrid; }
    public GridPane getBoardGrid() { return boardGrid; }
    public Label getGameMsg() { return gameMsg; }

    public Graphics(Board board) {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        Image imageback = new Image("file:src/main/resources/woodboard.jpg");
        BackgroundImage backgroundImage = new BackgroundImage(imageback, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImage);

        mainGrid = new GridPane();
        mainGrid.setAlignment(Pos.CENTER);
        mainGrid.setPadding(new Insets(20, 20, 20, 20));
        mainGrid.setHgap(20);
        mainGrid.setVgap(20);
        mainGrid.setBackground(background);

        ColumnConstraints mainGridLeftColumn = new ColumnConstraints();
        mainGridLeftColumn.setHalignment(HPos.CENTER);
        mainGridLeftColumn.setPrefWidth(355);
        mainGrid.getColumnConstraints().add(mainGridLeftColumn);

        ColumnConstraints mainGridMiddleColumn = new ColumnConstraints();
        mainGridMiddleColumn.setHalignment(HPos.CENTER);
        mainGridMiddleColumn.setPrefWidth(560);
        mainGrid.getColumnConstraints().add(mainGridMiddleColumn);

        ColumnConstraints mainGridRightColumn = new ColumnConstraints();
        mainGridRightColumn.setHalignment(HPos.CENTER);
        mainGridRightColumn.setPrefWidth(355);
        mainGrid.getColumnConstraints().add(mainGridRightColumn);

        RowConstraints mainGridTitleRow = new RowConstraints();
        mainGridTitleRow.setValignment(VPos.BOTTOM);
        mainGridTitleRow.setPrefHeight(100);
        mainGrid.getRowConstraints().add(mainGridTitleRow);

        RowConstraints mainGridMessageRow = new RowConstraints();
        mainGridMessageRow.setValignment(VPos.CENTER);
        mainGridMessageRow.setPrefHeight(120);
        mainGrid.getRowConstraints().add(mainGridMessageRow);

        RowConstraints mainGridBoardRow = new RowConstraints();
        mainGridBoardRow.setValignment(VPos.BOTTOM);
        mainGridBoardRow.setPrefHeight(560);
        mainGrid.getRowConstraints().add(mainGridBoardRow);


        boardGrid = new GridPane();
        boardGrid.setAlignment(Pos.TOP_CENTER);
        boardGrid.setPadding(new Insets(0, 0, 0, 0));
        boardGrid.setBorder(new Border(new BorderStroke(Color.WHITE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        GridPane titleGrid = new GridPane();
        titleGrid.setAlignment(Pos.BOTTOM_CENTER);
        titleGrid.setPadding(new Insets(0, 20, 0, 20));
//        titleGrid.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        GridPane whitePawnsGrid = new GridPane();
        whitePawnsGrid.setAlignment(Pos.BASELINE_LEFT);
        whitePawnsGrid.setPadding(new Insets(0, 0, 0, 0));
//        whitePawnsGrid.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        GridPane messageGrid = new GridPane();
        messageGrid.setAlignment(Pos.CENTER);
        messageGrid.setPadding(new Insets(20, 20, 20, 20));
//        messageGrid.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.TOP_RIGHT);
        buttonGrid.setPadding(new Insets(50, 20, 0, 20));
//        buttonGrid.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        buttonGrid.setHgap(20);

        GridPane blackPawnsGrid = new GridPane();
        blackPawnsGrid.setAlignment(Pos.BASELINE_LEFT);
        blackPawnsGrid.setPadding(new Insets(0, 0, 0, 0));
//        blackPawnsGrid.setBorder(new Border(new BorderStroke(Color.WHITESMOKE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        Text gameTitle = new Text("Checkers");
        gameTitle.setFill(new Color(1, 1, 1,0.6));
        gameTitle.setFont(Font.loadFont("file:src/main/resources/Fredericka_the_Great/FrederickatheGreat-Regular.ttf", 70));

        Text gameSubtitle = new Text("Simple game of checkers.");
        gameSubtitle.setFill(new Color(1, 1, 1,0.6));
        gameSubtitle.setFont(Font.loadFont("file:src/main/resources/Nanum_Pen_Script/NanumPenScript-Regular.ttf", 30));

        Button restart = new Button();
        restart.setText("Restart");
        restart.setOnAction((e) -> {
            Optional<ButtonType> result = confirm("Do you really want to restart?");
            if (result.get() == ButtonType.OK){
                board.initBoard();
                board.show(board.getGraphics().getBoardGrid());
                board.getGraphics().getGameMsg().setText("Let's play again. You are still playing with white figures.");
            }
        });

        Button quit = new Button();
        quit.setText("Quit");
        quit.setOnAction((e) -> {
            Optional<ButtonType> result = confirm("Do you really want to quit?");
            if (result.get() == ButtonType.OK){
                System.exit(0);
            }
        });

        gameMsg = new Label("");
        gameMsg.setTextFill(Color.rgb(255, 255, 255,1.0));
        gameMsg.setFont(Font.loadFont("file:src/main/resources/Nanum_Pen_Script/NanumPenScript-Regular.ttf", 26));

        mainGrid.add(titleGrid, 0, 0, 1, 1);
        titleGrid.add(gameTitle, 0,0, 1 ,1);
        titleGrid.add(gameSubtitle,0, 1,1 ,1);
        mainGrid.add(messageGrid, 1, 1, 1, 1);
        messageGrid.add(gameMsg, 0, 0, 3, 2);
        mainGrid.add(buttonGrid, 2, 0, 1, 1);
        buttonGrid.add(restart,2, 0, 1, 1);
        buttonGrid.add(quit,3, 0, 1, 1);
        mainGrid.add(whitePawnsGrid, 0, 2, 1, 1);
        mainGrid.add(boardGrid,1, 2, 1, 1);
        mainGrid.add(blackPawnsGrid, 2, 2, 1, 1);
    }

    private Optional<ButtonType> confirm(String headerText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(headerText);

        return alert.showAndWait();
    }

    private Optional<ButtonType> confirmOnlyRestart() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game over");
        alert.setHeaderText(null);
        alert.setContentText("Let's play again.");

        return alert.showAndWait();
    }

    public void gameOver(Board board) {
        Optional<ButtonType> result = confirmOnlyRestart();
        if (result.get() == ButtonType.OK){
            board.initBoard();
            board.show(board.getGraphics().getBoardGrid());
            board.nextMove = Colour.WHITE;
            board.getGraphics().getGameMsg().setText("Let's play again.\nYou're still playing with white figures. ");
        }
    }
}
