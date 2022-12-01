package com.example.g18_covidExtermination.Game.view;

import android.graphics.Canvas;

import com.example.g18_covidExtermination.tile.Tile;

public class EmptyTile implements Tile {


    @Override
    public void draw(Canvas canvas, int side) {

    }

    @Override
    public boolean setSelect(boolean selected) {
        return false;
    }
}
