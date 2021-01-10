package com.checkers;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MinMax {

    public MinMax() {
    }

    public List<BoardRow> getBestBoardRow(List<BoardRow> inputRows, Board board) {
        List<MoveAI> allPossibleMovesL1;
        List<MoveAI> allPossibleMovesL2;
        List<MoveAI> allPossibleMovesL3;
        List<BoardRow> hypotheticRow1;
        List<BoardRow> hypotheticRow2;
        List<BoardRow> hypotheticRow3;
        List<List<BoardRow>> hypotheticRowsL1 = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsL2 = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsL1WhiteMove;
        List<List<BoardRow>> hypotheticRowsL1NoWhiteMove = new ArrayList<>();
        List<List<BoardRow>> hypotheticRowsL2WhiteMove;
        List<List<BoardRow>> hypotheticRowsL3WhiteMove;
        Map<Integer,Integer> hypotheticRowsL1NoWhiteMoveMap = new HashMap<>();
        Map<Integer,Integer> hypotheticRowsL2Map = new HashMap<>();
        Map<Integer,Integer> hypotheticRowsL3Map = new HashMap<>();
        int scoreHypotheticRow;
        List<Integer> scoreHypotethicRows = new ArrayList<>();
        int IndexOfBestMovesL3;
        int counter1 = 0;
        int counter2 = 0;

        allPossibleMovesL1 = listAllPossibleMoves(copyListofBoardRow(inputRows), Colour.BLACK);
        for (MoveAI currentMove : allPossibleMovesL1) {
            hypotheticRow1 = movePlusBuildHypotheticalRows(currentMove, copyListofBoardRow(inputRows));
            hypotheticRowsL1NoWhiteMove.add(hypotheticRow1);
            hypotheticRowsL1WhiteMove = moveWhitePlusBoardBuild(hypotheticRow1);
            for (List<BoardRow> currentBoardRow : hypotheticRowsL1WhiteMove) {
                hypotheticRowsL1.add(currentBoardRow);
                hypotheticRowsL1NoWhiteMoveMap.put(counter1,counter2);
                counter1++;
           }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow1 : hypotheticRowsL1) {
            allPossibleMovesL2 = listAllPossibleMoves(copyListofBoardRow(currentBoardRow1), Colour.BLACK);
            for (MoveAI currentMove : allPossibleMovesL2) {
                hypotheticRow2 = movePlusBuildHypotheticalRows(currentMove, copyListofBoardRow(currentBoardRow1));
                hypotheticRowsL2WhiteMove = moveWhitePlusBoardBuild(hypotheticRow2);
                for (List<BoardRow> currentBoardRow2 : hypotheticRowsL2WhiteMove) {
                    hypotheticRowsL2.add(currentBoardRow2);
                    hypotheticRowsL2Map.put(counter1,counter2);
                    counter1++;
                }
            }
            counter2++;
        }

        counter1 = 0;
        counter2 = 0;
        for (List<BoardRow> currentBoardRow : hypotheticRowsL2) {
            allPossibleMovesL3 = listAllPossibleMoves(copyListofBoardRow(currentBoardRow), Colour.BLACK);
            for (MoveAI currentMove : allPossibleMovesL3) {
                hypotheticRow3 = movePlusBuildHypotheticalRows(currentMove, copyListofBoardRow(currentBoardRow));
                hypotheticRowsL3WhiteMove = moveWhitePlusBoardBuild(hypotheticRow3);
                for (List<BoardRow> currentBoardRow2 : hypotheticRowsL3WhiteMove) {
                    hypotheticRowsL3Map.put(counter1,counter2);
                    scoreHypotheticRow = scoreHypotheticRows(currentBoardRow2);
                    scoreHypotethicRows.add(scoreHypotheticRow);
                    counter1++;
                }
            }
            counter2++;
        }

        if (scoreHypotethicRows.size() == 0) {
            board.getGraphics().getGameMsg().setText("Computer can't move.\nGame over.");
            board.getGraphics().gameOver(board);
        }

        IndexOfBestMovesL3 = chooseIndexOfBestMoveL3(scoreHypotethicRows);
        counter1 = hypotheticRowsL3Map.get(IndexOfBestMovesL3);
        counter2 = hypotheticRowsL2Map.get(counter1);
        counter1 = hypotheticRowsL1NoWhiteMoveMap.get(counter2);

        return hypotheticRowsL1NoWhiteMove.get(counter1);
    }

    private List<MoveAI> listAllPossibleMoves(List<BoardRow> rows, Colour colorToCheck) {
        List<MoveAI> allPossibleMoves = new ArrayList<>();
        List<Figure> currentRow;
        int x;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getFigures();
            x = 0;
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    x++;
                    continue;
                }
                if (currentFigure.getColor() != colorToCheck) {
                    x++;
                    continue;
                }
                allPossibleMoves.addAll(listAllPossibleMovesForFigure(rows, x, y, currentFigure));
                x++;
            }
        }
        return allPossibleMoves;
    }

    private List<MoveAI> listAllPossibleMovesForFigure(List<BoardRow> rows, int colStart, int rowStart, Figure currentFigure) {
        List<MoveAI> allPossibleMovesForFigure = new ArrayList<>();

        for (int rowStop = 0; rowStop < 8; rowStop++) {
            for (int colStop = 0; colStop < 8; colStop++) {
                if (moveScheme(colStart, rowStart, colStop, rowStop, rows, currentFigure.getColor()))
                    allPossibleMovesForFigure.add(new MoveAI(colStart, rowStart, colStop, rowStop));
            }
        }
        return allPossibleMovesForFigure;
    }

    private boolean moveScheme(int colStart, int rowStart, int colStop, int rowStop, List<BoardRow> rows, Colour colorToCheck) {
        if (checkIfTargetFieldDuplicated(colStart, rowStart, colStop, rowStop)) return false;
        if (checkIfTargetFieldOccupied(colStop, rowStop, rows)) return false;

        if (getFigure(colStart, rowStart, rows) instanceof Pawn) {
            if (!movePawnJump(colStart, rowStart, colStop, rowStop, rows, false)) {
                return !checkPawnMovePossibility(colStart, rowStart, colStop, rowStop, rows, colorToCheck);
            }
        }

        if (getFigure(colStart, rowStart, rows) instanceof Queen) {
            if (checkQueenMoveLine(colStart, rowStart, colStop, rowStop))
                return false;
            return moveQueen(colStart, rowStart, colStop, rowStop, rows, false);
        }

        return true;
    }

    private Figure getFigure(int colStop, int rowStop, List<BoardRow> rows) {
        return rows.get(rowStop).getFigures().get(colStop);
    }

    private void setFigure(int colStop, int rowStop, Figure figure, List<BoardRow> rows) {
        rows.get(rowStop).getFigures().set(colStop, figure);
    }

    private boolean checkIfTargetFieldDuplicated(int colStart, int rowStart, int colStop, int rowStop) {
        return colStart == colStop && rowStart == rowStop;
    }

    private boolean checkIfTargetFieldOccupied(int colStop, int rowStop, List<BoardRow> rows) {
        return getFigure(colStop, rowStop, rows).getColor() != Colour.NONE;
    }

    private boolean movePawnJump(int colStart, int rowStart, int colStop, int rowStop, List<BoardRow> rows, boolean jump) {
        boolean result = pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart + 2, rowStart - 2, colStart + 1, rowStart - 1, rows, jump);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart + 2, rowStart + 2, colStart + 1, rowStart + 1, rows, jump);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart - 2, rowStart + 2, colStart - 1, rowStart + 1, rows, jump);
        result = result || pawnJumpDirection(colStart, rowStart, colStop, rowStop, colStart - 2, rowStart - 2, colStart - 1, rowStart - 1, rows, jump);
        return result;
    }

    private boolean pawnJumpDirection(int colStart, int rowStart, int colStop, int rowStop, int colToLand, int rowToLand, int colJumped, int rowJumped, List<BoardRow> rows, boolean jump) {
        if (colToLand == colStop && rowToLand == rowStop &&
                getFigure(colJumped, rowJumped, rows).getColor() != Colour.NONE &&
                getFigure(colJumped, rowJumped, rows).getColor() != getFigure(colStart, rowStart, rows).getColor()) {
            if (jump) {
                pawnJump(colStart, rowStart, colStop, rowStop, colJumped, rowJumped, rows);
            }
            return true;
        }
        return false;
    }

    private void pawnJump(int colStart, int rowStart, int colStop, int rowStop, int colJumped, int rowJumped, List<BoardRow> rows) {
        setFigure(colJumped, rowJumped, new None(), rows);
        setFigure(colStop, rowStop, new Pawn(getFigure(colStart, rowStart, rows).getColor()), rows);
        setFigure(colStart, rowStart, new None(), rows);
    }

    private boolean checkPawnMovePossibility(int colStart, int rowStart, int colStop, int rowStop, List<BoardRow> rows, Colour colorCheck) {
        if (!(colStart + 1 == colStop || colStart - 1 == colStop)) {
            return true;
        }
        if(colorCheck == Colour.BLACK) {
            return checkMoveDirection(colStart, rowStart, rowStop, Colour.BLACK, rowStart + 1, rows);
        } else {
            return checkMoveDirection(colStart, rowStart, rowStop, Colour.WHITE, rowStart - 1, rows);
        }
    }

    private boolean checkMoveDirection(int colStart, int rowStart, int rowStop, Colour currentColor, int rowToLand, List<BoardRow> rows) {
        return getFigure(colStart, rowStart, rows).getColor() == currentColor && rowToLand != rowStop;
    }

    private boolean checkQueenMoveLine(int colStart, int rowStart, int colStop, int rowStop) {
        return !(colStart - colStop == rowStart - rowStop || colStart - colStop == rowStop - rowStart ||
                colStop - colStart == rowStart - rowStop || colStop - colStart == rowStop - rowStart);
    }

    private boolean moveQueen(int colStart, int rowStart, int colStop, int rowStop, List<BoardRow> rows, boolean jump) {
        int colCheck = colStart;
        int rowCheck = rowStart;
        int lastColCheck;
        int lastRowCheck;
        boolean checkNextFieldAfterOpponent = false;

        boolean colRise = checkQueenMoveRise(colStart - colStop);
        boolean rowRise = checkQueenMoveRise(rowStart - rowStop);

        while (colCheck != colStop) {
            lastColCheck = colCheck;
            lastRowCheck = rowCheck;
            colCheck = queenMoveRepetition(colCheck, colRise);
            rowCheck = queenMoveRepetition(rowCheck, rowRise);

            if (checkQueenLineObstacle(colCheck, rowCheck, rows)) return false;
            if (getFigure(colCheck, rowCheck, rows).getColor() != Colour.NONE) {
                if (checkNextFieldAfterOpponent)
                    return false;
                checkNextFieldAfterOpponent = true;
                continue;
            }
            if (checkNextFieldAfterOpponent) {
                if (jump) {
                    setFigure(lastColCheck, lastRowCheck, new None(), rows);
                    setFigure(colCheck, rowCheck, new Queen(getFigure(colStart, rowStart, rows).getColor()), rows);
                    setFigure(colStart, rowStart, new None(), rows);
                }
                return true;
            }
        }
        if (jump)
            move(colStart, rowStart, colStop, rowStop, rows);
        return true;
    }

    private boolean checkQueenMoveRise(int result) {
        return result <= 0;
    }

    private void pawnBecomesQueen(int colStop, int rowStop, List<BoardRow> rows) {
        if (rowStop == 7)
            setFigure(colStop, rowStop, new Queen(getFigure(colStop, rowStop, rows).getColor()), rows);
    }

    private int queenMoveRepetition(int check, boolean rise) {
        if (rise)
            check++;
        else
            check--;
        return check;
    }

    private boolean checkQueenLineObstacle(int checkCol, int checkRow, List<BoardRow> rows) {
        return getFigure(checkCol, checkRow, rows).getColor() == Colour.BLACK;
    }

    private List<BoardRow> movePlusBuildHypotheticalRows(MoveAI currentMove, List<BoardRow> rows) {
        if (getFigure(currentMove.getColStart(), currentMove.getRowStart(), rows) instanceof Pawn) {
            if (!movePawnJump(currentMove.getColStart(), currentMove.getRowStart(), currentMove.getColStop(), currentMove.getRowStop(), rows, true)) {
                move(currentMove.getColStart(), currentMove.getRowStart(), currentMove.getColStop(), currentMove.getRowStop(), rows);
            }
            pawnBecomesQueen(currentMove.getColStop(), currentMove.getRowStop(), rows);
        }
        if (getFigure(currentMove.getColStart(), currentMove.getRowStart(), rows) instanceof Queen) {
            moveQueen(currentMove.getColStart(), currentMove.getRowStart(), currentMove.getColStop(), currentMove.getRowStop(), rows, true);
        }
        return rows;
    }

    private void move(int colStart, int rowStart, int colStop, int rowStop, List<BoardRow> rows) {
        setFigure(colStop, rowStop, getFigure(colStart, rowStart, rows), rows);
        setFigure(colStart, rowStart, new None(), rows);
    }

    private List<List<BoardRow>> moveWhitePlusBoardBuild(List<BoardRow> inputBoard) {
        List<List<BoardRow>> ListOfBoardsWithWhiteMove = new ArrayList<>();
        List<MoveAI> allPossibleMovesWhite;
        List<BoardRow> hypotheticRowWhite;

        allPossibleMovesWhite = listAllPossibleMoves(copyListofBoardRow(inputBoard), Colour.WHITE);
        for (MoveAI currentMove : allPossibleMovesWhite) {
            hypotheticRowWhite = movePlusBuildHypotheticalRows(currentMove, copyListofBoardRow(inputBoard));
            ListOfBoardsWithWhiteMove.add(hypotheticRowWhite);
        }
        return ListOfBoardsWithWhiteMove;
    }

    private int scoreHypotheticRows(List<BoardRow> rows) {
        List<Figure> currentRow;
        int queenValue = 3;
        int score;
        int scoreComputer = 0;
        int scorePlayer = 0;

        for (int y = 0; y < 8; y++) {
            currentRow = rows.get(y).getFigures();
            for (Figure currentFigure : currentRow) {
                if (currentFigure instanceof None) {
                    continue;
                }
                if (currentFigure.getColor() == Colour.WHITE) {
                    if (currentFigure instanceof Queen)
                        scorePlayer = scorePlayer + queenValue;
                    else
                        scorePlayer++;
                } else {
                    if (currentFigure instanceof Queen)
                        scoreComputer = scoreComputer + queenValue;
                    else
                        scoreComputer++;
                }
            }
        }
        score = scoreComputer - scorePlayer;
        return score;
    }

    private int chooseIndexOfBestMoveL3(List<Integer> scoreHypotethicRows) {
        int bestBoardRow = 0;
        boolean pickRandom = true;
        int random;

        while (pickRandom) {
            random = ThreadLocalRandom.current().nextInt(0, scoreHypotethicRows.size());
            if (scoreHypotethicRows.get(random).equals(Collections.max(scoreHypotethicRows))) {
                bestBoardRow = random;
                pickRandom = false;
            }
        }
        return bestBoardRow;
    }

    private static List<BoardRow> copyListofBoardRow(List<BoardRow> inputList) {
        List<BoardRow> outputList = new ArrayList<>();
        for (int counter = 0; counter < 8; counter++)
            outputList.add(new BoardRow());
        for (int counter1 = 0; counter1 < 8; counter1++)
            for (int counter2 = 0; counter2 < 8; counter2++) {
                Figure figure = createCopy(inputList.get(counter1).getFigures().get(counter2));
                outputList.get(counter1).setFigure(counter2, figure);
            }
        return outputList;
    }

    private static Figure createCopy(Figure figure) {
        if(figure instanceof Pawn)
            return new Pawn(figure.getColor());
        else if (figure instanceof Queen)
            return new Queen(figure.getColor());
        else
            return new None();
    }
}