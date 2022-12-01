package com.example.G18_design.model;

public class Circle extends Figure{

    private int radius;

    public Circle(int x, int y) {
        super(x, y);
    }

    @Override
    public String getLetter() {
        return "C";
    }


    public int getRadius() {
        return radius;
    }

    //Uso quando faço load
    public void setRadius(int radius) {
        this.radius = radius;
    }

    //TODO: devia tirar este getEnd mas preciso dele nas outras figuras
    @Override
    public Point getEnd() {
        return null;
    }

    @Override
    public void setEnd(int x, int y) {
        double dx = getStart().x - x;
        double dy = getStart().y - y;
        radius = (int)Math.sqrt(
                (Math.pow(dx,2)
                + Math.pow(dy,2)));
    }
}
