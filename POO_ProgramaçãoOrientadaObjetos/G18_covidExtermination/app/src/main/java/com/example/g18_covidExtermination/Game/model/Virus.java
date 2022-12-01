package com.example.g18_covidExtermination.Game.model;

public class Virus extends Piece {

    private int x;
    private int y;
    private char letter = '*';

    public Virus(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void incrementY() {
        ++y;
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
