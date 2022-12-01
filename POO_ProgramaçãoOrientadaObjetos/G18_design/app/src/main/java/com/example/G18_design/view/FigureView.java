package com.example.G18_design.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.G18_design.model.Figure;

public abstract class FigureView {

    protected Figure figure;
    protected Paint brush;

    public FigureView(Figure figure) {
        this.figure = figure;
        brush = new Paint();
        brush.setColor(Color.BLACK);
        brush.setStrokeWidth(5);
        brush.setStyle(Paint.Style.STROKE);
    }

    abstract void draw(Canvas c);
}
