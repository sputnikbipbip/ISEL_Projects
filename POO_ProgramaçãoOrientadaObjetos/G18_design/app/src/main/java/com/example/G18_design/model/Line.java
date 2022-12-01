package com.example.G18_design.model;

public class Line extends Figure {

    private Point end;

    public Line(int x, int y) {
        super(x, y);
        setEnd(x, y);
    }

    @Override
    public String getLetter() {
        return "L";
    }

    @Override
    public Point getEnd() {
        return end;
    }

    @Override
    public void setEnd(int x, int y) {
        end = new Point(x, y);
    }
}
