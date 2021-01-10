package com.checkers;

class Queen implements Figure {
    boolean checked = false;

    private final Colour color;

    Queen(Colour color) {
        this.color = color;
    }

    @Override
    public Colour getColor() {
        return color;
    }

    @Override
    public boolean getCheck() { return checked; }

    @Override
    public void setCheck(boolean checkResult) { this.checked = checkResult; }

    @Override
    public String toString() {
        return getColorSymbol() + "Q";
    }

    private String getColorSymbol() {
        return (color == Colour.WHITE) ? "w" : "b";
    }
}
