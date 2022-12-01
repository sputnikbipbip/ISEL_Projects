package com.example.G18_design.model;

public abstract class Figure {

    private Point start;

    public Figure(int x, int y) {
        start = new Point(x,y);
    }

    public Point getStart() {
        return start;
    }

    public abstract String getLetter();

    public abstract Point getEnd();

    public abstract void setEnd(int x, int y);
}
