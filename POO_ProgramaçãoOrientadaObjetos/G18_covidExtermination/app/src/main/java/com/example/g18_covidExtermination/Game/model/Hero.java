package com.example.g18_covidExtermination.Game.model;

public class Hero extends Piece {

    private char letter = '@';
    private int x;
    private int y;

    public Hero(int x, int y) {
        this.x = x;
        this.x = y;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public char getLetter() {
        return letter;
    }
}
