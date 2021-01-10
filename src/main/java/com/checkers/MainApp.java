package com.checkers;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.List;

public class MainApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Board board = new Board();

        board.initBoard();

        primaryStage.setScene(new Scene(board.getGraphics().getMainGrid(), 1350, 900, Color.CADETBLUE));
        primaryStage.setTitle("Checkers");
        primaryStage.setResizable(false);
        primaryStage.show();

        board.show(board.getGraphics().getBoardGrid());
        board.getGraphics().getGameMsg().setText("First move: WHITE.\nLet's start.");
        mouseCheck(board);
    }

    private static void mouseCheck(Board board) {
        board.getGraphics().getBoardGrid().setOnMouseClicked(event -> {
            int x = (int) (event.getX() / 70);
            int y = (int) (event.getY() / 70);
            mouseClick(x, y, board);
        });
    }

    private static void mouseClick(int x, int y, Board board) {
        if(board.getOldX() == -1) {
            board.setOldX(x);
            board.setOldY(y);
            markField(board, board.getOldX(), board.getOldY());
        } else {
            int oldX = board.getOldX();
            int oldY = board.getOldY();
            board.setOldX(-1);
            board.setOldY(-1);
            unmarkFields(board);
            if(board.moveScheme(oldX, oldY, x, y, board)) {
                board.show(board.getGraphics().getBoardGrid());
                doMove(board);
            } else {
                board.show(board.getGraphics().getBoardGrid());
                mouseCheck(board);
            }
        }
    }

    public static void markField(Board board, int x, int y) {
        Figure currentFigure = board.getFigure(x, y);
        currentFigure.setCheck(true);
        board.show(board.getGraphics().getBoardGrid());
    }

    public static void unmarkFields(Board board) {
        List<Figure> currentRow;

        for (int n = 0; n < 8; n++) {
            currentRow = board.getRows().get(n).getFigures();
            for (Figure currentFigure : currentRow) {
                currentFigure.setCheck(false);
            }
        }
        board.show(board.getGraphics().getBoardGrid());
    }

    public static void doMove(Board board) {
        if(board.checkIfGameEned(board.nextMove))
            gameOver(board);
        board.getGraphics().getGameMsg().setText(board.getGraphics().getGameMsg().getText() + "Next move: " + board.nextMove.toString());
        if(board.nextMove == Colour.WHITE)
            mouseCheck(board);
        else
            computer(board);
    }

    private static void computer(Board board) {
        board.getGraphics().getBoardGrid().setOnMouseClicked(null);
        board.getGraphics().getGameMsg().setText("Next move: " + board.nextMove.toString() + "\nI am thinking... This may take a while.");
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> {
            List<BoardRow> bestBoardRow = board.getMinMax().getBestBoardRow(board.getRows(), board);
            board.setRows(bestBoardRow);
            board.show(board.getGraphics().getBoardGrid());
            board.nextMove = Colour.WHITE;
            if(board.checkIfGameEned(board.nextMove))
                gameOver(board);
            board.getGraphics().getGameMsg().setText("Computer made a move.\nNext move: " + board.nextMove.toString());
            mouseCheck(board);
        });
        new Thread(sleeper).start();
    }

    private static void gameOver (Board board) {
        if(board.nextMove == Colour.WHITE)
            board.getGraphics().getGameMsg().setText("Computer wins!");
        else
            board.getGraphics().getGameMsg().setText("You win!");
        board.getGraphics().gameOver(board);
    }
}
