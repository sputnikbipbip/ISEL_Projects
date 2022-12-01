package com.example.g18_covidExtermination.Game.model;

import java.util.List;

public class Board {

    private int PanelHeight;
    private int PanelWidth;
    private Hero hero;
    private Piece[][] board;
    private List<Virus> virus;
    private HeroListener heroListener;
    private CovidListener covidListener;

    public Board(Level level) {
        board = level.getBoard();
        this.PanelHeight = level.getHeight();
        this.PanelWidth = level.getWidth();
        hero = (Hero)level.getHero();
        this.virus = level.getList();
    }

    public void updateBoard(Level level) {
        board = level.getBoard();
        this.PanelHeight = level.getHeight();
        this.PanelWidth = level.getWidth();
        hero = (Hero) level.getHero();
        this.virus = level.getList();
    }

    public boolean moveHero(Direction d) {
        moveCovid();
        int previousX = hero.getX();
        int previousY = hero.getY();
        if(hero.getX() + d.Dx > PanelWidth-1 || hero.getY() + d.Dy > PanelHeight-1 || hero.getX() + d.Dx < 0 || hero.getY() + d.Dy < 0) {
            return false;
        }
        if(!moveVerifier(board[hero.getX() + d.Dx][hero.getY() + d.Dy], d)) {
            return false;
        }
        hero.setCoordinates(hero.getX() + d.Dx, hero.getY() + d.Dy);
        if(heroListener !=null){
            heroListener.onHeroMoved(previousX, previousY, hero.getX(), hero.getY(), d);
        }
        return true;
    }

    public void moveCovid() {
        for(Virus v: virus) {
            if(board[v.getX()][v.getY() + 1] instanceof Empty && v.getY() < PanelHeight) {
                int previousY = v.getY();
                v.incrementY();
                if(covidListener != null)
                    covidListener.onCovidMoved(v.getX(), previousY, v.getX(),v.getY());
            }
            else if(board[v.getX()][v.getY() + 1] instanceof Trash) {
                board[v.getX()][v.getY()] = new Empty();
                virus.remove(v);
                if(covidListener != null)
                    covidListener.onCovidMoved(v.getX(),v.getY(),-1,-1);
            }
        }
    }

    public boolean moveVerifier(Piece c, Direction d) {
        if(hero.getX() <= 0 && d.Dx == -1 || d.Dx == +1 && hero.getX() >= PanelWidth-1)
            return false;
        if(d == Direction.DOWN) {
            if(hero.getY() >= PanelHeight - 1 || c instanceof Wall || c instanceof Virus)
                return false;
            return true;
        }
        return (c instanceof Empty || c instanceof Virus);
    }

    public void swap(int x, int y, int Dx, int Dy) {
        Piece aux = board[x][y];
        board[x][y] = board[Dx][Dy];
        board[Dx][Dy] = aux;
    }

    public List<Virus> getVirus() {
        return virus;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public int getPanelHeight() {
        return PanelHeight;
    }

    public int getPanelWidth() {
        return PanelWidth;
    }

    public void setOnMoveListener(HeroListener heroListener) {
        this.heroListener = heroListener;
    }

    public void setOnCovidListener(CovidListener covidListener) { this.covidListener = covidListener; }

}
