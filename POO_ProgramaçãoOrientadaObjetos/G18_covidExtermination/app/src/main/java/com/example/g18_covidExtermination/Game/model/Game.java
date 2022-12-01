package com.example.g18_covidExtermination.Game.model;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Game {

    private Level level;
    private Board board;
    private Scanner in;


    public Game(Scanner in) {
        this.in = in;
        Loader loader = new Loader(in);
        try {
            level = loader.load(1);
        } catch (Loader.LevelFormatException e) {
            e.printStackTrace();
        }
        board = new Board(level);
    }

    public void nextLevel() {
        Loader loader = new Loader(in);
        try {
            level = loader.load(level.getLevelNumber() + 1);
        } catch (Loader.LevelFormatException e) {
            e.printStackTrace();
        }
        board.updateBoard(level);
    }

    public void loadGame(InputStream is) throws IOException {
        Scanner input = new Scanner(is);
        Loader loader = new Loader(input);
        try {
            level = loader.load(level.getLevelNumber());
            board.updateBoard(level);
        } catch (Loader.LevelFormatException e) {
            e.printStackTrace();
        }
        is.close();
    }

    public void saveGame(OutputStream os, int level) {
        PrintWriter to = new PrintWriter(os);
        to.println("#"+level+" "+board.getPanelHeight()+" x "+board.getPanelWidth());
        for(int i = 0; i < getLevel().getHeight(); i++) {
            for (int j = 0; j < getLevel().getWidth(); j++) {
                to.print(board.getBoard()[j][i].getLetter());
            }
            to.println();
        }
        to.close();
    }

    public Board getBoard() {
        return board;
    }

    public Level getLevel() {
        return level;
    }

}
