package com.checkers;

public class MoveAI {
    private final int colStart;
    private final int rowStart;
    private final int colStop;
    private final int rowStop;

    public MoveAI(int colStart, int rowStart, int colStop, int rowStop) {
        this.colStart = colStart;
        this.rowStart = rowStart;
        this.colStop = colStop;
        this.rowStop = rowStop;
    }

    public int getColStart() {
        return colStart;
    }

    public int getRowStart() {
        return rowStart;
    }

    public int getColStop() {
        return colStop;
    }

    public int getRowStop() {
        return rowStop;
    }
}
