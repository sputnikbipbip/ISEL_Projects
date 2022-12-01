package com.example.g18_covidExtermination.Game.view;

import android.content.Context;
import android.graphics.Canvas;

import com.example.g18_covidExtermination.tile.Img;

public abstract class ImgTile extends MasterTile {

    private Img img;

    public ImgTile(Context ctx, int imageID) {
        img = new Img(ctx, imageID);
    }

    @Override
    public void draw(Canvas canvas, int side) {
        img.draw(canvas, side, side, paint);
    }
}
