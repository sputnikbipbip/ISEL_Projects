package com.example.G18_design.model;

public class Pixel extends Figure {


    private Point end;

    public Pixel(int x, int y) {
        super(x, y);
    }

    @Override
    public String getLetter() {
        return "P";
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(int x, int y){
        this.end = new Point(x, y);
    };


}
