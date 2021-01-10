package com.checkers;

class Pawn implements Figure {
    boolean checked = false;

    private final Colour color;

    Pawn(Colour color) {
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
        return getColorSymbol() + "P";
    }

    private String getColorSymbol() {
        return (color == Colour.WHITE) ? "w" : "b";
    }
}
