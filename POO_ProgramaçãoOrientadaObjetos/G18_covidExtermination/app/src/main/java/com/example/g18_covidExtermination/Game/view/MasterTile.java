package com.example.g18_covidExtermination.Game.view;

import android.graphics.Paint;

import com.example.g18_covidExtermination.tile.Tile;

public abstract class MasterTile implements Tile {

    protected static Paint paint = new Paint();

    @Override
    public boolean setSelect(boolean selected) {
        return false;
    }
}
