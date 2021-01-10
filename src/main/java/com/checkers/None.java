package com.checkers;

class None implements Figure {
    boolean checked = false;

    @Override
    public Colour getColor() {
        return Colour.NONE;
    }

    @Override
    public boolean getCheck() { return checked; }

    @Override
    public void setCheck(boolean checkResult) { this.checked = checkResult; }

    @Override
    public String toString() {
        return "  ";
    }
}
