package com.example.g18_covidExtermination.Game.model;

import android.util.Log;

import java.util.LinkedList;

public class Level {

    private int levelNumber;
    private int height;
    private int width;
    private final Piece[][] board;
    private Piece hero;
    private LinkedList<Virus> virus = new LinkedList<>();

    public Level(int levelNumber, int height, int width) {
        this.levelNumber = levelNumber;
        this.height = height;
        this.width = width;
        board = new Piece[height][width];
    }

    public int getNumber() {
        return levelNumber;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public Piece getHero() {
        return hero;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public LinkedList<Virus> getList() {
        return virus;
    }

    public void put(int l, int c, char type) {
        switch (type) {
            case '@':
                hero = new Hero(l,c);
                board[c][l] = hero;
                break;
            case 'X':
                board[c][l] = new Wall();
                break;
            case '*':
                board[c][l] = new Virus(c,l);
                virus.add(new Virus(c,l));
                break;
            case 'V':
                board[c][l] = new Trash();
                break;
            case '.':
                board[c][l] = new Empty();
                break;
            default:
                Log.v("App","Something went wrong when constructing the viewBoard");
                break;
        }
    }
}
