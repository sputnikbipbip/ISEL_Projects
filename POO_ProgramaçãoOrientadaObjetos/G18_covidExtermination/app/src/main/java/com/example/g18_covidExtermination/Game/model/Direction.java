package com.example.g18_covidExtermination.Game.model;

public enum Direction { LEFT(-1,0), RIGHT(+1,0), DOWN(0, +1);

    public final int Dx, Dy;

    Direction (int x, int y) {
        Dx = x;
        Dy = y;
    }

};

/*
public class Direction {
    public static final Direction LEFT = new Direction();
    public static final Direction RIGHT = new Direction();

    private Direction () {};
}
 */
