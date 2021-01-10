package com.checkers;

import java.util.ArrayList;
import java.util.List;

class BoardRow {
    private final List<Figure> figures = new ArrayList<>();

    BoardRow() {
        for (int n = 0; n < 8; n++)
            figures.add(new None());
    }

    List<Figure> getFigures() {
        return figures;
    }

    void setFigure(int column, Figure figure) { this.figures.set(column, figure); }
}
