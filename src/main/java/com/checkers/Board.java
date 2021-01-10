package com.checkers;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

class Board {
    Graphics graphics = new Graphics(this);
    MinMax minMax = new MinMax();
    private List<BoardRow> rows = new ArrayList<>();
    public Colour nextMove = Colour.WHITE;
    private int oldX = -1;
    private int oldY = -1;

    public Graphics getGraphics() { return graphics; }
    public MinMax getMinMax() { return minMax; }
    public int getOldX() { return oldX; }
    public int getOldY() { return oldY; }
    public List<BoardRow> getRows() { return rows; }

    public void setOldX(int oldX) { this.oldX = oldX; }
    public void setOldY(int oldY) { this.oldY = oldY; }
    public void setRows(List<BoardRow> rows) { this.rows = rows; }

    Board() {
        for (int row = 0; row < 8; row++)
            rows.add(new BoardRow());
    }

    public Figure getFigure(int col, int row) {
        return rows.get(row).getFigures().get(col);
    }

    public void setFigure(int col, int row, Figure figure) {
        rows.get(row).getFigures().set(col, figure);
    }

        public void initBoard() {
        for (int row = 0; row < 8; row = row + 1) {
            for (int col = 0; col < 8; col = col + 1) {
                setFigure(col, row, new None());
            }
        }
        for (int col=1; col<8; col=col+2) {
            setFigure(col, 0, new Pawn(Colour.BLACK));
            setFigure(col, 2, new Pawn(Colour.BLACK));
            setFigure(col, 6, new Pawn(Colour.WHITE));
        }
        for (int col=0; col<8; col=col+2) {
            setFigure(col, 1, new Pawn(Colour.BLACK));
            setFigure(col, 7, new Pawn(Colour.WHITE));
            setFigure(col, 5, new Pawn(Colour.WHITE));
        }
    }

    public boolean moveScheme(int colStart, int rowStart, int colStop, int rowStop, Board board) {

        if (checkBoardRange(colStart, rowStart, colStop, rowStop, board.getGraphics().getGameMsg())) return false;
        if (checkMovePossibility(colStart, rowStart, board.getGraphics().getGameMsg())) return false;
        if (checkIfTargetFieldDuplicated(colStart, rowStart, colStop, rowStop, board.getGraphics().getGameMsg())) return false;
        if (checkIfTargetFieldOccupied(colStop, rowStop, board.getGraphics().getGameMsg())) return false;

        if(getFigure(colStart,rowStart) instanceof Pawn) {
            if(!movePawnJump(colStart, rowStart, colStop, rowStop, board.getGraphics().getGameMsg())) {
                if (checkPawnMovePosssibility(colStart, rowStart, colStop, rowStop, board.getGraphics().getGameMsg()))
                    return false;
                else
                    move(colStart, rowStart, colStop, rowStop, board);
            }
            pawnBecomesQueen(colStop, rowStop);
        }

        if(getFigure(colStart,rowStart) instanceof Queen) {
            if(checkQueenMoveLine(colStart, rowStart, colStop, rowStop, board.getGraphics().getGameMsg()))
                return false;
            if (!moveQueen(colStart, rowStart, colStop, rowStop, board))
                return false;
        }

        switchPlayer();
        return true;
    }

    public boolean checkIfGameEned(Colour checkColor) {
        for (int a = 0; a < 8; a++) {
            for (int b = 0; b < 8; b++) {
                if (rows.get(a).getFigures().get(b).getColor() == checkColor)
                    return false;
            }
        }
        return true;
    }

    public void show(GridPane boardGrid) {
        List<Figure> currentRow;
        String file = "";
        boolean blackField;
        int x;
        Rectangle marker = new Rectangle(70,70);
        marker.setFill(Paint.valueOf("#FFA700"));
        marker.setOpacity(0.3);

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getFigures();
            x = 0;
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    blackField = ((y==0) && ((x==0) || (x==2) || (x==4) || (x==6)));
                    blackField = blackField || ((y==1) && ((x==1) || (x==3) || (x==5) || (x==7)));
                    blackField = blackField || ((y==2) && ((x==0) || (x==2) || (x==4) || (x==6)));
                    blackField = blackField || ((y==3) && ((x==1) || (x==3) || (x==5) || (x==7)));
                    blackField = blackField || ((y==4) && ((x==0) || (x==2) || (x==4) || (x==6)));
                    blackField = blackField || ((y==5) && ((x==1) || (x==3) || (x==5) || (x==7)));
                    blackField = blackField || ((y==6) && ((x==0) || (x==2) || (x==4) || (x==6)));
                    blackField = blackField || ((y==7) && ((x==1) || (x==3) || (x==5) || (x==7)));
                    if(blackField)
                        file = "file:src/main/resources/black-field.png";
                    else
                        file = "file:src/main/resources/empty-white-field.jpg";
                }
                if (currentFigure instanceof Pawn) {
                    if (currentFigure.getColor() == Colour.WHITE) {
                        file = "file:src/main/resources/pawn-white-v4.jpg";
                    } else {
                        file = "file:src/main/resources/pawn-black-v4.jpg";
                    }
                }
                if (currentFigure instanceof Queen) {
                    if (currentFigure.getColor() == Colour.WHITE) {
                        file = "file:src/main/resources/queen-white-v5.jpg";
                    } else {
                        file = "file:src/main/resources/queen-black-v5.jpg";
                    }
                }
                if(currentFigure.getCheck()) {
                    boardGrid.add(marker, x, y, 1, 1);
                } else {
                    boardGrid.add(new ImageView(new Image(file, 70, 70, true, true)), x, y, 1, 1);
                }
                x++;
            }
        }
    }

    private boolean checkQueenMoveLine(int colStart, int rowStart, int colStop, int rowStop, Label gameMsg) {
        if(!(colStart - colStop == rowStart - rowStop || colStart - colStop == rowStop - rowStart ||
                colStop - colStart == rowStart - rowStop || colStop - colStart == rowStop - rowStart)) {
            gameMsg.setText("Move has to be diagonal!");
            return true;
        }
        return false;
    }

    private boolean moveQueen(int colStart, int rowStart, int colStop, int rowStop, Board board) {
        int checkCol = colStart;
        int checkRow = rowStart;
        int lastCheckCol;
        int lastCheckRow;
        boolean checkNextFieldAfterOpponent = false;

        boolean colRise = checkQueenMoveRise(colStart - colStop);
        boolean rowRise = checkQueenMoveRise(rowStart - rowStop);

        while(checkCol != colStop) {
            lastCheckCol = checkCol;
            lastCheckRow = checkRow;
            checkCol = queenMoveRepetition(checkCol, colRise);
            checkRow = queenMoveRepetition(checkRow, rowRise);

            if (checkQueenLineOstacle(checkCol, checkRow, board.getGraphics().getGameMsg())) return false;
            if(getFigure(checkCol,checkRow).getColor() != Colour.NONE) {
                if (checkNextFieldAfterOpponent) {
                    board.getGraphics().getGameMsg().setText("Jump over two figures impossible!");
                    return false;
                }
                checkNextFieldAfterOpponent = true;
                continue;
            }
            if(checkNextFieldAfterOpponent) {
                setFigure(lastCheckCol, lastCheckRow, new None());
                setFigure(checkCol, checkRow, new Queen(getFigure(colStart, rowStart).getColor()));
                setFigure(colStart, rowStart, new None());
                return true;
            }
        }
        move(colStart,rowStart,colStop,rowStop, board);
        return true;
    }

    private boolean checkQueenLineOstacle(int checkCol, int checkRow, Label gameMsg) {
        if(getFigure(checkCol,checkRow).getColor() == nextMove) {
            gameMsg.setText("Move blocked by one of your figures!");
            return true;
        }
        return false;
    }

    private int queenMoveRepetition(int check, boolean rise) {
        if (rise)
            check++;
        else
            check--;
        return check;
    }

    private boolean checkQueenMoveRise(int result) {
        return result <= 0;
    }

    private void pawnBecomesQueen(int colStop, int rowStop) {
        if(rowStop == 0)
            setFigure(colStop, rowStop, new Queen(getFigure(colStop, rowStop).getColor()));
    }

    private void switchPlayer() {
        if(nextMove == Colour.WHITE)
            nextMove = Colour.BLACK;
        else
            nextMove = Colour.WHITE;
    }

    private void move(int colStart, int rowStart, int colSTop, int rowStop, Board board) {
        setFigure(colSTop,rowStop,getFigure(colStart,rowStart));
        setFigure(colStart,rowStart,new None());
        board.getGraphics().getGameMsg().setText("Move done.");
    }

    private boolean checkPawnMovePosssibility(int colStart, int rowStart, int colStop, int rowStop, Label gameMsg) {
        if(!(colStart + 1 == colStop || colStart - 1 == colStop)) {
            gameMsg.setText("Move impossible - not diagonal or unauthorized jump!");
            return true;
        }
        return checkMoveDirection(colStart, rowStart, rowStop, rowStart - 1, gameMsg);
    }

    private boolean checkMoveDirection(int colStart, int rowStart, int rowStop, int rowToLand, Label gameMsg) {
        if (getFigure(colStart, rowStart).getColor() == Colour.WHITE && rowToLand != rowStop) {
            gameMsg.setText("Wrong move direction!");
            return true;
        }
        return false;
    }

    private boolean movePawnJump(int colStart, int rowStart, int colStop, int rowStop, Label gameMsg) {
        boolean result = pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart + 2, rowStart - 2, colStart + 1, rowStart - 1);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart+2, rowStart+2, colStart+1, rowStart+1);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart-2, rowStart+2, colStart-1, rowStart+1);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart-2, rowStart-2, colStart-1, rowStart-1);
        if(result)
            gameMsg.setText("One figure less:)");
        return result;
    }

    private boolean pawnJumpDirection(int colStart, int rowStart, int colStop, int rowStop, int colToLand, int rowToLand, int colJumped, int rowJamped) {
        if (colToLand == colStop && rowToLand == rowStop &&
                getFigure(colJumped, rowJamped).getColor() != Colour.NONE &&
                getFigure(colJumped, rowJamped).getColor() != getFigure(colStart, rowStart).getColor()) {
            pawnJump(colStart, rowStart, colStop, rowStop, colJumped, rowJamped);
            return true;
        }
        return false;
    }

    private void pawnJump(int colStart, int rowStart, int colStop, int rowStop, int colJumped, int rowJumped) {
        setFigure(colJumped, rowJumped, new None());
        setFigure(colStop, rowStop, new Pawn(getFigure(colStart, rowStart).getColor()));
        setFigure(colStart, rowStart, new None());
    }

    private boolean checkIfTargetFieldOccupied(int colStop, int rowStop, Label gameMsg) {
        if(getFigure(colStop,rowStop).getColor() != Colour.NONE) {
            gameMsg.setText("Target field already occupied!");
            return true;
        }
        return false;
    }

    private boolean checkIfTargetFieldDuplicated(int colStart, int rowStart, int colStop, int rowStop, Label gameMsg) {
        if(colStart == colStop && rowStart == rowStop) {
            gameMsg.setText("Start and stop field is the same!");
            return true;
        }
        return false;
    }

    private boolean checkMovePossibility(int colStart, int rowStart, Label gameMsg) {
        if(getFigure(colStart,rowStart).getColor() == Colour.BLACK) {
            gameMsg.setText("Trying to move opponent's figure!\nChange to whites.");
            return true;
        }
        else if(getFigure(colStart,rowStart).getColor() == Colour.NONE) {
            gameMsg.setText("This field is empty!");
            return true;
        }
        return false;
    }

    private boolean checkBoardRange(int colStart, int rowStart, int colStop, int rowStop, Label gameMsg) {
        if(colStart < 0 || colStart > 8 || rowStart < 0 || rowStart > 8 || colStop < 0 || colStop > 8 || rowStop < 0 || rowStop > 8) {
            gameMsg.setText("Field out of range!");
            return true;
        }
        return false;
    }
}
