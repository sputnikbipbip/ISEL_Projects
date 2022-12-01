package com.example.G18_design.view;

import android.graphics.Canvas;

import com.example.G18_design.model.Pixel;

public class PixelView extends FigureView {

    public PixelView(Pixel pixel) {
        super(pixel);
    }

    @Override
    void draw(Canvas canvas) {
        canvas.drawPoint(this.figure.getStart().x,
                this.figure.getStart().y,
                this.brush);
    }
}
